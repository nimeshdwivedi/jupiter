/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlBean;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */
@XmlRootElement(name="Reference")
public class Reference {
    private String Factory = null;
    private RefAddresses refAddresses = null;
    private String name=null;
    private String className=null;

    @XmlAttribute(name = "className")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlElement(name = "RefAddresses")
    public RefAddresses getRefAddresses() {
        return refAddresses;
    }

    public void setRefAddresses(RefAddresses refAddresses) {
        this.refAddresses = refAddresses;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "Factory")
    public String getFactory() {
        return Factory;
    }

    public void setFactory(String Factory) {
        this.Factory = Factory;
    }
}
