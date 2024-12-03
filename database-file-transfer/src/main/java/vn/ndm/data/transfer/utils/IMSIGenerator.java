package vn.ndm.data.transfer.utils;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@UtilityClass
public class IMSIGenerator {
    private static final Logger logger = LogManager.getLogger(IMSIGenerator.class);
    private static final String PREFIX = "45207";

    public static synchronized String generateIMSI(String msisdn) {
        try {
            StringBuilder builder = new StringBuilder();
            if (msisdn.length() > 10) {
                if (msisdn.startsWith("84")) {
                    msisdn = msisdn.substring(2);
                    builder.append(PREFIX).append("0").append(msisdn);
                    return builder.toString();
                }
            } else {
                builder.append(PREFIX).append(msisdn);
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
