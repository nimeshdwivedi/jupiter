
import FileHelper.FileHandlingUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import jdk.internal.org.xml.sax.SAXException;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;

//First JFrame
public class Test {

    public static void main(String ag[]) throws JAXBException, SQLException, ClassNotFoundException, InterruptedException, org.xml.sax.SAXException {

        //FileInputStream fis1 = null;
        try {
            /*List<DBTable> tableList = new ArrayList<>();
            DBTable table = new DBTable();
            /*table.setName("CM1_FLOW_CUSTOMIZE");
            tableList.add(table);
            table = null;//
            table = new DBTable();
            table.setName("BL1_API_CUSTOMIZE");
            tableList.add(table);
            /
            DBConnection conn = new DBConnection();
            conn.setConnectionName("NET DB 237");
            conn.setCustURL("jdbc:oracle:thin:@illinqw237:1710:NETDB237");
            conn.setDriverName("oracle.jdbc.OracleDriver");
            conn.setHost("illinqw237");
            conn.setPassword("ABPREF1");
            conn.setPort(1710);
            conn.setServiceID("NETDB237");
            conn.setUsername("ABPREF1");
            DBConnection conn1 = new DBConnection();
            conn1.setConnectionName("NET DB 708");
            conn1.setCustURL("jdbc:oracle:thin:@illinqw708:1710:NETDB708");
            conn1.setDriverName("oracle.jdbc.OracleDriver");
            conn1.setHost("illinqw708");
            conn1.setPassword("ABPREF1");
            conn1.setPort(1710);
            conn1.setServiceID("NETDB708");
            conn1.setUsername("ABPREF1");
            Connection connection1 = new ConnectionProvider().getODSConnection(conn);
            PreparedStatement ps = connection1.prepareStatement("select flow_name, customize_class from bl1_api_customize where rownum<10 order by flow_name, customize_class");
            ResultSet rs1 = ps.executeQuery();
            System.out.println(rs1.getString(1));
            String username = "ABPREF1";
            Connection connection1 = new ConnectionProvider().getODSConnection(conn);
            System.out.println(connection1);
            ResultSet metaData = connection1.getMetaData().getTables(null, username, "%", new String[]{"TABLE", "SYNONYM"});
            //ResultSet metaData = connection1.getMetaData().getTables(null, null, "%", null);
            int count =0;
            while(metaData.next()){
            if(count == 10)
            break;
            count++;
            System.out.println(metaData.getString(1) +" , "+metaData.getString(2) +" , "+metaData.getString(3) +" , "+metaData.getString(4));
            }
            connection1.close();
            //connection2.close();
            //Connection connection2 = new ConnectionProvider().getODSConnection(conn1);
            //if(connection1 == null || connection2 == null)
            // System.out.println("Null"+ connection1+" and "+ connection2);
            /*
            //else{
            PreparedStatement ps = connection1.prepareStatement("select flow_name, customize_class from bl1_api_customize where rownum<10 order by flow_name, customize_class");
            ResultSet rs1 = ps.executeQuery();
            ps.clearParameters();
            ps = connection2.prepareStatement("select flow_name, customize_class from bl1_api_customize where rownum<5 and customize_class is not null order by flow_name, customize_class");
            ResultSet rs2 = ps.executeQuery();
            while(rs1.next() && rs2.next()){
            if(rs1.getString(1).equals(rs2.getString(1))  && rs1.getString(2).equals(rs2.getString(2)) )
            System.out.println("Equal");
            }
            }
            connection1.close();
            connection2.close();
            new Test().initializeColumnsForSelectedTables(tableList, conn);*
            String connectionFile = "Connections.xml";
            String connectionFilePath = System.getProperty("user.home") + "//Documents//DBComp";
            File dir = new File(connectionFilePath);
            if (!dir.exists()) {
            System.out.println(dir.mkdir());;
            }
            File file = new File(connectionFilePath + "//" + connectionFile);
            if (!file.exists()) {
            try {
            file.createNewFile();
            System.out.println("Length of file: " + file.length());
            } catch (IOException ex) {
            Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            }else{
            System.out.println("file exists");
            }
            System.out.println(new ConnectionHelper().saveDBConnection(conn1));;
            System.out.println("False");
            System.out.println(new File(connectionFilePath + "//" + connectionFile).exists());
            System.out.println(new File(connectionFilePath).exists());
            Double d = Double.parseDouble("465f4");
            System.out.println(d);
            HashMap<Integer, String> hashMap = new HashMap<>();
            hashMap.put(89866, "BL9_PRODUCT_GROUP_CONFIG@89866@0@0");
            hashMap.put(12265, "BL9_PRODUCT_GROUP_CONFIG@12265@0@0");
            hashMap.put(10808, "BL9_PRODUCT_GROUP_CONFIG@89866@5016@0");
            hashMap.put(4018, "AR1_GROUPING_RULE_NAMES@0@668@3350");
            List list = new ArrayList(hashMap.keySet());
            String format = "%1$-30s|\t%2$-10s|\t%3$-10s|\t%4$-10s";
            for (int i = 0; i < list.size(); i++) {
            String tableData = hashMap.get(list.get(i));
            String[] split = tableData.split("@");
            String data = String.format(format, split[0], split[1], split[2], split[3]);
            System.out.println(data + " | \t " + list.get(i));
            System.out.println();
            }
            format = "|%1$-10s|%2$-10s|%3$-20s|\n";
            System.out.format(format, "A", "AA", "AAA");
            System.out.format(format, "B", "", "BBBBB");
            System.out.format(format, "C", "CCCCC", "CCCCCCCC");
            String ex[] = {"E", "EEEEEEEEEE", "E"};
            System.out.format(String.format(format, (Object[]) ex));*
            fis1 = new FileInputStream("C:\\Users\\nimeshd\\Documents\\DBComp\\Tasks.xml");
            FileInputStream fis2 = new FileInputStream("C:\\Users\\nimeshd\\Documents\\DBComp\\Tasks2.xml"); // using BufferedReader for improved performance 
            BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
            BufferedReader target = new BufferedReader(new InputStreamReader(fis2)); //configuring XMLUnit to ignore white spaces
            XMLUnit.setIgnoreWhitespace(true); //comparing two XML using XMLUnit in Java
            List differences = compareXML(source, target); //showing differences found in two xml files 
            printDifferences(differences);*
            /*long starttime, endtime;
            HashMap<String, Clob> mp = new HashMap<>();
            Set<String> set = new HashSet<>();
            Set<String> tset = new HashSet<>();*
            DBConnection conn = new DBConnection();
            conn.setConnectionName("NET DB 129");
            conn.setCustURL("jdbc:oracle:thin:@illinqw129:1710:NETDB129");
            conn.setDriverName("oracle.jdbc.OracleDriver");
            conn.setHost("illinqw129");
            conn.setPassword("ABPREFO1");
            conn.setPort(1710);
            conn.setServiceID("NETDB129");
            conn.setUsername("ABPREFO1");
            String data = null;
            Clob c = null;
            Connection connection1 = new ConnectionProvider().getODSConnection(conn);
            PreparedStatement ps = connection1.prepareStatement(null);
            ResultSet rs = ps.executeQuery();
            /*
            DBConnection conn2 = new DBConnection();
            conn2.setConnectionName("NET DB 338");
            conn2.setCustURL("jdbc:oracle:thin:@illinqw338:1710:NETDB338");
            conn2.setDriverName("oracle.jdbc.OracleDriver");
            conn2.setHost("illinqw338");
            conn2.setPassword("ABPREFO1");
            conn2.setPort(1710);
            conn2.setServiceID("NETDB338");
            conn2.setUsername("ABPREFO1");
            data = null;
            c = null;
            Connection connection2 = new ConnectionProvider().getODSConnection(conn2);
            ps = connection2.prepareStatement("SELECT OBJECT_NAME, SCHEMA, XML FROM AC1_XML_CONFIG ORDER BY OBJECT_NAME, SCHEMA DESC");
            rs = ps.executeQuery();
            if (rs != null) {
            starttime = System.currentTimeMillis();
            while (rs.next()) {
            data = rs.getString("OBJECT_NAME") + " | " + rs.getString("SCHEMA");
            data += " | " + toString(rs.getClob("XML"));
            tset.add(data);
            }
            System.out.println("Time Taken:"+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - starttime));
            }
            starttime = System.currentTimeMillis();
            tset.removeAll(set);
            System.out.println("Time Taken:"+ TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - starttime));
            File f2 = new File("C:\\Users\\nimeshd\\Desktop\\AR_ENTLINXML2.xml");
            //System.out.println(f.compareTo(f2));
            FileInputStream fis1 = new FileInputStream("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\source1XML\\AR1_AGGREGATION_CONFIG-CONFIG_XML-8");
            FileInputStream fis2 = new FileInputStream("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\target1XML\\AR1_AGGREGATION_CONFIG-CONFIG_XML-8");
            BufferedReader target = new BufferedReader(new InputStreamReader(fis2));
            BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(Boolean.TRUE);
            XMLUnit.setCompareUnmatched(false);
            XMLUnit.setIgnoreComments(true);
            System.out.println("Comparing 2 XMLs");
            List differences = compareXML(source, target); //showing differences found in two xml files 
            printDifferences(differences);
            //new Test().doSomething();
            /*FileInputStream fis1 = new FileInputStream("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\sourceXML\\ADJ1_PROTOCOL_FILES-FILE_CONTENT-1");
            FileInputStream fis2 = new FileInputStream("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\targetXML\\ADJ1_PROTOCOL_FILES-FILE_CONTENT-1");
            BufferedReader target = new BufferedReader(new InputStreamReader(fis2));
            BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
            String sline, tline;
            int mismatchCount = 0;
            while ((sline = source.readLine()) != null && (tline = target.readLine()) != null) {
            if (!sline.replace(" ", "").equals(tline.replace(" ", ""))) {
            System.out.println("source: " + sline);
            System.out.println("target: " + tline);
            mismatchCount++;
            }
            System.out.println("");
            }
            while ((sline = source.readLine()) != null) {
            System.out.println("Extra /source - ");
            System.out.println(sline);
            mismatchCount++;
            }
            while ((tline = target.readLine()) != null) {
            System.out.println("Extra /target - ");
            System.out.println(tline);
            mismatchCount++;
            }
            System.out.println("mismatch count:" + mismatchCount);
            String connName = "NET 129";
            String schema = "ABPREFO1";
            List<DBTable> dBTables = new ArrayList<>();
            DBTable table = new DBTable();
            table.setName("CSM_OFFER");
            List<String> columns = new ArrayList<>();
            columns.add("ITEM_CD");
            columns.add("DESCRIPTION");
            columns.add("EFFECTIVE_DATE");
            columns.add("EXPIRATION_DATE");
            columns.add("ADDED1");
            columns.add("ADDED3");
            columns.add("ADDED2");
            columns.add("ADDED4");
            System.out.println(columns.toString());
            table.setColumnList(columns);
            columns= new ArrayList<>();   
            columns.add("SOC_CD");
            columns.add("SOC_NAME");
            System.out.println(columns.toString());
            table.setPrimaryColumnList(columns);
            columns= new ArrayList<>();
            dBTables.add(table);
            System.out.println("========"+table.getColumnList());
            System.out.println("========"+table.getPrimaryColumnList());
            table = null;
            table = new DBTable();
            table.setName("CSM_OFFER_ITEM");
            columns.add("ITEM_DESCRIPTION");
            columns.add("REVENUE_CODE");
            columns.add("ITEM_TYPE");
            columns.add("ADDED3");
            columns.add("ADDED2");
            columns.add("ADDED4");
            System.out.println(columns.toString());
            table.setColumnList(columns);
            columns= new ArrayList<>();        
            columns.add("SOC_CD");
            columns.add("ITEM_CD");
            System.out.println(columns.toString());
            table.setPrimaryColumnList(columns);
            columns= new ArrayList<>();
            dBTables.add(table);
            System.out.println("========"+table.getColumnList());
            System.out.println("========"+table.getPrimaryColumnList());
            DatabaseObjects objects = new DatabaseObjects();
            objects.setTables(dBTables);
            //boolean saveDBObject = new DatabaseDetailHelper().saveColumns(connName, schema, dBTables);
            boolean saveDBObject = new DatabaseDetailHelper().saveDBObject(connName, schema, objects);
            System.out.println("Saved or not? "+saveDBObject);*/
            //JOptionPane.showInputDialog(null, "Input daalo");
            /*String beyondComparePath = null;
            File f = new File("C:\\Program Files");
            File[] programFiles = f.listFiles();
            for (File file : programFiles) {
            if (file.getName().toUpperCase().startsWith("BEYOND COMPARE") && file.isDirectory()) {
            File[] bCompareFiles = file.listFiles();
            for (File bcFiles : bCompareFiles) {
            if (bcFiles.getName().equalsIgnoreCase("BCompare.exe")) {
            beyondComparePath = bcFiles.getAbsolutePath();
            break;
            }
            }
            break;
            }
            }
            if (beyondComparePath == null) {
            File f1 = new File("C:\\Program Files (x86)");
            File[] programFiles1 = f.listFiles();
            for (File file : programFiles) {
            if (file.getName().toUpperCase().startsWith("BEYOND COMPARE") && file.isDirectory()) {
            File[] bCompareFiles = file.listFiles();
            for (File bcFiles : bCompareFiles) {
            if (bcFiles.getName().equalsIgnoreCase("BCompare.exe")) {
            beyondComparePath = bcFiles.getAbsolutePath();
            break;
            }
            }
            break;
            }
            }
            }
            String contentToWrite = "\"C:\\Program Files\\Beyond Compare 4\\BCompare.exe\" /readonly ";
            System.out.println("contentToWrite: " + contentToWrite);
            String contentToWrite2 = "\"" + beyondComparePath + "\" /readonly ";
            System.out.println("contentToWrite: " + contentToWrite2);*/
            //JOptionPane.showConfirmDialog(null, "dsfkbadfaf", "adfadsf", JOptionPane.YES_OPTION);

            BufferedReader source = new BufferedReader(new FileReader(new File("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\source1XML\\BL1_XML_CONFIG-XML-1416")));
            BufferedReader target = new BufferedReader(new FileReader(new File("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\TempTableDump\\target1XML\\BL1_XML_CONFIG-XML-1416")));

            XMLUnit.setIgnoreWhitespace(true);
            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setCompareUnmatched(false);
            XMLUnit.setIgnoreComments(true);
            Diff xmlDiff = new Diff(source, target); //for getting detailed differences between two xml files
            DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
            List allDifferences = detailXmlDiff.getAllDifferences(); //showing differences found in two xml files 
            int totalDifferences = allDifferences.size();

            for (Iterator it = allDifferences.iterator(); it.hasNext();) {
                Difference object = (Difference) it.next();
                System.out.println("\t  ERROR in " + object.getDescription().toUpperCase() + " at location " + object.getControlNodeDetail().getXpathLocation());    
            }

            
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //fis2.close();

        }

    }

    public void doSomething() {

    }

    public static String toString(final Clob clob)
            throws SQLException, IOException {

        if (clob == null) {
            return "";
        }

        Long length = null;

        // try to get the oracle specific CLOB length
        // no vendor-specific code here.
        try {
            final Class<?> oracleClobClass = Class.forName("oracle.sql.CLOB");
            if (oracleClobClass.isInstance(clob)) {
                length = (Long) oracleClobClass.getMethod("getLength", (Class<?>) null)
                        .invoke(clob, (Object) null);
            }
        } catch (final ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            System.out.println(e.getStackTrace());
        }

        // we can set initial capacity if we got the length.
        final StringBuilder builder
                = length == null
                        ? new StringBuilder() : new StringBuilder(length.intValue());

        final BufferedReader reader
                = new BufferedReader(clob.getCharacterStream());
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line);
        }

        return builder.toString();
    }

    public static List compareXML(Reader source, Reader target) throws SAXException, IOException, org.xml.sax.SAXException {
        Diff xmlDiff = new Diff(source, target); //for getting detailed differences between two xml files
        DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
        detailXmlDiff.getAllDifferences();
        return detailXmlDiff.getAllDifferences();
    }

    public static void printDifferences(List differences) {

        int totalDifferences = differences.size();
        System.out.println("===============================");
        System.out.println("Total differences : " + totalDifferences);
        System.out.println("================================\n\n");
        for (Iterator it = differences.iterator(); it.hasNext();) {
            Difference object = (Difference) it.next();
            System.out.println(object.getControlNodeDetail().getValue());
            System.out.println(object.getControlNodeDetail().getXpathLocation());
            System.out.println(object.getDescription());
            if (!object.isRecoverable()) {
                String[] split = object.toString().split("- comparing");
                String mismatch = split[0];
                System.out.println("Mismatch:" + mismatch);
                System.out.println("Description:" + split[1] + "]");
                System.out.println("\n\n");
            }
        }
    }

}
