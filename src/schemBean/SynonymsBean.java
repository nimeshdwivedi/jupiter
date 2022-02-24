/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schemBean;

/**
 *
 * @author nimeshd
 */
public class SynonymsBean {
    String synonymName, tableName, dbLink;

    public String getSynonymName() {
        return synonymName;
    }

    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbLink() {
        return dbLink;
    }

    public void setDbLink(String dbLink) {
        this.dbLink = dbLink;
    }

    @Override
    public String toString() {
        String format = "%1$-20s | %2$-20s | %3$-20s";        
        return String.format(format, synonymName, tableName, dbLink);
    }
    
    
}
