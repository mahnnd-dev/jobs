package vn.ndm.jobmanagement.object;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "jobs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Jobs {
    private List<Job> jobList = new ArrayList<>();

    @XmlElement(name = "job")
    public List<Job> getJobs() {
        return jobList;
    }

    public void setJobs(List<Job> jobs) {
        this.jobList = jobs;
    }
}
