/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import java.util.Set;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */
@XmlRootElement(name = "DatabaseConnection")
public class DatabaseDetails {

    private String name;
    private String schema;
    private DatabaseObjects objects;

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "user")
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @XmlElement(name = "Objects")
    public DatabaseObjects getObjects() {
        return objects;
    }

    public void setObjects(DatabaseObjects objects) {
        this.objects = objects;
    }
}
