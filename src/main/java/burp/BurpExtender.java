package burp;

import UI.Tags;
import func.vulscan;
import utils.BurpAnalyzedRequest;
import utils.DomainNameRepeat;
import utils.UrlRepeat;
import yaml.YamlUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BurpExtender implements IBurpExtender, IScannerCheck {

    public static String Yaml_Path = System.getProperty("user.dir") + "/" +"Config_yaml.yaml";
    public IBurpExtenderCallbacks call;
    private DomainNameRepeat DomainName;
    public IExtensionHelpers help;
    public Tags tags;
    private UrlRepeat urlC;
    public Collection<String> history_url = new LinkedList<String>();
    public static String EXPAND_NAME = "Route Vulnerable Scanning";
    public View view_class;
    public List<View.LogEntry> log;
    public Config Config_l;
    public ExecutorService ThreadPool;

    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.call = callbacks;
        this.help = call.getHelpers();
        this.view_class = new View();
        this.DomainName = new DomainNameRepeat();
        this.urlC = new UrlRepeat();
        this.log = this.view_class.log;
        Config_l = new Config(view_class,log);
        this.tags = new Tags(callbacks,Config_l);
        if (!new File(Yaml_Path).exists()){
            YamlUtil.init_Yaml(Yaml_Path);
        }
        Bfunc.show_yaml(view_class,log,Yaml_Path);
        call.printOutput("Loading RouteVulScan success");
        call.setExtensionName(EXPAND_NAME);
        call.registerScannerCheck(this);

    }

    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        this.ThreadPool = Executors.newFixedThreadPool((Integer) Config_l.spinner1.getValue());
        ArrayList<IScanIssue> IssueList = new ArrayList();
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
            new vulscan(this,Root_Request);
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
    }

    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        return null;
    }

    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        return 0;
    }
}


