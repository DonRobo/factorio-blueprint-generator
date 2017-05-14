package com.donrobo.fpbg;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.InflaterInputStream;

public class BlueprintParser {

    public static void main(String[] args) throws Exception {
        File blueprints = new File("src/main/resources/");
        Arrays.stream(blueprints.listFiles()).filter(f ->
                f.isFile() && f.getName().toLowerCase().endsWith(".bp")
        ).forEach(file -> {
            try (InputStream is = new FileInputStream(file)) {
                String string = IOUtils.toString(is, "UTF-8");

                String name = file.getName().substring(0, file.getName().length() - 3);

                string = string.substring(1, string.length());
                byte[] bytes = Base64.getDecoder().decode(string);
                try (InputStream decompressionStream = new InflaterInputStream(new ByteArrayInputStream(bytes))) {
                    byte[] array = IOUtils.toByteArray(decompressionStream);
                    String inflatedString = new String(array);
                    JSONObject json = JSONObject.fromObject(inflatedString);
                    FileUtils.writeStringToFile(new File(String.format("src/main/resources/%s.json", name)), json.toString(), "UTF-8")
                    ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
