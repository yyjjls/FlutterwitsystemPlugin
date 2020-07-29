package com.witsystem.top.flutterwitsystem.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验
 */
public class CheckCode {

    /**
     * 字符串mac转mac
     * @param split 分割符号
     */
    public static String formatMac(String mac, String split) {
        String regex = "[0-9a-fA-F]{12}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mac);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("mac format is error");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            char c = mac.charAt(i);
            sb.append(c);
            if ((i & 1) == 1 && i <= 9) {
                sb.append(split);
            }
        }

        return sb.toString();
    }
}
