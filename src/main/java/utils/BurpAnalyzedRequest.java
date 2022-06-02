package utils;

import burp.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BurpAnalyzedRequest {
    private IBurpExtenderCallbacks callbacks;

    private CustomHelpers customHelpers;

    private IExtensionHelpers help;

    private List<IParameter> jsonParameters = new ArrayList<>();

    private IHttpRequestResponse requestResponse;

    public BurpAnalyzedRequest(IBurpExtenderCallbacks callbacks, IHttpRequestResponse requestResponse2) {
        this.callbacks = callbacks;
        this.help = this.callbacks.getHelpers();
        this.customHelpers = new CustomHelpers();
        this.requestResponse = requestResponse2;
        setJsonParameters();
    }

    public IHttpRequestResponse requestResponse() {
        return this.requestResponse;
    }

    public IRequestInfo analyzeRequest() {
        return this.help.analyzeRequest(this.requestResponse);
    }

    public boolean isRequestParameterContentJson() {
        CustomHelpers customHelpers2 = this.customHelpers;
        if (!CustomHelpers.isJson(getHttpRequestBody(requestResponse())) && getAllJsonParameters().isEmpty())
            return false;
        return true;
    }

    private String getHttpRequestBody(IHttpRequestResponse httpRequestResponse) {
        byte[] request = httpRequestResponse.getRequest();
        int httpBodyOffset = this.help.analyzeRequest(request).getBodyOffset();
        try {
            return new String(request, httpBodyOffset, request.length - httpBodyOffset, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setJsonParameters() {
        if (!analyzeRequest().getParameters().isEmpty())
            for (IParameter p : analyzeRequest().getParameters()) {
                if (p.getType() != 2 && p.getType() != 6 && p.getName() != null && !"".equals(p.getName())) {
                    CustomHelpers customHelpers2 = this.customHelpers;
                    if (CustomHelpers.isJson(this.help.urlDecode(p.getValue())))
                        this.jsonParameters.add(p);
                }
            }
    }

    public List<IParameter> getAllJsonParameters() {
        return this.jsonParameters;
    }
}


