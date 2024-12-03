package vn.ndm.jobmanagement.itf;


import java.util.Map;

/**
 * JobProcess
 * Định nghĩa cấu trúc một Job
 */

public interface JobProcess {
    void setParam(Map<String, Object> param);

    void execute();

    void stopJob();

    void workJob();

    String getNameJob();

    boolean isWorking();

}
