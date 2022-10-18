package burp;


import yaml.YamlUtil;

import javax.swing.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Bfunc {

    public static Map<String, View> Get_Views() {
        Map<String, View> views = new Hashtable<String, View>();
        Map<String, Object> jieguo = YamlUtil.readYaml(BurpExtender.Yaml_Path);
        List<Map<String, Object>> rule_list = (List<Map<String, Object>>) jieguo.get("Load_List");
        for (Map<String, Object> zidian : rule_list) {
            String type = (String) zidian.get("type");
            String id = String.valueOf(zidian.get("id"));
            boolean loaded = Boolean.parseBoolean(String.valueOf(zidian.get("loaded")));
            String name = (String) zidian.get("name");
            String url = (String) zidian.get("url");
            String re = (String) zidian.get("re");
            String info = (String) zidian.get("info");
            String state = (String) zidian.get("state");
            String method = (String) zidian.get("method");
            if (views.containsKey(type)) {
                View view_one = views.get(type);
                view_one.log.add(new View.LogEntry(id, type, loaded, name, method, url, re, info, state));
                views.put(type, view_one);
            } else {
                View view_one = new View();
                view_one.log.add(new View.LogEntry(id, type, loaded, name, method, url, re, info, state));
                views.put(type, view_one);
            }
        }
        return views;

    }

    public static void show_yaml_view(BurpExtender burp, View view,String type) {
        if (view == null){
            show_yaml(burp);
        }else {
            List<View.LogEntry> log = view.log;
            synchronized (log) {
                log.clear();
                int row = log.size();
                Map<String, Object> Dict_Yaml = YamlUtil.readYaml(BurpExtender.Yaml_Path);
                List<Map<String, Object>> rule_list = (List<Map<String, Object>>) Dict_Yaml.get("Load_List");
                for (Map<String, Object> zidian : rule_list) {
                    String type2 = String.valueOf(zidian.get("type"));
                    if (type2.equals(type)) {
                        String id = String.valueOf(zidian.get("id"));
                        boolean loaded = Boolean.parseBoolean(String.valueOf(zidian.get("loaded")));
                        String name = (String) zidian.get("name");
                        String url = (String) zidian.get("url");
                        String re = (String) zidian.get("re");
                        String info = (String) zidian.get("info");
                        String state = (String) zidian.get("state");
                        String method = (String) zidian.get("method");
                        log.add(new View.LogEntry(id, type, loaded, name, method, url, re, info, state));
                        view.fireTableRowsInserted(row, row);
                    }
                }
            }
            burp.views = Get_Views();
        }
    }
        public static void show_yaml (BurpExtender burp){
            burp.views = Get_Views();
            burp.Config_l.ruleTabbedPane.removeAll();
            for (String key : burp.views.keySet()) {
                burp.Config_l.ruleTabbedPane.addTab(key, burp.views.get(key).Get_View());
            }
            burp.Config_l.ruleTabbedPane.addTab("...", new JLabel());


        }


    }
