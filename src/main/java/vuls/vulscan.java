package vuls;

import burp.*;
import utils.BurpAnalyzedRequest;
import yaml.YamlUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class vulscan {
    private Date startDate = new Date();

    private IBurpExtenderCallbacks call;

    private BurpAnalyzedRequest Root_Request;

    private IExtensionHelpers help;


    public vulscan(IBurpExtenderCallbacks call, BurpAnalyzedRequest Root_Request, Tags tag, Collection<String> history_url,Config Config_l) {
        this.call = call;
        this.help = call.getHelpers();

        this.Root_Request = Root_Request;
        // 获取httpService对象
        IHttpService httpService = this.Root_Request.requestResponse().getHttpService();
        byte[] request = this.Root_Request.requestResponse().getRequest();
        // 判断请求方法为POST
        if (this.help.analyzeRequest(request).getMethod() == "POST")
            //将POST切换为GET请求
            request = this.help.toggleRequestMethod(request);
        // 获取所有参数
        List<IParameter> Parameters = this.help.analyzeRequest(request).getParameters();
        // 判断参数列表不为空
        if (!Parameters.isEmpty())

            for (IParameter parameter : Parameters)
                // 删除所有参数
                request = this.help.removeParameter(request, parameter);
        // 创建新的请求类
        IHttpRequestResponse newHttpRequestResponse = this.call.makeHttpRequest(httpService, request);
        // 使用/分割路径
        String[] paths = this.help.analyzeRequest(newHttpRequestResponse).getUrl().getPath().split("/");

        String Path_record = "";

        Map<String, Object> Yaml_Map = YamlUtil.readYaml(Config_l.yaml_path);
        List<Map<String, Object>> Listx = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        for (String path:paths) {
            if (!path.contains(".")){
                if (!path.equals("")){
                    Path_record = Path_record + "/" + path;
                }

                URL url = null;

                for (Map<String, Object> zidian : Listx) {
                    String name = (String) zidian.get("name");
                    String urll = (String) zidian.get("url");
                    String re = (String) zidian.get("re");
                    String info = (String) zidian.get("info");
                    String state = (String) zidian.get("state");
                    try {
                        url = new URL(this.help.analyzeRequest(newHttpRequestResponse).getUrl().getProtocol(), this.help.analyzeRequest(newHttpRequestResponse).getUrl().getHost(), this.help.analyzeRequest(newHttpRequestResponse).getUrl().getPort(), String.valueOf(Path_record) + urll);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (!history_url.contains(url.toString())) {
                        history_url.add(url.toString());
                        call.printOutput(url.toString());
                        request = this.help.buildHttpRequest(url);
                        newHttpRequestResponse = this.call.makeHttpRequest(httpService, request);

                        if (this.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode() == Integer.parseInt(state)) {
                            byte[] resp = newHttpRequestResponse.getResponse();
                            Pattern re_rule = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
                            Matcher pipe = re_rule.matcher(this.help.bytesToString(resp));
                            if (pipe.find()) {
                                ir_add(tag, name, this.help.analyzeRequest(newHttpRequestResponse).getMethod(), this.help.analyzeRequest(newHttpRequestResponse).getUrl().toString(), String.valueOf(this.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode()) + " ", info, newHttpRequestResponse);
                            }
                        }
                    } else {
                        call.printError(url.toString());
                    }

                }




        }

        }
    }

    private void ir_add(Tags tag, String title, String method, String url, String StatusCode, String notes, IHttpRequestResponse newHttpRequestResponse) {
        if (!tag.Get_URL_list().contains(url)) {
            tag.add(title, method, url, StatusCode, notes, newHttpRequestResponse);
        }
    }


}

