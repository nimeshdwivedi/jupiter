/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHelper;

import componentBean.DBTable;
import componentBean.DatabaseDetails;
import componentBean.DatabaseObjects;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author nimeshd
 */
public class DatabaseDetailHelper {

    String connectionFilePath = System.getProperty("user.home") + "//Documents//Jupiter-config//connectionsBackup";

    public DatabaseDetailHelper() {
        File dir = new File(connectionFilePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public File getDBConnectionFile(String connectionName, String schema) {
        File dir = new File(connectionFilePath);
        if (dir.listFiles().length > 0) {
            for (File file : dir.listFiles()) {
                String nameToBeSearched = connectionName + "_" + schema;
                if (schema == null && file.getName().startsWith(connectionName)) {
                    return file;
                }
                if (file.getName().equalsIgnoreCase(nameToBeSearched)) {
                    return file;
                }
            }
        }
        return null;
    }

    public boolean checkIfDBSaved(String name, String schema) {
        return getDBConnectionFile(name, schema) != null;
    }

    public DatabaseObjects getDBObject(String connName, String schema) {
        System.out.println("Getting DB Object...");
        File f = getDBConnectionFile(connName, schema);
        if (f != null) {
            JAXBContext jaxbContext;
            try {
                jaxbContext = JAXBContext.newInstance(DatabaseDetails.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                DatabaseDetails dbDetails = (DatabaseDetails) unmarshaller.unmarshal(f);
                if (dbDetails.getSchema().equalsIgnoreCase(schema)) {
                    return dbDetails.getObjects();
                } else {
                    System.out.println("Schema mismatch: Found - " + dbDetails.getSchema() + ", \t Expected - " + schema);
                }
            } catch (JAXBException ex) {
                Logger.getLogger(DatabaseDetailHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public List<DBTable> getTables(String connName, String schema, String type) {
        File f = getDBConnectionFile(connName, schema);
        if (f != null) {
            DatabaseObjects objects = getDBObject(connName, schema);
            if (objects == null) {
                System.out.println("Objects not initialized for this DB");
            } else {
                if (type.equals("CLOB")) {
                    return objects.getClobs();
                } else {
                    return objects.getTables();
                }
            }
        }
        return null;
    }

    public boolean saveDBObject(String connName, String schema, DatabaseObjects objects) {
        File f = getDBConnectionFile(connName, schema);
        JAXBContext jaxbContext;
        if (f != null) {
            try {
                jaxbContext = JAXBContext.newInstance(DatabaseDetails.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                DatabaseDetails savedDetails = (DatabaseDetails) unmarshaller.unmarshal(f);
                /*if (!savedDetails.getSchema().equals(schema)) {
                    int showConfirmDialog = JOptionPane.showConfirmDialog(null, "DBDetails configuration already exists with another schema. Do you want to override the details?");
                    if (showConfirmDialog != 0) {
                        return false;
                    }
                }*/
                savedDetails.setObjects(objects);
                savedDetails.setSchema(schema);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(savedDetails, new File(connectionFilePath + "//" + f.getName()));
                return true;
            } catch (JAXBException ex) {
                Logger.getLogger(DatabaseDetailHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            DatabaseDetails databaseDetails = new DatabaseDetails();
            databaseDetails.setName(connName);
            databaseDetails.setSchema(schema);
            databaseDetails.setObjects(objects);
            Marshaller jaxbMarshaller;
            try {
                jaxbContext = JAXBContext.newInstance(DatabaseDetails.class);
                jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(databaseDetails, new File(connectionFilePath + "//" + connName + "_" + schema));
                return true;
            } catch (JAXBException ex) {
                Logger.getLogger(DatabaseDetailHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public boolean deleteDBDetails(String connName) {
        File dir = new File(connectionFilePath);
        boolean deleteStatus = false;
        if (dir.listFiles().length > 0) {
            for (File file : dir.listFiles()) {
                if (file.getName().startsWith(connName)) {
                    file.delete();
                    deleteStatus = true;
                }
            }
        }
        return deleteStatus;
    }

    public boolean saveColumns(String connName, String schema, List<DBTable> tableList, String dataType) {
        DatabaseObjects dbObject = getDBObject(connName, schema);
        List<DBTable> tables;

        if (dataType.equals("CLOB")) {
            tables = dbObject.getClobs();
        } else {
            tables = dbObject.getTables();
        }
        if (tables != null && !tables.isEmpty()) {
            List<DBTable> modifiedTables = new ArrayList<>(tables);
            List<DBTable> inputTables = new ArrayList<>(tableList);
            int index = 0, index2 = 0;
            boolean changed = false;
            System.out.println("Fetched " + tables.size() + " for Connection:" + connName);
            if (tables.size() > 0) {
                for (DBTable savedTable : tables) {
                    for (DBTable dBTable : inputTables) {
                        changed = false;
                        if (savedTable.getName().equals(dBTable.getName())) {
                            System.out.println("Modifying the Columns for Table: " + dBTable.getName() + " and Columns :" + dBTable.getColumnList());
                            index = inputTables.indexOf(dBTable);
                            index2 = tables.indexOf(savedTable);
                            modifiedTables.remove(savedTable);
                            modifiedTables.add(index2, dBTable);
                            changed = true;
                            break;
                        }
                    }
                    if (changed && inputTables.size() > 0) {
                        inputTables.remove(index);
                    }
                }
                if (dataType.equals("CLOB")) {
                    dbObject.setClobs(modifiedTables);
                } else {
                    dbObject.setTables(modifiedTables);
                }

                return saveDBObject(connName, schema, dbObject);
            }
            inputTables.clear();
        }
        return false;
    }
}
