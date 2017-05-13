package com.donrobo.fpbg.parser;

import com.sun.istack.internal.Nullable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {

    @Nullable
    public static Element parseFile(File file) throws IOException {
        String fileContent = FileUtils.readFileToString(file, "UTF-8");
        fileContent = fileContent.trim().replace("\r", "").replaceAll("--[^\n]+\n", "").replace('\n', ' ');

        Pattern pattern = Pattern.compile("data:extend\\s*\\(");
        Matcher matcher = pattern.matcher(fileContent);

        if (matcher.find()) {
            int startIndex = matcher.end();
            int endIndex = startIndex - 1;

            int brackets = 1;
            while (brackets != 0) {
                switch (fileContent.charAt(++endIndex)) {
                    case '(':
                        brackets++;
                        break;
                    case ')':
                        brackets--;
                        break;
                }
            }

            String content = fileContent.substring(startIndex, endIndex);
            List<String> tokens = tokenize(content);

            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                if (token.matches("\\d+") && tokens.get(i + 1).matches("\\.\\d+")) {
                    String first = tokens.remove(i);
                    String second = tokens.remove(i);
                    tokens.add(i, first + second);
                }
            }

            return ElementFactory.parseElement(tokens);
        }

        return null;
    }

    private static List<String> tokenize(String string) {
        List<String> tokens = new ArrayList<>();

        Pattern pattern = Pattern.compile(TokenRegex.combinedPattern());
        Matcher matcher = pattern.matcher(string);

        int endedWith = 0;
        while (matcher.find()) {
            if (matcher.start() != endedWith) {
                throw new RuntimeException("Skipped \"" + string.substring(endedWith, matcher.start() + 1) + "\"");
            }
            endedWith = matcher.end();
            tokens.add(matcher.group(0));
        }

        return tokens.stream().filter(s -> !s.matches("\\s*")).collect(Collectors.toList());
    }

}
