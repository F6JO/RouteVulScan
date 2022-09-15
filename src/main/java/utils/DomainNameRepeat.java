package utils;

import java.util.HashMap;
import java.util.Map;

public class DomainNameRepeat {
    private Map<String, Integer> domainNameMap = new HashMap<>();

    public Map<String, Integer> getDomainNameMap() {
        return this.domainNameMap;
    }

    public void add(String domainName) {
        if (domainName == null || domainName.length() <= 0)
            throw new IllegalArgumentException("Domain name cannot be empty");
        getDomainNameMap().put(domainName, Integer.valueOf(1));
    }

//    public void del(String domainName) {
//        if (getDomainNameMap().get(domainName) != null)
//            getDomainNameMap().remove(domainName);
//    }
//
//    public boolean check(String domainName) {
//        if (getDomainNameMap().get(domainName) != null)
//            return true;
//        return false;
//    }
}

