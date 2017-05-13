package com.donrobo.fpbg.parser;

public enum TokenRegex {
    CURLY_BRACE_OPEN("\\{"),
    CURLY_BRACE_CLOSE("}"),
    WHITESPACE("\\s+"),
    EQUALS("="),
    STRING("\"[^\"]*\""),
    COMMA("[,;]"),//HACK should probably switch to Lua for parsing
    COMPLEX_CALCULATION("-?\\d*(?>\\.\\d+)?\\s*[+\\-*/]\\s*-?\\d*(?>\\.\\d+)?\\s*[+\\-*/]\\s*-?\\d*(?>\\.\\d+)?"), //HACK should probably switch to Lua for parsing
    CALCULATION("-?\\d*(?>\\.\\d+)?\\s*[+\\-*/]\\s*-?\\d*(?>\\.\\d+)?"),//HACK should probably switch to Lua for parsing
    FLOATING_POINT("-?\\d*\\.\\d+"),
    INTEGER("-?\\d+"),
    BOOLEAN("true|false"),
    MORE_COMPLEX_FUNCTION_CALL("\\(function\\(\\).*?end\\)\\(\\)"),//HACK should probably switch to Lua for parsing
    FUNCTION_CALL("[\\w\\-_.]+\\([^)]*\\)|[\\w\\-_.]+\\{[^}]*}"),//HACK should probably switch to Lua for parsing
    IDENTIFIER("[\\w\\-_.]+");

    public final String pattern;

    TokenRegex(String pattern) {
        this.pattern = pattern;
    }

    public static String combinedPattern() {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        sb.append("(");
        for (TokenRegex tokenType : values()) {
            if (!first) {
                sb.append("|");
            }
            first = false;
            sb.append(tokenType.pattern);
        }
        sb.append(")");

        return sb.toString();
    }
}
