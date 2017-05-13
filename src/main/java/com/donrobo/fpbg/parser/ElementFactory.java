package com.donrobo.fpbg.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ElementFactory {

    public static Element parseElement(List<String> tokens) {
        String token = tokens.remove(0);
        if (token.equals("{")) {
            if (tokens.get(1).equals("=")) { //Map
                HashMap<String, Element> elementHashMap = new HashMap<>();
                while (!tokens.get(0).equals("}")) {
                    String name = tokens.remove(0);
                    if (!name.matches("[\\w-]+")) {
                        throw new RuntimeException("Invalid name: " + name);
                    }
                    if (!tokens.remove(0).equals("=")) {
                        throw new RuntimeException("Expected '='");
                    }

                    Element value = parseElement(tokens);
                    elementHashMap.put(name, value);
                    String nextToken = tokens.get(0);
                    if (!nextToken.equals("}") && !tokens.remove(0).equals(",")) {
                        throw new RuntimeException("Expected ',', got \"" + nextToken + "\"");
                    }
                }
                if (!tokens.remove(0).equals("}")) {
                    throw new RuntimeException("Expected '}'");
                }
                return new MapElement(elementHashMap);
            } else { //Array
                List<Element> elements = new ArrayList<>();
                while (!tokens.get(0).equals("}")) {
                    elements.add(parseElement(tokens));
                    if (!tokens.get(0).equals("}") && !tokens.remove(0).equals(",")) {
                        throw new RuntimeException("Expected ','");
                    }
                }
                if (!tokens.remove(0).equals("}")) {
                    throw new RuntimeException("Expected '}'");
                }
                return new ArrayElement(elements);
            }
        } else if (token.matches("\"[^\"]*\"")) {
            return new PrimitiveElement(token.substring(1, token.length() - 1));
        } else if (token.matches("-?\\d+")) {
            return new PrimitiveElement(Integer.parseInt(token));
        } else if (token.matches("(-?\\d*\\.\\d+|-?\\d+\\.\\d*)")) {
            return new PrimitiveElement(Double.parseDouble(token));
        } else if (token.equals("false") || token.equals("true")) {
            return new PrimitiveElement(Boolean.parseBoolean(token));
        } else {
            throw new RuntimeException("Didn't parse: " + token);
        }
    }
}
