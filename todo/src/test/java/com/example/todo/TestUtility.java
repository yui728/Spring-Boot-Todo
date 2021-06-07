package com.example.todo;

import java.util.Collections;

public class TestUtility {
    public static String repeatString(String repeatString, int repeatLength, String prefix) {
        if(prefix != null) {
            repeatLength = repeatLength - prefix.length();
        }

        int remainder = repeatLength % repeatString.length();
        int odd = repeatLength / repeatString.length();

        StringBuilder builder = new StringBuilder(prefix);
        builder.append(String.join("", Collections.nCopies(odd, repeatString)));
        if(remainder > 0) {
            builder.append(repeatString.substring(0, remainder));
        }
        return builder.toString();
    }
}
