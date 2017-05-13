package com.donrobo.fpbg;

import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.InflaterInputStream;

public class BlueprintParser {

    public static void main(String[] args) throws Exception {
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("testblueprint.bp")) {
            String string = IOUtils.toString(is, "UTF-8");

            string = string.substring(1, string.length());
            byte[] bytes = Base64.getDecoder().decode(string);
            try (InputStream decompressionStream = new InflaterInputStream(new ByteArrayInputStream(bytes))) {
                byte[] array = IOUtils.toByteArray(decompressionStream);
                String inflatedString = new String(array);
                JSONObject json = JSONObject.fromObject(inflatedString);
                System.out.println(json);
            }
        }
    }
}
