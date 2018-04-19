package com.ono.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amosli on 16/3/31.
 */
public class StringUtil extends StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    /**
     * 判断是否存在多个参数为空的情况,如果有一个为空则返回true
     *
     * @param params
     * @return
     */
    public static boolean isExistsEmptyInParams(Object... params) {
        for (Object param : params) {
            if (isEmpty(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在多个参数为空的情况,如果有一个为空则返回true
     *
     * @param params
     * @return
     */
    public static boolean isParamsAllEmpty(Object... params) {
        boolean isEmpty = true;
        for (Object param : params) {
            if (!isEmpty(param)) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    /**
     * 生成副本,比如input为ab,copyNum为2,则返回ababab
     *
     * @param input
     * @param copyNum
     * @return
     */
    public static String generateCopies(String input, int copyNum) {
        StringBuffer stringBuffer = new StringBuffer(input);
        for (int i = 0; i < copyNum; i++) {
            stringBuffer.append(input);
        }
        return stringBuffer.toString();
    }

    /**
     * sql查询时,前后添加%号
     *
     * @param input
     * @return
     */
    public static String addLikeParam(String input) {
        if (isEmpty(input)) {
            return null;
        }
        return "%" + input + "%";
    }

    public static String addLikeParamWithQuotation(String input) {
        if (isEmpty(input)) {
            return null;
        }
        return "'%" + input + "%'";
    }

    /**
     * sql查询时,前后添加%号
     *
     * @param input
     * @return
     */
    public static String addQuotationParam(String input) {
        if (isEmpty(input)) {
            return null;
        }
        return "'" + input + "'";
    }

    /**
     * 将对象转为String形式输出
     *
     * @param obj
     * @return
     */
    public static String objToString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    /**
     * 判断字符串是否是数值
     *
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        try {
            if (isEmpty(s)) {
                return false;
            }

            NumberFormat instance = NumberFormat.getInstance();
            instance.parse(s);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    /**
     * 将格式为如1-1000的字符串 用map 存储，minNum 存1, maxNum 存1000
     *
     * @param singleChargeNumStr 待解析
     * @return
     */
    public static Map<String, Integer> getSingleChargeNumInfo(String singleChargeNumStr) {
        if (singleChargeNumStr.indexOf("-") == -1) {
            return null;
        }
        String[] strings = singleChargeNumStr.split("-");
        if (strings.length != 2) {
            return null;
        }
        Map<String, Integer> result = new HashMap<>();
        String minStr = strings[0].trim();
        Integer minNum = Integer.parseInt(minStr);
        String maxStr = strings[1].trim();
        Integer maxNum = Integer.parseInt(maxStr);
        result.put("minNum", minNum);
        result.put("maxNum", maxNum);
        return result;
    }

    /**
     * 正则匹配
     *
     * @param reg
     * @param string
     * @return
     */
    public static boolean regexCheck(String reg, String string) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * 检查是否为数值
     *
     * @param string
     * @return
     */
    public static boolean checkIsNum(String string) {
        String reg = "^[0-9]{1,}$";
        return regexCheck(reg, string);
    }

    /**
     * List<String> list = new ArrayList<>();
     * list.add("a");
     * list.add("b");
     * 转换为('a','b')
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        String result = "(";

        for (String str : list) {
            if (!isEmpty(str)) {
                result += "'" + str + "',";
            }
        }
        result = result.substring(0, result.lastIndexOf(","));
        result += ")";
        return result;
    }

    public static String reverse(String str) {
        return new StringBuffer(str).reverse().toString();
    }


    public static String trim(String string) {
        if (string == null) {
            return null;
        }
        // 去掉特殊空格
        string = string.replace(" ", " ");
        // 并将中间多个连续的空格合并成一个
        String trim = Pattern.compile("[' ']+").matcher(string).replaceAll(" ").trim();
        //
        if (trim.startsWith(" ")) {
            trim = trim.substring(1);
        }
        return trim;
    }

    public static Boolean isListEmpty(List<String> list) {
        return isEmpty(list) || list.isEmpty() || list.size() < 1;
    }

    public static Boolean isTimeout(String result) {
        if (isEmpty(result) || result.equals("timeout")) {
            return true;
        }
        return false;
    }

    /**
     * @Author lujun
     * @Date 2017/9/1
     * @Description 中文转unicode
     * @params
     */
    public static String gbUnicodeEncoding(final String gbString) {   //gbString = "测试"
        char[] utfBytes = gbString.toCharArray();   //utfBytes = [测, 试]
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * @Author lujun
     * @Date 2017/9/1
     * @Description Unicode转中文
     * @params
     */
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 将Unicode编码解析成汉字
     *
     * @param pStr 包含（汉字的Unicode）编码的字符串
     * @return
     */
    public static String decodeUnicode2(String pStr) {
        char aChar;
        int len = pStr.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = pStr.charAt(x++);
            if (aChar == '\\') {
                aChar = pStr.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = pStr.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * @Author lujun
     * @Date 2017/10/9
     * @Description 流转换为字符串
     * @params
     */
    public static String inputStream2String(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            return baos.toString();
        } catch (IOException e) {
            LOGGER.error("读取流字符串异常", e);
            return null;
        }
    }


    /**
     * 字符串转换成十六进制字符串
     * @param str String 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String stringToHexString( String str) {
        byte[] b = str.getBytes();
        String a = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            a = a+hex;
        }
        return a;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}
