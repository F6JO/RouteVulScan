package func;

import UI.Tags;
import burp.*;
import utils.BurpAnalyzedRequest;
import yaml.YamlUtil;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class vulscan {

    private IBurpExtenderCallbacks call;

    private BurpAnalyzedRequest Root_Request;

    private IExtensionHelpers help;
    public String Path_record;
    public BurpExtender burp;
    public IHttpService httpService;


    public vulscan(BurpExtender burp, BurpAnalyzedRequest Root_Request,byte[] request) {
        this.burp = burp;
        this.call = burp.call;
        this.help = burp.help;
        this.Root_Request = Root_Request;
        // 获取httpService对象
        if (request == null){
            request = this.Root_Request.requestResponse().getRequest();
        }
//        IRequestInfo iRequestInfo = help.analyzeRequest(request);
//        httpService = help.buildHttpService(iRequestInfo.getUrl().getHost(), iRequestInfo.getUrl().getPort(), iRequestInfo.getUrl().getProtocol());
        httpService = this.Root_Request.requestResponse().getHttpService();
        IRequestInfo analyze_Request = help.analyzeRequest(httpService, request);
        List<String> heads = analyze_Request.getHeaders();
        burp.ThreadPool = Executors.newFixedThreadPool((Integer) burp.Config_l.spinner1.getValue());


        // 判断请求方法为POST
        if (this.help.analyzeRequest(request).getMethod() == "POST")
            //将POST切换为GET请求
            request = this.help.toggleRequestMethod(request);
        // 获取所有参数
        IRequestInfo iRequestInfo = this.help.analyzeRequest(request);
        List<IParameter> Parameters = iRequestInfo.getParameters();
        // 判断参数列表不为空
        if (!Parameters.isEmpty())
            for (IParameter parameter : Parameters)
                // 删除所有参数
                request = this.help.removeParameter(request, parameter);

        // 创建新的请求类
//        IHttpRequestResponse newHttpRequestResponse = this.call.makeHttpRequest(httpService, request);
        IHttpRequestResponse newHttpRequestResponse = Root_Request.requestResponse();
        // 使用/分割路径
        IRequestInfo analyzeRequest = this.help.analyzeRequest(newHttpRequestResponse);
        List<String> headers = analyzeRequest.getHeaders();
        HashMap<String, String> headMap = vulscan.AnalysisHeaders(headers);
        String[] domainNames = vulscan.AnalysisHost(headMap.get("Host"));


        String[] paths = analyzeRequest.getUrl().getPath().split("\\?",2)[0].split("/");

        Map<String, Object> Yaml_Map = YamlUtil.readYaml(burp.Config_l.yaml_path);
        List<Map<String, Object>> Listx = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        if (paths.length == 0) {
            paths = new String[]{""};
        }
        List<String> Bypass_List = (List<String>) Yaml_Map.get("Bypass_List");
        if (burp.DomainScan) {
            LaunchPath(true, domainNames, Listx, newHttpRequestResponse, heads, Bypass_List);
        }
        LaunchPath(false,paths,Listx,newHttpRequestResponse,heads,Bypass_List);



    }

    private void LaunchPath(Boolean ClearPath_record ,String[] paths,List<Map<String, Object>> Listx,IHttpRequestResponse newHttpRequestResponse,List<String> heads,List<String> Bypass_List){
        this.Path_record = "";
        for (String path : paths) {
            if (ClearPath_record){
                this.Path_record = "";
            }
            if (path.contains(".") && path.equals(paths[paths.length - 1])) {
                break;
            }
//            this.burp.call.printOutput(this.Path_record);

            if (!path.equals("")) {
                this.Path_record = this.Path_record + "/" + path;
            }

            String url = this.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getProtocol() + "://" + this.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getHost() + ":" + this.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getPort() + String.valueOf(this.Path_record);

            boolean is_InList;
            synchronized (this.burp.history_url) {
                is_InList = !this.burp.history_url.contains(url);
            }


            if (is_InList) {
                synchronized (this.burp.history_url) {
                    this.burp.history_url.add(url);
                }
                for (Map<String, Object> zidian : Listx) {
                    this.burp.ThreadPool.execute(new threads(zidian, this, newHttpRequestResponse, heads, Bypass_List));
                }


                int whileSiz = 0;
                while (true) {
//                    this.burp.call.printError(String.valueOf(whileSiz));
                    if (whileSiz >= 10){
                        this.burp.ThreadPool.shutdownNow();
                        this.burp.ThreadPool = Executors.newFixedThreadPool((Integer) this.burp.Config_l.spinner1.getValue());
                        this.burp.call.printError("Timeout: " + url + "/*");
                        break;
                    }
                    // 防止线程混乱，睡眠3.1秒
                    try {
                        Thread.sleep(3100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (((ThreadPoolExecutor) this.burp.ThreadPool).getActiveCount() == 0) {
                        break;
                    }
                    whileSiz += 1;

                }


            }else {
                this.burp.call.printError("Skip: " + url + "/*");
            }


        }
    }


    public static void ir_add(Tags tag, String title, String method, String url, String StatusCode, String notes, String Size, IHttpRequestResponse newHttpRequestResponse) {
//        if (!tag.Get_URL_list().contains(url)) {
        tag.add(title, method, url, StatusCode, notes, Size, newHttpRequestResponse);
//        }
    }

    public static HashMap<String, String> AnalysisHeaders(List<String> headers){
        headers.remove(0);
        HashMap<String, String> headMap = new HashMap<String, String>();
        for (String i : headers){
            int indexLocation = i.indexOf(":");
            String key = i.substring(0,indexLocation).trim();
            String value = i.substring(indexLocation + 1).trim();
            headMap.put(key,value);
        }
        return headMap;

    }

    public static String[] AnalysisHost(String host){
        ArrayList<String> ExceptSubdomain = new ArrayList<String>(Collections.singletonList("www"));
        Pattern zhengze = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        Matcher pipei = zhengze.matcher(host);
        if (!pipei.find()){
            List<String> hostArray = new ArrayList<>(Arrays.asList(host.split("\\.")));
            if (ExceptSubdomain.contains(hostArray.get(0))){
                hostArray.remove(0);
            }
            if (hostArray.get(hostArray.size() - 1).equals("cn") && hostArray.get(hostArray.size() - 2).equals("com")){
                hostArray.remove(hostArray.size() - 1);
                hostArray.remove(hostArray.size() - 1);
//                hostArray.remove(hostArray.size() - 2);
            }else {
                hostArray.remove(hostArray.size() - 1);
            }
            return hostArray.toArray(new String[0]);
        }
        return new String[]{};
    }


}

