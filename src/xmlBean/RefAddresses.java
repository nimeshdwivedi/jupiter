/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlBean;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author nimeshd
 */
@XmlRootElement(name="Reference")
public class RefAddresses {
    
    private List<StringRefAddr> dbDetails = null;

    @XmlElement(name = "StringRefAddr")
    public List<StringRefAddr> getDbDetails() {
        return dbDetails;
    }

    public void setDbDetails(List<StringRefAddr> dbDetails) {
        this.dbDetails = dbDetails;
    }
}
