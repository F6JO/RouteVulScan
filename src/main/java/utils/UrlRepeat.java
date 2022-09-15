package utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UrlRepeat {
    private Map<String, Integer> MethodAndUrlMap = new HashMap<>();

    public Map<String, Integer> getRequestMethodAndUrlMap() {
        return this.MethodAndUrlMap;
    }

    public void addMethodAndUrl(String Method, String url) {
        if (Method == null || Method.length() <= 0)
            throw new IllegalArgumentException("Request method cannot be empty");
        if (url == null || url.length() <= 0)
            throw new IllegalArgumentException("Url cannot be empty");
        getRequestMethodAndUrlMap().put(String.valueOf(Method) + " " + url, Integer.valueOf(1));
    }

//    public void delMethodAndUrl(String Method, String url) {
//        if (Method != null && Method.length() > 0 && url != null && url.length() > 0)
//            getRequestMethodAndUrlMap().remove(String.valueOf(Method) + " " + url);
//    }

    public boolean check(String Method, String url) {
        if (getRequestMethodAndUrlMap().get(String.valueOf(Method) + " " + url) != null)
            return true;
        return false;
    }

    public String RemoveUrlParameterValue(String url) {
        try {
            String urlQuery = (new URL(url)).getQuery();
            if (urlQuery == null) {
//                Object obj = "";
                return url;
            }
            String newUrl = String.valueOf(url.replace(urlQuery, "")) + RemoveParameterValue(urlQuery);
            String str = newUrl;
            return newUrl;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String RemoveParameterValue(String urlQuery) {
        String parameter = "";
        String[] split = urlQuery.split("&");
        for (int i = 0; i < split.length; i++)
            parameter = String.valueOf(parameter) + split[i].split("=")[0] + "=&";
        return parameter.substring(0, parameter.length() - 1);
    }
}

