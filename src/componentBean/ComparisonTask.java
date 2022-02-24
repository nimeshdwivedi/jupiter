/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */
@XmlRootElement(name = "Task")
public class ComparisonTask {
    private String name;
    private String sourceDBName;
    private String targetDBName;
    private String lastModifiedTime;
    private TaskSourceObject sourceObject;
    private TaskTargetObject targetObject;
    private String dataType;
    private String dataOption;

    @XmlAttribute(name = "dataType")
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @XmlAttribute(name = "dataOption")
    public String getDataOption() {
        return dataOption;
    }

    public void setDataOption(String dataOption) {
        this.dataOption = dataOption;
    }

    @XmlElement(name = "SourceObject")
    public TaskSourceObject getSourceObject() {
        return sourceObject;
    }

    public void setSourceObject(TaskSourceObject sourceObject) {
        this.sourceObject = sourceObject;
    }

    @XmlElement(name = "TargetObject")
    public TaskTargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TaskTargetObject targetObject) {
        this.targetObject = targetObject;
    }

    @XmlAttribute(name = "lastModifiedDate")
    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "SourceDBName")
    public String getSourceDBName() {
        return sourceDBName;
    }

    public void setSourceDBName(String sourceDBName) {
        this.sourceDBName = sourceDBName;
    }

    @XmlElement(name = "TargetDBName")
    public String getTargetDBName() {
        return targetDBName;
    }

    public void setTargetDBName(String targetDBName) {
        this.targetDBName = targetDBName;
    }
    
}
