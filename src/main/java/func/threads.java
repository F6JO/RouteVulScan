package func;


import burp.IHttpRequestResponse;
import com.sun.jmx.snmp.tasks.Task;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class threads implements Task {
    private Map<String, Object> zidian;
    private vulscan vul;
    private IHttpRequestResponse newHttpRequestResponse;


    public threads(Map<String, Object> zidian, vulscan vul, IHttpRequestResponse newHttpRequestResponse) {
        this.zidian = zidian;
        this.vul = vul;
        this.newHttpRequestResponse = newHttpRequestResponse;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void run() {
        go(this.zidian, this.vul, this.newHttpRequestResponse);

    }

    private static void go(Map<String, Object> zidian, vulscan vul, IHttpRequestResponse newHttpRequestResponse) {

        String name = (String) zidian.get("name");
        String urll = (String) zidian.get("url");
        String re = (String) zidian.get("re");
        String info = (String) zidian.get("info");
        String state = (String) zidian.get("state");
//        newHttpRequestResponse.

        URL url = null;
        try {
            url = new URL(vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getProtocol(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getHost(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().getPort(), String.valueOf(vul.Path_record) + urll);
//            vul.burp.call.printOutput(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        boolean is_InList;
        synchronized (vul.burp.history_url) {
            is_InList = !vul.burp.history_url.contains(url.toString());
        }
        if (is_InList) {
            synchronized (vul.burp.history_url) {
                vul.burp.history_url.add(url.toString());
                vul.burp.call.printOutput(url.toString());
            }
            byte[] request = vul.burp.help.buildHttpRequest(url);
            newHttpRequestResponse = vul.burp.call.makeHttpRequest(vul.httpService, request);

            if (vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode() == Integer.parseInt(state)) {
                byte[] resp = newHttpRequestResponse.getResponse();
                Pattern re_rule = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
                Matcher pipe = re_rule.matcher(vul.burp.help.bytesToString(resp));
                String lang = String.valueOf(vul.burp.help.bytesToString(resp).length());
                if (pipe.find()) {
                    vulscan.ir_add(vul.burp.tags, name, vul.burp.help.analyzeRequest(newHttpRequestResponse).getMethod(), vul.burp.help.analyzeRequest(newHttpRequestResponse).getUrl().toString(), String.valueOf(vul.burp.help.analyzeResponse(newHttpRequestResponse.getResponse()).getStatusCode()) + " ", info,lang, newHttpRequestResponse);
                }
            }
        } else {
            vul.burp.call.printError("Skip: " + url.toString());
        }


    }


}


