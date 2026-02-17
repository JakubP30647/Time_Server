/**
 * @author Pawlik Jakub S30647
 */

package zad1;

import org.yaml.snakeyaml.Yaml;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(fileName))) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            String host = (String) data.get("host");
            int port = (int) data.get("port");
            boolean concurMode = (boolean) data.get("concurMode");
            boolean showSendRes = (boolean) data.get("showSendRes");


            Map<String, List<String>> clientsMap = new LinkedHashMap<>();
            Map<String, Object> rawClientsMap = (Map<String, Object>) data.get("clientsMap");
            for (Map.Entry<String, Object> entry : rawClientsMap.entrySet()) {
                clientsMap.put(entry.getKey(), (List<String>) entry.getValue());
            }

            return new Options(host, port, concurMode, showSendRes, clientsMap);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

