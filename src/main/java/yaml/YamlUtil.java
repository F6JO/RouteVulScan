package yaml;

import burp.BurpExtender;
import func.init_Yaml_thread;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class YamlUtil {

    public static void init_Yaml(BurpExtender burp, JPanel one) {
        new init_Yaml_thread(burp, one).start();

    }

    public static Map<String, Object> readYaml(String file_path) {
        File file = new File(file_path);
        Map<String, Object> data = null;
        try {
            InputStream inputStream = new FileInputStream(file);
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
        Map<String, Object> Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String, Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        ArrayList<Map<String, Object>> List2 = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> zidian : List1) {
            if (!zidian.get("id").toString().equals(id)) {
                List2.add(zidian);
            }
        }
        Map<String, Object> save = (Map<String, Object>) new HashMap<String, Object>();
        save.put("Load_List", List2);
        YamlUtil.writeYaml(save, filePath);
    }

    public static void updateYaml(Map<String, Object> up, String filePath) {
        Map<String, Object> Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String, Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        List<Map<String, Object>> List2 = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> zidian : List1) {
            if (zidian.get("id").toString().equals(up.get("id").toString())) {
                List2.add(up);
            } else {
                List2.add(zidian);
            }
        }
        Map<String, Object> save = (Map<String, Object>) new HashMap<String, Object>();
        save.put("Load_List", List2);
        YamlUtil.writeYaml(save, filePath);

    }

    public static void addYaml(Map<String, Object> add, String filePath) {
        Map<String, Object> Yaml_Map = YamlUtil.readYaml(filePath);
        List<Map<String, Object>> List1 = (List<Map<String, Object>>) Yaml_Map.get("Load_List");
        int panduan = 0;
        for (Map<String, Object> zidian : List1) {
            if (zidian.get("id").toString().equals(add.get("id").toString())) {
                panduan += 1;
            }
        }
        if (panduan == 0) {
            Map<String, Object> save = (Map<String, Object>) new HashMap<String, Object>();
            List1.add(add);
            save.put("Load_List", List1);
            YamlUtil.writeYaml(save, filePath);
        }

    }

    public static Map<String, Object> readStrYaml(String str){
        Map<String, Object> data = null;
        Yaml yaml = new Yaml();
        data = yaml.load(str);
        return data;
    }


    public static void MergerUpdateYamlFunc(Map<String, Object> newYaml){
        Map<String, Object> oldYaml = YamlUtil.readYaml(BurpExtender.Yaml_Path);
        List<Map<String, Object>> oldYamlList = (List<Map<String, Object>>)oldYaml.get("Load_List");
        List<Map<String, Object>> newYamlList = (List<Map<String, Object>>)newYaml.get("Load_List");
        for (Map<String, Object> i : newYamlList){
            if (!YamlUtil.inYamlList(oldYamlList,i)){
                int id = 0;
                for (Map<String, Object> zidian : (List<Map<String, Object>>)YamlUtil.readYaml(BurpExtender.Yaml_Path).get("Load_List")) {
                    if ((int) zidian.get("id") > id) {
                        id = (int) zidian.get("id");
                    }
                }
                id += 1;
                i.remove("id");
                i.put("id",id);
                YamlUtil.addYaml(i,BurpExtender.Yaml_Path);
            }
        }


    }

    public static boolean inYamlList(List<Map<String, Object>> mapList,Map<String, Object> oneMap){
        for (Map<String, Object> i : mapList){
            if (YamlUtil.ifmapEqual(i,oneMap)){
                return true;
            }
        }
        return false;

    }

    public static boolean ifmapEqual(Map<String, Object> i, Map<String, Object> oneMap){
        boolean mapEqual = true;
        for (String key : i.keySet()){
            if (!key.equals("loaded") && !key.equals("id") && !key.equals("type")){
                if (!i.get(key).equals(oneMap.get(key))){
                    mapEqual = false;
                    break;
                }
            }
        }
        return mapEqual;
    }



}


