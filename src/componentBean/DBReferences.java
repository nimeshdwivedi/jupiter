/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import xmlBean.Reference;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */

@XmlRootElement(name ="References")
public class DBReferences {
    
    private List<Reference> reference=  null;

    @XmlElement(name = "Reference", namespace = "")
    public List<Reference> getReference() {
        return reference;
    }

    public void setReference(List<Reference> reference) {
        this.reference = reference;
    }
}
