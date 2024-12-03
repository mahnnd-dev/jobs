package vn.ndm.jobmanagement.object;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "job")
public class Job {
    private String name;
    private String classJob;
    private String active;
    private String cronTime;

    private List<Param> params = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassJob() {
        return classJob;
    }

    public void setClassJob(String classJob) {
        this.classJob = classJob;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCronTime() {
        return cronTime;
    }

    public void setCronTime(String cronTime) {
        this.cronTime = cronTime;
    }

    @XmlElement(name = "params")
    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Job{");
        sb.append("name='").append(name).append('\'');
        sb.append(", classJob='").append(classJob).append('\'');
        sb.append(", active='").append(active).append('\'');
        sb.append(", cronTime='").append(cronTime).append('\'');
        sb.append(", params=").append(params);
        sb.append('}');
        return sb.toString();
    }
}
