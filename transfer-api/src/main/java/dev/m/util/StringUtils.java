package dev.m.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@UtilityClass
public class StringUtils {
    private static final Logger logger = LogManager.getLogger(StringUtils.class);

    public static void main(String[] args) {
        String a = "010601ae02056a0045c60c036e656f2e766e0007010364656e20747261692074696d000101";
        String b = "AQYBrgIFagBFxgwDbmVvLnZuAAcBA2RlbiB0cmFpIHRpbQABAQ==";
        String c = "BgUEC4Qj8A==";
        byte[] wapPushBytes = hexStringToByteArray("0605040b8423f0");
        // Mã hóa byte array thành chuỗi Base64
        String base64EncodedString = Base64.getEncoder().encodeToString(wapPushBytes);
        byte[] decodedBytes = Base64.getDecoder().decode(c);
        String decodedString = new String(decodedBytes);        // In ra chuỗi Base64
        System.out.println("Base64 encoded string: " + base64EncodedString);
        System.out.println("Original string: " + b);
        System.out.println("decodedString: " + decodedString);
    }

    // Phương thức chuyển đổi chuỗi hex thành byte array
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public String getIpServer() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String dateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date today = new Date();
        return dateFormat.format(today);
    }

    public static String formatMobi84(String isdn) {
        StringBuilder str = new StringBuilder();
        if (isdn == null) return "";
        try {
            if (isdn.startsWith("84")) {
                if (isdn.length() <= 9) {
                    str.append("84").append(isdn);
                } else {
                    return isdn;
                }
            } else if (isdn.startsWith("0")) {
                str.append("84").append(isdn.substring(1));
            } else {
                str.append("84").append(isdn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Error {}: {}", StringUtils.class.getMethods(), e.getMessage());
        }
        return str.toString();
    }

    public static boolean isNumber(String isdn) {
        boolean is = false;
        if (isdn == null) return is;
        try {
            int numSo = isdn.length();
            for (int i = 0; i < numSo; i++) {
                if (isdn.charAt(i) < '0' || isdn.charAt(i) > '9') {
                    return is;
                }
            }
            is = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Error {}: {}", StringUtils.class.getMethods(), e.getMessage());
        }
        return is;
    }

    public static boolean isMobile(String isdn) {
        boolean is = false;
        if (isdn == null) return is;
        try {
            int numSo = isdn.length();
            if (numSo < 9 || numSo > 12) {
                return false;
            }
            for (int i = 0; i < numSo; i++) {
                if (isdn.charAt(i) < '0' || isdn.charAt(i) > '9') {
                    return is;
                }
            }
            is = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Error {}: {}", StringUtils.class.getMethods(), e.getMessage());
        }
        return is;
    }

    public static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isNullOrEmpty(String str, int length) {
        if (str == null || str.isEmpty()) return true;
        return str.length() > length;
    }

    public static String getLastNumber(String msisdn, int length) {
        if (msisdn.length() == length) {
            return msisdn;
        } else if (msisdn.length() > length) {
            return msisdn.substring(msisdn.length() - length);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has fewer than 3 characters!");
        }
    }

    public boolean isDateValid(String input) {
        // Định dạng date cần kiểm tra
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        dateFormat.setLenient(false);
        try {
            // Parse chuỗi thành đối tượng Date
            Date parsedDate = dateFormat.parse(input);
            // Nếu không có lỗi, kiểm tra xem input có giữ nguyên định dạng không
            return !dateFormat.format(parsedDate).equals(input);
        } catch (ParseException e) {
            logger.debug("Error {}: {}", StringUtils.class.getMethods(), e.getMessage());
            return true;
        }
    }
}
