/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */
@XmlRootElement(name = "Objects")
public class DatabaseObjects {
    private List<DBTable> tables;
    private List<DBTable> clobs;
    private Set<String> views;
    private Set<String> triggers;
    private Set<String> sequences;
    private Set<String> synonyms;
    private Set<String> indexes;
    private Set<String> dbLinks;

    @XmlElement(name = "Clobs")
    public List<DBTable> getClobs() {
        return clobs;
    }

    public void setClobs(List<DBTable> clobs) {
        this.clobs = clobs;
    }
    
    @XmlElement(name = "Table")
    public List<DBTable> getTables() {
        return tables;
    }

    public void setTables(List<DBTable> tables) {
        this.tables = tables;
    }

    @XmlElement(name = "Views")
    public Set<String> getViews() {
        return views;
    }

    public void setViews(Set<String> views) {
        this.views = views;
    }

    @XmlElement(name = "Triggers")
    public Set<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<String> triggers) {
        this.triggers = triggers;
    }

    @XmlElement(name = "Sequences")
    public Set<String> getSequences() {
        return sequences;
    }

    public void setSequences(Set<String> sequences) {
        this.sequences = sequences;
    }

    @XmlElement(name = "Constraint")
    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    @XmlElement(name = "Indexes")
    public Set<String> getIndexes() {
        return indexes;
    }

    public void setIndexes(Set<String> indexes) {
        this.indexes = indexes;
    }

    @XmlElement(name = "DBLink")
    public Set<String> getDbLinks() {
        return dbLinks;
    }

    public void setDbLinks(Set<String> dbLinks) {
        this.dbLinks = dbLinks;
    }

}
