/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHelper;

import componentBean.DBConnection;
import componentBean.DBReferences;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import xmlBean.RefAddresses;
import xmlBean.Reference;
import xmlBean.StringRefAddr;
import componentBean.AttributeConstants;
import dbHelper.DatabaseHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author nimeshd
 */
public class ConnectionHelper {

    String connectionFile = "Connections.xml";
    String connectionFilePath = System.getProperty("user.home") + "//Documents//Jupiter-config";

    public ConnectionHelper() {
        File dir = new File(connectionFilePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                JOptionPane.showMessageDialog(null, "Failed to create directory structure due to system constraints. Please Create a directory: " + connectionFilePath + " and try again...");
            }
        }
        //Moving already present files from old dir to new one
        File oldDir = new File(System.getProperty("user.home") + "//Documents//DBComp");
        if (oldDir.exists()) {
            File[] content = oldDir.listFiles();
            for (File f : content) {
                if (f.isFile()) {
                    try {
                        Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(connectionFilePath + "//" + f.getName()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        File file = new File(connectionFilePath + "//" + connectionFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String[] getDBConnectionNames() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File(connectionFilePath + "//" + connectionFile);
            if (file.length() != 0) {
                DBReferences dbReferences = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
                String[] connNames = new String[dbReferences.getReference().size()];
                int i = 0;
                for (Reference r : dbReferences.getReference()) {
                    if (r.getName() != null) {
                        connNames[i] = r.getName();
                        i++;
                    }
                }
                System.out.println(connNames.length + " DB connections found");
                Arrays.sort(connNames);
                return connNames;
            }
        } catch (JAXBException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public Reference getDBReference(String connectionName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(connectionFilePath + "//" + connectionFile);
        if (file.length() != 0) {
            DBReferences dbReferences = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
            for (int i = 0; i < dbReferences.getReference().size(); i++) {
                if (dbReferences.getReference().get(i).getName().equalsIgnoreCase(connectionName)) {
                    return dbReferences.getReference().get(i);
                }
            }
        }
        return null;
    }

    public boolean testDBConnection(DBConnection dBConnection) throws SQLException {
        if (dBConnection.getPassword().isEmpty() || dBConnection.getPassword() == null) {
            JOptionPane.showMessageDialog(null, "Password missing. Please update Password for this connection");
            return false;
        }
        if (dBConnection.getCustURL() == null || dBConnection.getCustURL().isEmpty()) {
            dBConnection.setCustURL(DatabaseHelper.getCustomURL(dBConnection));
        }
        if (dBConnection.getDriverName() == null || dBConnection.getDriverName().isEmpty()) {
            dBConnection.setDriverName("oracle.jdbc.OracleDriver");
        }
        return DatabaseHelper.testDBConnection(dBConnection);
    }

    public boolean saveDBConnection(DBConnection dBConnection) {
        try {
            Reference reference = returnConnectionReference(dBConnection);
            JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File(connectionFilePath + "//" + connectionFile);
            if (file.length() == 0) {
                DBReferences dBReferences = new DBReferences();
                List<Reference> referenceList = new ArrayList<>();
                referenceList.add(reference);
                dBReferences.setReference(referenceList);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                if (!new File(connectionFilePath).exists()) {
                    JOptionPane.showMessageDialog(null, "Failed to create directory structure due to system constraints. Please Create a directory: " + connectionFilePath + " and try again...");
                } else {
                    jaxbMarshaller.marshal(dBReferences, new File(connectionFilePath + "//" + connectionFile));
                    return true;
                }
            } else {
                DBReferences connections = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
                List<Reference> dbReferences = connections.getReference();
                if (dbReferences.size() > 0) {
                    if (checkIfExistingConnection(reference, dbReferences)) {
                        JOptionPane.showMessageDialog(null, "DB Connection already exists with this name", "Please provide correct data source name", JOptionPane.ERROR_MESSAGE);
                    } else {
                        connections.getReference().add(reference);
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jaxbMarshaller.marshal(connections, new File(connectionFilePath + "//" + connectionFile));
                        return true;
                    }
                } else {
                    connections.getReference().add(reference);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(connections, new File(connectionFilePath + "//" + connectionFile));
                    return true;
                }
            }
        } catch (JAXBException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkIfExistingConnection(Reference reference, List<Reference> dbReferences) {
        int i = 0;
        while (i < dbReferences.size()) {
            if (dbReferences.get(i) == null) {
                dbReferences.remove(i);
            } else if (reference.getName().equalsIgnoreCase(dbReferences.get(i).getName())) {
                return true;
            }

            i++;
        }
        return false;
    }

    public boolean deleteDBConnection(String connectionName) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File(connectionFilePath + "//" + connectionFile);
            if (file.exists()) {
                DBReferences connections = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
                for (Reference r : connections.getReference()) {
                    if (r.getName().equalsIgnoreCase(connectionName)) {
                        connections.getReference().remove(r);
                        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        jaxbMarshaller.marshal(connections, new File(connectionFilePath + "//" + connectionFile));
                        return true;
                    }
                }
            }
        } catch (JAXBException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public boolean importDBConnection(String pathToFile) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File(connectionFilePath + "//" + connectionFile);
            if (!file.exists() || file.length() == 0) {
                Files.copy(Paths.get(new File(pathToFile).getAbsolutePath()), Paths.get(connectionFilePath + "//" + connectionFile), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } else {
                DBReferences existingConnection = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
                DBReferences importedConnection = (DBReferences) unmarshaller.unmarshal(new File(pathToFile));
                int existingConnSize = existingConnection.getReference().size();
                for (Reference conn : importedConnection.getReference()) {
                    if (!checkIfExistingConnection(conn, existingConnection.getReference())) {
                        existingConnection.getReference().add(conn);
                    }
                }
                if ((existingConnection.getReference().size()) - existingConnSize > 0) {
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(existingConnection, new File(connectionFilePath + "//" + connectionFile));
                    return true;
                }
                System.out.println("No changes made to Connections file. Nothing added.");
            }
        } catch (IOException | JAXBException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public boolean testConnection(String connectionName) {
        try {
            Reference reference = getDBReference(connectionName);
            DBConnection connection = extractDBConnection(reference);
            if (connection.getPassword().isEmpty() || connection.getPassword() == null) {
                JOptionPane.showMessageDialog(null, "Password missing. Please update Password for this connection");
            }
            return DatabaseHelper.testDBConnection(connection);
        } catch (JAXBException | SQLException ex) {
            System.out.println("Error Occurred while testing DB connections:" + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public boolean modifyDBConnection(DBConnection conn) {
        try {
            Reference ref = returnConnectionReference(conn);
            JAXBContext jaxbContext = JAXBContext.newInstance(DBReferences.class
            );
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            DBReferences existingConnection = (DBReferences) unmarshaller.unmarshal(new File(connectionFilePath + "//" + connectionFile));
            if (DatabaseHelper.testDBConnection(conn)) {
                if (checkIfExistingConnection(ref, existingConnection.getReference())) {
                    for (Reference r : existingConnection.getReference()) {
                        if (r.getName().equals(ref.getName())) {
                            existingConnection.getReference().remove(r);
                            existingConnection.getReference().add(ref);
                            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                            jaxbMarshaller.marshal(existingConnection, new File(connectionFilePath + "//" + connectionFile));
                            return true;
                        }
                    }
                } else {
                    existingConnection.getReference().add(ref);
                    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.marshal(existingConnection, new File(connectionFilePath + "//" + connectionFile));
                    return true;
                }
            }
        } catch (JAXBException | SQLException ex) {
            System.out.println("Error Occurred while testing DB connections:" + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public DBConnection extractDBConnection(Reference reference) {
        RefAddresses refAddress = reference.getRefAddresses();
        DBConnection dBConnection = new DBConnection();
        List<StringRefAddr> dbDetails = refAddress.getDbDetails();
        for (StringRefAddr db : dbDetails) {
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.CONN_NAME)) {
                dBConnection.setConnectionName(reference.getName());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.CUSTOME_URL)) {
                dBConnection.setCustURL(db.getContents());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.DRIVER)) {
                dBConnection.setDriverName(db.getContents());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.HOSTNAME)) {
                dBConnection.setHost(db.getContents());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.PASSWORD)) {
                String[] args = new String[1];
                args[0] = db.getContents();
                if (db.getContents().startsWith("05")) {
                    dBConnection.setPassword(decodePassword(args));
                } else {
                    dBConnection.setPassword(args[0]);
                }
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.PORT)) {
                dBConnection.setPort(Integer.parseInt(db.getContents()));
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.SERVICE_ID)) {
                dBConnection.setServiceID(db.getContents());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.SERVICE_NAME)) {
                dBConnection.setServiceName(db.getContents());
            }
            if (db.getAddrType().equalsIgnoreCase(AttributeConstants.USERNAME)) {
                dBConnection.setUsername(db.getContents());
            }
        }
        return dBConnection;
    }

    public Reference returnConnectionReference(DBConnection dBConnection) {
        Reference reference = new Reference();
        if (dBConnection != null) {
            List<StringRefAddr> dbDetails = new ArrayList<>();
            StringRefAddr addr;
            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.CUSTOME_URL);
            addr.setContents(dBConnection.getCustURL());
            dbDetails.add(addr);
            addr = null;

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.DRIVER);
            addr.setContents(dBConnection.getDriverName());
            dbDetails.add(addr);
            addr = null;

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.HOSTNAME);
            addr.setContents(dBConnection.getHost());
            dbDetails.add(addr);
            addr = null;

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.PASSWORD);
            addr.setContents(dBConnection.getPassword());
            dbDetails.add(addr);
            addr = null;

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.PORT);
            addr.setContents(dBConnection.getPort().toString());
            dbDetails.add(addr);
            addr = null;

            if (dBConnection.getServiceID() != null) {
                addr = new StringRefAddr();
                addr.setAddrType(AttributeConstants.SERVICE_ID);
                addr.setContents(dBConnection.getServiceID());
                dbDetails.add(addr);
                addr = null;
            }

            if (dBConnection.getServiceName() != null) {
                addr = new StringRefAddr();
                addr.setAddrType(AttributeConstants.SERVICE_NAME);
                addr.setContents(dBConnection.getServiceName());
                dbDetails.add(addr);
                addr = null;
            }

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.USERNAME);
            addr.setContents(dBConnection.getUsername());
            dbDetails.add(addr);
            addr = null;

            addr = new StringRefAddr();
            addr.setAddrType(AttributeConstants.CONN_NAME);
            addr.setContents(dBConnection.getConnectionName());
            dbDetails.add(addr);
            addr = null;

            RefAddresses refAddresses = new RefAddresses();
            refAddresses.setDbDetails(dbDetails);

            reference.setName(dBConnection.getConnectionName());
            reference.setRefAddresses(refAddresses);
        }
        return reference;
    }

    public String decodePassword(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage:  java Decrypt <password>");
            System.exit(1);
        }

        if (args[0].length() % 2 != 0) {
            System.err.println("Password must consist of hex pairs.  Length is odd (not even).");
            System.exit(2);
        }

        byte[] secret = new byte[args[0].length() / 2];
        for (int i = 0; i < args[0].length(); i += 2) {
            String pair = args[0].substring(i, i + 2);
            secret[i / 2] = (byte) (Integer.parseInt(pair, 16));
        }

        return new String(decryptPassword(secret));
    }

    public static byte[] decryptPassword(byte[] result) {
        byte constant = result[0];
        if (constant != (byte) 5) {
            throw new IllegalArgumentException();
        }

        byte[] secretKey = new byte[8];
        System.arraycopy(result, 1, secretKey, 0, 8);

        byte[] encryptedPassword = new byte[result.length - 9];
        System.arraycopy(result, 9, encryptedPassword, 0, encryptedPassword.length);

        byte[] iv = new byte[8];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = 0;
        }

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "DES"), new IvParameterSpec(iv));
            return cipher.doFinal(encryptedPassword);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Error Occurred while testing DB connections:" + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}
