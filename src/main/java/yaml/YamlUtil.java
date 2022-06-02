package yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlUtil {

    public static void init_Yaml(String path){
        Map<String,Object> x = new HashMap<String,Object>();
        Collection<Object> list1 = new ArrayList<Object>();
        Map<String,Object> x1 = new HashMap<String,Object>();
        x1.put("id",1);
        x1.put("name","Nacos");
        x1.put("re","nacos");
        x1.put("url","/nacos/index.html");
        x1.put("info","Nacos Find!!!");
        x1.put("state","200");
        list1.add(x1);

        Map<String,Object> x2 = new HashMap<String,Object>();
        x2.put("id",2);
        x2.put("name","Spring Env RCE");
        x2.put("re","springframework|spring.cloud.bootstrap.location|Spring Actuator Env RCE");
        x2.put("url","/env");
        x2.put("info","Actuator Env|RCE:spring.cloud.bootstrap.location|XStreamRCE:eureka.client.serviceUrl.defaultZone Find!!!");
        x2.put("state","200");
        list1.add(x2);


        Map<String,Object> x3 = new HashMap<String,Object>();
        x3.put("id",3);
        x3.put("name","Spring Env RCE");
        x3.put("re","springframework|spring.cloud.bootstrap.location|Spring Actuator Env RCE");
        x3.put("url","/actuator/env");
        x3.put("info","Actuator Env|RCE:spring.cloud.bootstrap.location|XStreamRCE:eureka.client.serviceUrl.defaultZone Find!!!");
        x3.put("state","200");
        list1.add(x3);


        Map<String,Object> x4 = new HashMap<String,Object>();
        x4.put("id",4);
        x4.put("name","Druid Monitor");
        x4.put("re","druid");
        x4.put("url","/druid/index.html");
        x4.put("info","Druid Monitor Find!!!");
        x4.put("state","200");
        list1.add(x4);

        Map<String,Object> x5 = new HashMap<String,Object>();
        x5.put("id",5);
        x5.put("name","api-docs");
        x5.put("re","api-docs");
        x5.put("url","/v2/api-docs");
        x5.put("info","api-docs Find!!!");
        x5.put("state","200");
        list1.add(x5);

        Map<String,Object> x6 = new HashMap<String,Object>();
        x6.put("id",6);
        x6.put("name","Swagger-UI");
        x6.put("re","swagger");
        x6.put("url","/swagger-ui.html");
        x6.put("info","Swagger-UI Find!!!");
        x6.put("state","200");
        list1.add(x6);

        Map<String,Object> x7 = new HashMap<String,Object>();
        x7.put("id",7);
        x7.put("name","Spring Jolokia|Rce");
        x7.put("re","springframework|reloadByURL|createJNDIRealm");
        x7.put("url","/jolokia/list");
        x7.put("info","Spring Jolokia|XXE/RCE:reloadByURL|RCE:createJNDIRealm Find!!!");
        x7.put("state","200");
        list1.add(x7);

        Map<String,Object> x8 = new HashMap<String,Object>();
        x8.put("id",8);
        x8.put("name","Spring Jolokia|Rce");
        x8.put("re","springframework|reloadByURL|createJNDIRealm");
        x8.put("url","/actuator/jolokia/list");
        x8.put("info","Spring Jolokia|XXE/RCE:reloadByURL|RCE:createJNDIRealm Find!!!");
        x8.put("state","200");
        list1.add(x8);

        Map<String,Object> x9 = new HashMap<String,Object>();
        x9.put("id",9);
        x9.put("name","Doc File");
        x9.put("re","api");
        x9.put("url","/doc.html");
        x9.put("info","Doc File Find!!!");
        x9.put("state","200");
        list1.add(x9);

        Map<String,Object> x10 = new HashMap<String,Object>();
        x10.put("id",10);
        x10.put("name","swagger.json");
        x10.put("re","swagger");
        x10.put("url","/v1/swagger.json");
        x10.put("info","swagger.json Find!!!");
        x10.put("state","200");
        list1.add(x10);

        Map<String,Object> x11 = new HashMap<String,Object>();
        x11.put("id",11);
        x11.put("name","swagger.json");
        x11.put("re","swagger");
        x11.put("url","/v2/swagger.json");
        x11.put("info","swagger.json Find!!!");
        x11.put("state","200");
        list1.add(x11);

        x.put("Load_List",list1);
        YamlUtil.writeYaml(x,path);

    }

    public static Map<String, Object> readYaml(String file_path) {
        File file = new File(file_path);
        Map<String, Object> data = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            // InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("customer.yaml");

            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeYaml(Map<String, Object> data, String filePath) {
        Yaml yaml = new Yaml();
        try {
            PrintWriter writer = new PrintWriter(new File(filePath));
            yaml.dump(data, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void removeYaml(String id, String filePath) {
        Map<String,Object>Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String,Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        ArrayList<Map<String, Object>> List2 = new ArrayList<Map<String, Object>>();
        for (Map<String,Object> zidian : List1){
            if (!zidian.get("id").toString().equals(id)){
                List2.add(zidian);
            }
        }
        Map<String,Object> save = (Map<String, Object>) new HashMap<String,Object>();
        save.put("Load_List",List2);
        YamlUtil.writeYaml(save,filePath);
    }

    public static void updateYaml(Map<String,Object> up,String filePath){
        Map<String,Object>Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String,Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        List<Map<String,Object>> List2 = new ArrayList<Map<String,Object>>();
        for (Map<String,Object> zidian : List1){
            if (zidian.get("id").toString().equals(up.get("id").toString())){
                List2.add(up);
            }else {
                List2.add(zidian);
            }
        }
        Map<String,Object> save = (Map<String, Object>) new HashMap<String,Object>();
        save.put("Load_List",List2);
        YamlUtil.writeYaml(save,filePath);

    }

    public static void addYaml(Map<String,Object> add, String filePath) {
        Map<String,Object>Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String,Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        int panduan = 0;
        for (Map<String,Object> zidian : List1){
            if (zidian.get("id").toString().equals(add.get("id").toString())){
                panduan += 1;
            }
        }
        if (panduan == 0){
            Map<String,Object> save = (Map<String, Object>) new HashMap<String,Object>();
            List1.add(add);
            save.put("Load_List",List1);
            YamlUtil.writeYaml(save,filePath);
        }

    }



}