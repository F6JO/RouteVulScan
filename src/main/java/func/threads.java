package func;


import burp.Bfunc;
import burp.IExtensionHelpers;
import burp.IHttpRequestResponse;


import com.sun.jmx.snmp.tasks.Task;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class threads implements Task {
    private Map<String, Object> zidian;
    private vulscan vul;
    private IHttpRequestResponse newHttpRequestResponse;
    private List<String> heads;
    private List<String> Bypass_List;

    public threads(Map<String, Object> zidian, vulscan vul, IHttpRequestResponse newHttpRequestResponse, List<String> heads, List<String> Bypass_List) {
        this.zidian = zidian;
        this.vul = vul;
        this.newHttpRequestResponse = newHttpRequestResponse;
        this.heads = heads;
        this.Bypass_List = Bypass_List;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run() {
        go(this.zidian, this.vul, this.newHttpRequestResponse, this.heads, this.Bypass_List);

    }

    private static void go(Map<String, Object> zidian, vulscan vul, IHttpRequestResponse newHttpRequestResponse, List<String> heads, List<String> Bypass_List) {

        String name = (String) zidian.get("name");
        boolean loaded = (boolean) zidian.get("loaded");
        String urll = Bfunc.ProcTemplateLanguag((String) zidian.get("url"), newHttpRequestResponse, vul, false);
        String re = Bfunc.ProcTemplateLanguag((String) zidian.get("re"), newHttpRequestResponse, vul, true);
        String info = (String) zidian.get("info");
//        String state = (String) zidian.get("state");
        Collection<Integer> states = Bfunc.StatusCodeProc((String) zidian.get("state"));

        if (loaded) {
            URL url = null;
            try {
                url = new URL(vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getProtocol(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getHost(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getPort(), String.valueOf(vul.Path_record) + urll);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
//            boolean is_InList;
//            synchronized (vul.burp.history_url) {
//                is_InList = !vul.burp.history_url.contains(url.toString());
//            }
//            if (is_InList) {
//            synchronized (vul.burp.history_url) {
//                vul.burp.history_url.add(url.toString());
//            }
            byte[] request = vul.burp.help.buildHttpRequest(url);
            // 添加head
            if (vul.burp.Carry_head) {
                synchronized (heads) {
                    heads.remove(0);
                    heads.add(0, vul.burp.help.analyzeRequest(request).getHeaders().get(0));
                    request = vul.burp.help.buildHttpMessage(heads, new byte[]{});
                }
            }
            if ("POST".equals(zidian.get("method"))) {
                request = vul.burp.help.toggleRequestMethod(request);
            }


            newHttpRequestResponse = vul.burp.call.makeHttpRequest(vul.httpService, request);


            // 是否匹配成功
            boolean IFconform = true;
//                if (vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode() == Integer.parseInt(state)) {

            Integer stat = 0;
            if (newHttpRequestResponse.getResponse() == null){
                return;
            }

            if (states.contains(new Integer(vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode()))) {
                byte[] resp = newHttpRequestResponse.getResponse();
                Pattern re_rule = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
                Matcher pipe = re_rule.matcher(vul.burp.help.bytesToString(resp));
                String lang = String.valueOf(vul.burp.help.bytesToString(resp).length());
                if (pipe.find()) {
                    synchronized(vul){
                        vulscan.ir_add(vul.burp.tags, name, vul.burp.help.analyzeRequest(newHttpRequestResponse).getMethod(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().toString(), String.valueOf(vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode()) + " ", info, lang, newHttpRequestResponse);
                        IFconform = false;
                    }


                }
            }
//           匹配不成功则匹配bypass
            if (IFconform) {
                if (vul.burp.Bypass) {
                    for (String i : Bypass_List) {
                        byte[] newRequest = threads.edit_Bypass_request(vul.burp.help, request, i,urll);
                        newHttpRequestResponse = vul.burp.call.makeHttpRequest(vul.httpService, newRequest);
//                            if (vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode() == Integer.parseInt(state)) {
                        if (states.contains(vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode())) {
                            byte[] resp = newHttpRequestResponse.getResponse();
                            Pattern re_rule = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
                            Matcher pipe = re_rule.matcher(vul.burp.help.bytesToString(resp));
                            String lang = String.valueOf(vul.burp.help.bytesToString(resp).length());
                            if (pipe.find()) {
                                synchronized(vul) {
                                    vulscan.ir_add(vul.burp.tags, name, vul.burp.help.analyzeRequest(newHttpRequestResponse).getMethod(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().toString(), String.valueOf(vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode()) + " ", info, lang, newHttpRequestResponse);
                                    break;
                                }
                            }
                        }

                    }

                }
            }

            synchronized (vul.burp.call) {
                    vul.burp.call.printOutput(url.toString());
            }


//            } else {
////                vul.burp.call.printError("Skip: " + url.toString());
//            }

        }

    }

    private static byte[] edit_Bypass_request(IExtensionHelpers help, byte[] request, String str, String payPath) {

        String requests = help.bytesToString(request);
        String[] rows = requests.split("\r\n");
        String path = rows[0].split(" ")[1];
        String prefix = "";
        if (path.contains("http://")) {
            prefix = "http://";
            path = path.replace("http://", "");
        }
        if (path.contains("https://")) {
            path = path.replace("http://", "");
            prefix = "https://";
        }

        String newpath = path.replace(payPath,"") + payPath.replace("/", "/" + str + "/");
//        String newpath = path.replace("/", "/" + str + "/");
        if (path.endsWith("/")) {
            newpath = newpath.substring(0, newpath.lastIndexOf(str + "/"));
//            newpath = newpath.substring(0, newpath.lastIndexOf("/" + str + "/"));
        }
        newpath = prefix + newpath;
        String row1 = rows[0].split(" ")[0] + " " + newpath + " " + rows[0].split(" ")[2];
        String newRequest = requests.replace(rows[0], row1);
        return help.stringToBytes(newRequest);
    }


}



