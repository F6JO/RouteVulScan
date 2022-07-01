package yaml;

import burp.BurpExtender;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlUtil {

    public static Boolean init_Yaml(){
        try {
            String url = "https://raw.githubusercontent.com/F6JO/RouteVulScan/main/Config_yaml.yaml";
            OkHttpClient httpClient = new OkHttpClient();
            Request httpRequest = new Request.Builder().url(url).get().build();
            Response httpResponse = httpClient.newCall(httpRequest).execute();
            FileOutputStream file = new FileOutputStream(BurpExtender.Yaml_Path);
            file.write(httpResponse.body().bytes());
            file.close();
        } catch (IOException e) {
            return false;
        }


        return true;




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


