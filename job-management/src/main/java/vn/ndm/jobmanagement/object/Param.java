package vn.ndm.jobmanagement.object;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class Param {
    private String value;

    @XmlElement(name = "param")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
