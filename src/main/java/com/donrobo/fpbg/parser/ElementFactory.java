package com.donrobo.fpbg.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.donrobo.fpbg.parser.TokenRegex.*;

public class ElementFactory {

    public static Element parseElement(List<String> tokens) {
        String token = tokens.remove(0);
        if (token.matches(CURLY_BRACE_OPEN.pattern)) {
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
                    skipToEndOfValue(tokens);
                }
                if (!tokens.remove(0).equals("}")) {
                    throw new RuntimeException("Expected '}'");
                }
                return new MapElement(elementHashMap);
            } else { //Array
                List<Element> elements = new ArrayList<>();
                while (!tokens.get(0).equals("}")) {
                    elements.add(parseElement(tokens));
                    skipToEndOfValue(tokens);
                }
                if (!tokens.remove(0).equals("}")) {
                    throw new RuntimeException("Expected '}'");
                }
                return new ArrayElement(elements);
            }
        } else if (token.matches(STRING.pattern)) {
            return new PrimitiveElement(token.substring(1, token.length() - 1));
        } else if (token.matches(FUNCTION_CALL.pattern)) {
            return new PrimitiveElement(token);
        } else if (token.matches(INTEGER.pattern)) {
            return new PrimitiveElement(Integer.parseInt(token));
        } else if (token.matches(FLOATING_POINT.pattern)) {
            return new PrimitiveElement(Double.parseDouble(token));
        } else if (token.matches(CALCULATION.pattern)) {
            Matcher calcMatcher = Pattern.compile("(-?\\d*(?>\\.\\d+)?)\\s*([+\\-*/])(\\s*-?\\d*(?>\\.\\d+)?)").matcher(token);
            if (!calcMatcher.find()) {
                throw new RuntimeException("Something went very wrong! (forgot to update regex??)");
            }
            double value = Double.parseDouble(calcMatcher.group(1));
            double value2 = Double.parseDouble(calcMatcher.group(3));
            switch (calcMatcher.group(2)) {
                case "+":
                    value += value2;
                    break;
                case "-":
                    value -= value2;
                    break;
                case "*":
                    value *= value2;
                    break;
                case "/":
                    value /= value2;
                    break;
                default:
                    throw new RuntimeException("Something went very wrong!?");

            }
            return new PrimitiveElement(value);
        } else if (token.matches(MORE_COMPLEX_FUNCTION_CALL.pattern)) {
            return new PrimitiveElement(token);
        } else if (token.matches(COMPLEX_CALCULATION.pattern)) {
            return new PrimitiveElement(token); //TODO
        } else if (token.matches(BOOLEAN.pattern)) {
            return new PrimitiveElement(Boolean.parseBoolean(token));
        } else if (token.matches(IDENTIFIER.pattern)) {
            return new PrimitiveElement(token); //TODO
        } else {
            throw new RuntimeException("Didn't parse: " + token);
        }
    }

    private static void skipToEndOfValue(List<String> tokens) {
        boolean okay = false;
        while (!okay) {
            okay = true;
            String nextToken = tokens.get(0);
            if (!nextToken.equals("}") && !tokens.remove(0).matches(COMMA.pattern)) {
                okay = false;
            }
        }
    }
}
