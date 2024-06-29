package burp;

import UI.Tags;
import func.vulscan;
import utils.BurpAnalyzedRequest;
import utils.DomainNameRepeat;
import utils.UrlRepeat;
import yaml.YamlUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BurpExtender implements IBurpExtender, IScannerCheck, IContextMenuFactory {

    public static String Yaml_Path = System.getProperty("user.dir") + "/" + "Config_yaml.yaml";
    public IBurpExtenderCallbacks call;
    private DomainNameRepeat DomainName;
    public IExtensionHelpers help;
    public Tags tags;
    private UrlRepeat urlC;
    public Collection<String> history_url = new LinkedList<String>();
    public static String EXPAND_NAME = "Route Vulnerable Scan";
    public Config Config_l;
    public ExecutorService ThreadPool;
    public boolean Carry_head = false;
    public boolean on_off = false;
    public boolean Bypass = false;
    public boolean DomainScan = false;
    public static String Download_Yaml_protocol = "https";

    public static String VERSION = "1.5.4";
    public static String Download_Yaml_host = "raw.githubusercontent.com";
    public static int Download_Yaml_port = 443;
    public static String Download_Yaml_file = "/F6JO/RouteVulScan/main/Config_yaml.yaml";
    public Map<String, View> views;
    public JTextField Host_txtfield;




    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        if (!new File(Yaml_Path).exists()) {
            Map<String, Object> x = new HashMap<String, Object>();
            Collection<Object> list1 = new ArrayList<Object>();
            x.put("Load_List", list1);
            YamlUtil.writeYaml(x, Yaml_Path);
        }
        this.call = callbacks;
        this.help = call.getHelpers();
        this.DomainName = new DomainNameRepeat();
        this.urlC = new UrlRepeat();
        this.Config_l = new Config(this);
        this.tags = new Tags(callbacks, Config_l);
//        this.views = Bfunc.Get_Views();
        call.printOutput("@Info: Loading RouteVulScan success");
        call.printOutput("@Version: RouteVulScan " + VERSION);
        call.printOutput("@From: Code by F6JO");
        call.printOutput("@Github: https://github.com/F6JO/RouteVulScan");
        call.printOutput("");
        call.setExtensionName(EXPAND_NAME + " " + VERSION);
        call.registerScannerCheck(this);
        call.registerContextMenuFactory(this);


    }

    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        ArrayList<IScanIssue> IssueList = new ArrayList();
        if (on_off) {
            String re = Host_txtfield.getText().replace(".", "\\.").replace("*", ".*?");
            Pattern pattern = Pattern.compile(re);
            Matcher matcher = pattern.matcher(baseRequestResponse.getHttpService().getHost());
            if (matcher.find()) {
                this.ThreadPool = Executors.newFixedThreadPool((Integer) Config_l.spinner1.getValue());
                IHttpService Http_Service = baseRequestResponse.getHttpService();
                String Root_Url = Http_Service.getProtocol() + "://" + Http_Service.getHost() + ":" + String.valueOf(Http_Service.getPort());
                try {
                    URL url = new URL(Root_Url + this.help.analyzeRequest(baseRequestResponse).getUrl().getPath());
                    BurpAnalyzedRequest Root_Request = new BurpAnalyzedRequest(this.call, baseRequestResponse);
                    String Root_Method = this.help.analyzeRequest(baseRequestResponse.getRequest()).getMethod();
                    String New_Url = this.urlC.RemoveUrlParameterValue(url.toString());
                    if (this.urlC.check(Root_Method, New_Url)) {
                        return null;
                    }
                    new vulscan(this, Root_Request,null);
                    this.urlC.addMethodAndUrl(Root_Method, New_Url);
                    try {
                        this.DomainName.add(Root_Url);
                        return IssueList;
                    } catch (Throwable th) {
                        return IssueList;
                    }
                } catch (MalformedURLException e3) {
                    throw new RuntimeException(e3);
                }
            } else {
                return IssueList;
            }


        } else {
            return IssueList;
        }
    }

    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        return null;
    }

    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        return 0;
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menu = new ArrayList<JMenuItem>();
        JMenuItem one_menu = new JMenuItem("Send To RouteVulScan On Map");
        JMenuItem two_menu = new JMenuItem("Send To RouteVulScan and Head On Map");
        JMenuItem three_menu = new JMenuItem("Send To RouteVulScan On Url");
        JMenuItem four_menu = new JMenuItem("Send To RouteVulScan and Head On Url");

        one_menu.addActionListener(new Right_click_monitor(invocation, this, false, true));
        two_menu.addActionListener(new Right_click_monitor(invocation, this,true, true));
        three_menu.addActionListener(new Right_click_monitor(invocation, this,false,false));
        four_menu.addActionListener(new Right_click_monitor(invocation, this,true,false));

        menu.add(one_menu);
        menu.add(two_menu);
        menu.add(three_menu);
        menu.add(four_menu);

        return menu;
    }

    public void prompt(Component component,String message){
        if (component == null){
            component = this.tags.getUiComponent();
        }
        JOptionPane.showMessageDialog(component, message);
    }
}


class Right_click_monitor implements ActionListener {
    private IContextMenuInvocation invocation;
    private BurpExtender burp;

    private Boolean withHead;
    private Boolean withSiteMap;

    public Right_click_monitor(IContextMenuInvocation invocation, BurpExtender burp, Boolean withHead, Boolean withSiteMap) {
        this.invocation = invocation;
        this.burp = burp;
        this.withHead = withHead;
        this.withSiteMap = withSiteMap;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        burp.ThreadPool = Executors.newFixedThreadPool((Integer) burp.Config_l.spinner1.getValue());
        IHttpRequestResponse[] RequestResponses = invocation.getSelectedMessages();
        if (withHead) {
            JTextArea jTextArea = new JTextArea(1, 1);
            jTextArea.setLineWrap(false);
            List<String> headers = this.getHeaders(RequestResponses[0]);
            headers.remove(0);
            String headerText = "";
            for (String head : headers){
                headerText += head + "\n";
            }
            jTextArea.setText(headerText);

            JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            jSplitPane.setResizeWeight(0.95);
            jSplitPane.add(new JScrollPane(jTextArea));


            JSplitPane jSplitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            jSplitPane2.setResizeWeight(0.5);
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            jSplitPane2.add(okButton);
            jSplitPane2.add(cancelButton);

            jSplitPane.add(jSplitPane2);

            JFrame frame = new JFrame("Custom Request Header");
            frame.add(jSplitPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null); // 让窗口在屏幕中央显示
            frame.setVisible(true);

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                }
            });
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<String> headersText = parseHead(jTextArea.getText());
                    if (headersText == null){
                        burp.prompt(frame,"Wrong header!");
                        return;
                    }
                    frame.dispose();
                    for (IHttpRequestResponse baseRequestResponse : RequestResponses) {
                        if (withSiteMap){
                            try {
                                IHttpService Http_Service = baseRequestResponse.getHttpService();
                                IRequestInfo RequestInfo = burp.help.analyzeRequest(Http_Service, baseRequestResponse.getRequest());
                                String host_url = RequestInfo.getUrl().getProtocol() + "://" + RequestInfo.getUrl().getHost();
                                IHttpRequestResponse[] httpRequestResponses = burp.call.getSiteMap(host_url);
                                for (IHttpRequestResponse requestResponse : httpRequestResponses) {
                                    byte[] requestBytes = replaceHeader(requestResponse, headersText);
                                    BurpAnalyzedRequest Root_Request = new BurpAnalyzedRequest(burp.call, requestResponse);
                                    start_send send = new start_send(burp, Root_Request,requestBytes);
                                    send.start();
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        } else{
                            try {
                                byte[] requestBytes = replaceHeader(baseRequestResponse, headersText);
                                BurpAnalyzedRequest Root_Request = new BurpAnalyzedRequest(burp.call, baseRequestResponse);
                                start_send send = new start_send(burp, Root_Request,requestBytes);
                                send.start();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                }
            });

        } else {
            for (IHttpRequestResponse baseRequestResponse : RequestResponses) {
                if (withSiteMap){
                    try {
                        IHttpService Http_Service = baseRequestResponse.getHttpService();
                        IRequestInfo RequestInfo = burp.help.analyzeRequest(Http_Service, baseRequestResponse.getRequest());
                        String host_url = RequestInfo.getUrl().getProtocol() + "://" + RequestInfo.getUrl().getHost();
                        IHttpRequestResponse[] requestResponses = burp.call.getSiteMap(host_url);
                        for (IHttpRequestResponse requestResponse : requestResponses) {
                            BurpAnalyzedRequest Root_Request = new BurpAnalyzedRequest(burp.call, requestResponse);
                            start_send send = new start_send(burp, Root_Request,null);
                            send.start();
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }else{
                    try {
                        BurpAnalyzedRequest Root_Request = new BurpAnalyzedRequest(burp.call, baseRequestResponse);
                        start_send send = new start_send(burp, Root_Request,null);
                        send.start();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public byte[] replaceHeader(IHttpRequestResponse i, List<String> header) {
        List<String> headers = new ArrayList<>(header);

        IRequestInfo iRequestInfo = burp.help.analyzeRequest(i);
        iRequestInfo.getHeaders();
        headers.add(0, burp.help.analyzeRequest(i).getHeaders().get(0));

        return burp.help.buildHttpMessage(headers, new byte[]{});
    }

    public List<String> getHeaders(IHttpRequestResponse iHttpRequestResponse) {
        return this.burp.help.analyzeRequest(iHttpRequestResponse).getHeaders();
    }

    public static List<String> parseHead(String headerText) {
        if (headerText.equals("")) {
            return null;
        }
        List<String> rows = new ArrayList<>();
        for (String row : headerText.split("\n")) {
            if (!row.equals("")) {
                rows.add(row);
            }
        }
        if (rows.size() == 0) {
            return null;
        }
        return rows;
    }

}



class start_send extends Thread {
    private BurpExtender burp;
    private BurpAnalyzedRequest Root_Request;
    private byte[] request;

    public start_send(BurpExtender burp, BurpAnalyzedRequest Root_Request,byte[] request) {
        this.burp = burp;
        this.Root_Request = Root_Request;
        this.request = request;
    }

    public void run() {
        new vulscan(this.burp, this.Root_Request,this.request);
    }

}

