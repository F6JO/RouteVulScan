package burp;





import yaml.YamlUtil;

import java.util.List;
import java.util.Map;

public class Bfunc {

    public static void show_yaml(View view_class, List<View.LogEntry> log,String path){

        synchronized (log) {
            log.clear();
            int row = log.size();
            Map<String, Object> Dict_Yaml = YamlUtil.readYaml(path);
            List<Map<String,Object>> rule_list = (List<Map<String, Object>>) Dict_Yaml.get("Load_List");
            for (Map<String,Object> zidian : rule_list){
                String id = String.valueOf(zidian.get("id"));
                String name = (String) zidian.get("name");
                String url = (String) zidian.get("url");
                String re = (String) zidian.get("re");
                String info = (String) zidian.get("info");
                String state = (String) zidian.get("state");
                log.add(new View.LogEntry(id,name,url,re,info,state));
                view_class.fireTableRowsInserted(row, row);
            }



        }

    }


}
