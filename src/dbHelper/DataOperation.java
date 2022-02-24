/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbHelper;

import FileHelper.ReportGenerator;
import XMLHelper.ConnectionHelper;
import componentBean.ComparisonTask;
import componentBean.DBConnection;
import componentBean.DBTable;
import componentBean.TaskSourceObject;
import gui.ColumnSelectionPage;
import gui.HomePage;
import java.awt.Component;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;
import oracle.jdbc.OracleTypes;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;
import xmlBean.Reference;

/**
 *
 * @author nimeshd
 */
public class DataOperation {

    //private static Connection conn = null;
    int primaryColumnCount;
    static int columnCount, done;
    static String directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
    static File file;
    static int counterForNoData = 1;
    static File errorFile;
    static Set<String> clobTables = new HashSet<>();
    static HashMap<String, List<File>> sourceClobData1 = new HashMap<>(), targetClobData1 = new HashMap<>();
    static HashMap<String, List<File>> sourceClobData2 = new HashMap<>(), targetClobData2 = new HashMap<>();
    static HashMap<String, List<File>> sourceClobData3 = new HashMap<>(), targetClobData3 = new HashMap<>();
    static String fileName;
    FileInputStream inputStream;
    XSSFWorkbook workbook = null;
    XSSFSheet workSheet = null;
    static List<String> errorTable = new ArrayList<>();
    static BufferedWriter bufferedWriter;
    static int rowCounter = 0, mismatchRow = 0, uniqueSourceRow = 0, uniqueTargetRow = 0;
    Set<String> sourceColumnData1 = new HashSet<>(), targetColumnData1 = new HashSet<>();
    Set<String> sourceColumnData2 = new HashSet<>(), targetColumnData2 = new HashSet<>();
    Set<String> sourceColumnData3 = new HashSet<>(), targetColumnData3 = new HashSet<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh_mm");
    static HashMap<Integer, String> hashMap = new HashMap<>();

    public DataOperation() {
        directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
        file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
        //errorFile = new File(directory + "\\Error-StackTrace_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
        File f = new File(directory);
        if (!f.exists()) {
            f.mkdir();
        } else {
            System.out.println("Folder exists");
        }
    }

    public static String returnDetailedReport() {
        return file.getAbsolutePath();
    }

    public static String returnFinalReport() {
        return fileName;
    }

    @SuppressWarnings("UnusedAssignment")
    public boolean getComparisonData(ComparisonTask task, final Connection sourceConn, final Connection targetConn, final String outputFileFormat, final String actionUnique, final String actiondifference, final String dataType, String outputPath, String missingTables, final Map<String, String> tableConstraints) {
        directory = outputPath.replace("%20", " ");
        file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
        if (outputFileFormat.equals("HTML")) {
            File f = new File(directory + "/HTML-Reports");
            if (!f.exists()) {
                f.mkdir();
            } else {
                File[] files = f.listFiles();
                for (File contentfile : files) {
                    if (contentfile.isFile()) {
                        if (!new File(directory + "/HTML-Reports/backup").exists()) {
                            new File(directory + "/HTML-Reports/backup").mkdir();
                        } else {
                            if (!contentfile.getName().startsWith("My")) {
                                try {
                                    Files.move(Paths.get(contentfile.getAbsolutePath()), Paths.get(directory + "/HTML-Reports/backup/" + contentfile.getName()), StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ex) {
                                    Logger.getLogger(DataOperation.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        /*switch (outputFileFormat) {
            case "TEXT":
                file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
                break;
            case "EXCEL":
                file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".xlsx");
                break;
            default:
                file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".html");
                break;
        }*/

        errorTable.clear();
        TaskSourceObject sourceObject = task.getSourceObject();
        final List<DBTable> tableList = sourceObject.getTables();
        String sourceDB = task.getSourceDBName().toUpperCase(), targetDB = task.getTargetDBName().toUpperCase();
        done = 0;
        final int listSize = tableList.size();
        boolean hashMapEmpty = true, allFailed = false;

        if (sourceConn != null && targetConn != null) {
            checkForNETEnvironments(sourceConn, sourceDB, targetConn, targetDB);
            Set<Integer> arr1 = new HashSet<>(), arr2 = new HashSet<>(), arr3 = new HashSet<>();
            for (int i = 0; i < tableList.size(); i++) {
                switch (i % 3) {
                    case 0:
                        arr1.add(i);
                        break;
                    case 1:
                        arr2.add(i);
                        break;
                    default:
                        arr3.add(i);
                        break;
                }
            }

            final Set<Integer> ar1 = new HashSet<>(arr1), ar2 = new HashSet<>(arr2), ar3 = new HashSet<>(arr3);
            final DataOperation operation1 = new DataOperation();
            Thread th1 = new Thread() {
                @Override
                public void run() {
                    try {
                        List<String> primaryColumns = new ArrayList<>();
                        for (Integer i : ar1) {
                            final DBTable table = tableList.get(i);
                            if (table.getPrimaryColumnList() != null) {
                                for (String pk : table.getPrimaryColumnList()) {
                                    if (!primaryColumns.contains(pk)) {
                                        primaryColumns.add(pk);
                                    }
                                }
                            }
                            String temp = null;
                            if(tableConstraints.containsKey(table.getName())){
                                temp = tableConstraints.get(table.getName());
                            }
                            final String constraint = temp;
                            
                            if ((table.getColumnList() != null && !table.getColumnList().isEmpty()) || (table.getPrimaryColumnList() != null && !table.getPrimaryColumnList().isEmpty())) {
                                long startTime = System.currentTimeMillis();
                                Thread sourceth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation1.getDBResults(sourceConn, table, "source1", dataType, 1, constraint);
                                    }
                                };
                                sourceth.start();
                                Thread targetth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation1.getDBResults(targetConn, table, "target1", dataType, 1, constraint);
                                    }
                                };
                                targetth.start();
                                System.out.println("Done loading data...");
                                sourceth.join();
                                targetth.join();
                                // Opening Result File for data
                                long totalTime = System.currentTimeMillis() - startTime;
                                System.out.println("Time Taken to getData : " + TimeUnit.MILLISECONDS.toSeconds(totalTime));
                                if (dataType.equals("NON-CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation1.operateOnColumnData(table, "TEXT", actionUnique, actiondifference, 1);
                                } else if (dataType.equals("CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation1.operateOnCLOBColumnData(table, "TEXT", actionUnique, actiondifference, 1);
                                }
                            }
                            System.out.println((++done) + " tables done out of " + listSize + " tables....");
                        }
                    } catch (InterruptedException ex) {
                        //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
                        Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            };
            th1.start();
            final DataOperation operation2 = new DataOperation();
            Thread th2 = new Thread() {
                @Override
                public void run() {
                    try {
                        List<String> primaryColumns = new ArrayList<>();
                        for (Integer i : ar2) {
                            final DBTable table = tableList.get(i);
                            if (table.getPrimaryColumnList() != null) {
                                for (String pk : table.getPrimaryColumnList()) {
                                    if (!primaryColumns.contains(pk)) {
                                        primaryColumns.add(pk);
                                    }
                                }
                            }
                            String temp = null;
                            if(tableConstraints.containsKey(table.getName())){
                                temp = tableConstraints.get(table.getName());
                            }
                            final String constraint = temp;
                            if ((table.getColumnList() != null && !table.getColumnList().isEmpty()) || (table.getPrimaryColumnList() != null && !table.getPrimaryColumnList().isEmpty())) {
                                long startTime = System.currentTimeMillis();
                                Thread sourceth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation2.getDBResults(sourceConn, table, "source2", dataType, 2, constraint);
                                    }
                                };
                                sourceth.start();
                                Thread targetth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation2.getDBResults(targetConn, table, "target2", dataType, 2, constraint);
                                    }
                                };
                                targetth.start();
                                System.out.println("Done loading data...");
                                sourceth.join();
                                targetth.join();
                                // Opening Result File for data

                                long totalTime = System.currentTimeMillis() - startTime;
                                System.out.println("Time Taken to getData : " + TimeUnit.MILLISECONDS.toSeconds(totalTime));
                                if (dataType.equals("NON-CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation2.operateOnColumnData(table, "TEXT", actionUnique, actiondifference, 2);
                                } else if (dataType.equals("CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation2.operateOnCLOBColumnData(table, "TEXT", actionUnique, actiondifference, 2);
                                }
                            }
                            System.out.println((++done) + " tables done out of " + listSize + " tables....");
                        }
                    } catch (InterruptedException ex) {
                        //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
                        Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            th2.start();
            final DataOperation operation3 = new DataOperation();
            Thread th3 = new Thread() {
                @Override
                public void run() {
                    try {
                        List<String> primaryColumns = new ArrayList<>();
                        for (Integer i : ar3) {
                            final DBTable table = tableList.get(i);
                            if (table.getPrimaryColumnList() != null) {
                                for (String pk : table.getPrimaryColumnList()) {
                                    if (!primaryColumns.contains(pk)) {
                                        primaryColumns.add(pk);
                                    }
                                }
                            }
                            String temp = null;
                            if(tableConstraints.containsKey(table.getName())){
                                temp = tableConstraints.get(table.getName());
                            }
                            final String constraint = temp;
                            if ((table.getColumnList() != null && !table.getColumnList().isEmpty()) || (table.getPrimaryColumnList() != null && !table.getPrimaryColumnList().isEmpty())) {
                                long startTime = System.currentTimeMillis();
                                Thread sourceth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation3.getDBResults(sourceConn, table, "source3", dataType, 3, constraint);
                                    }
                                };
                                sourceth.start();
                                Thread targetth = new Thread() {
                                    @Override
                                    public void run() {
                                        operation3.getDBResults(targetConn, table, "target3", dataType, 3, constraint);
                                    }
                                };
                                targetth.start();
                                System.out.println("Done loading data...");
                                sourceth.join();
                                targetth.join();
                                // Opening Result File for data
                                long totalTime = System.currentTimeMillis() - startTime;
                                System.out.println("Time Taken to getData : " + TimeUnit.MILLISECONDS.toSeconds(totalTime));
                                if (dataType.equals("NON-CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation3.operateOnColumnData(table, "TEXT", actionUnique, actiondifference, 3);
                                } else if (dataType.equals("CLOB") && !errorTable.contains(table.getName() + " has no columns selected")) {
                                    operation3.operateOnCLOBColumnData(table, "TEXT", actionUnique, actiondifference, 3);
                                }
                            }
                            System.out.println((++done) + " tables done out of " + listSize + " tables....");
                        }
                    } catch (InterruptedException ex) {
                        //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
                        Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            th3.start();
            try {
                th1.join();
                th2.join();
                th3.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(DataOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Finished processing all threads.");

            //Merging detailed results generated by Threads into one
            File dir = new File(directory);
            if (dir.listFiles() != null) {
                for (File threadFile : dir.listFiles()) {
                    if (threadFile.getName().startsWith("thread_report")) {
                        Scanner s = null;
                        try {
                            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                            bufferedWriter.newLine();
                            s = new Scanner(threadFile);
                            while (s.hasNextLine()) {
                                bufferedWriter.write(s.nextLine());
                                bufferedWriter.newLine();
                            }
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                            s.close();
                        } catch (IOException ex) {
                            if (s != null) {
                                s.close();
                            }
                            Logger.getLogger(DataOperation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        threadFile.delete();
                    }
                }
            }

            List list = new ArrayList(hashMap.keySet());
            Collections.sort(list, Collections.reverseOrder());
            File finalReport = new File(file.getParentFile() + "\\FINAL REPORT - " + file.getName());
            if (finalReport.exists()) {
                finalReport = new File(file.getParentFile() + "\\FINAL REPORT - Report_" + sdf.format(new Date(System.currentTimeMillis())) + "_1.txt");
            }
            fileName = finalReport.getAbsolutePath();
            try {

                String format = "%1$-30s|\t%2$-13s|\t%3$-13s|\t%4$-13s";
                bufferedWriter = new BufferedWriter(new FileWriter(finalReport, true));
                bufferedWriter.newLine();
                bufferedWriter.write("================================================================================");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("                              FINAL RESULTS                                     ");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("================================================================================");
                bufferedWriter.newLine();
                bufferedWriter.write("->    SOURCE CONNECTION : " + sourceDB);
                bufferedWriter.newLine();
                bufferedWriter.write("->    TARGET CONNECTION : " + targetDB);
                bufferedWriter.newLine();
                bufferedWriter.write("================================================================================");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("#MISMATCHES (Data in both source and target with minor differences)");
                bufferedWriter.newLine();
                bufferedWriter.write("#UNIQUESOURCE (Data in found only in source)");
                bufferedWriter.newLine();
                bufferedWriter.write("#UNIQUETARGET (Data in found only in target)");
                bufferedWriter.newLine();
                bufferedWriter.write("#TOTALCONFLICTS (Total of all unique and mismatched data)");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(String.format(format, "TableName", "Mismatches", "UniqueSource", "UniqueTarget") + " | \t TotalConflicts");
                bufferedWriter.newLine();
                if (hashMap.isEmpty()) {
                    String data = String.format(format, "NA", "-", "-", "-");
                    bufferedWriter.write(data + " | \t -");
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.write("No conflicts found.");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String tableData = hashMap.get(list.get(i));
                        String[] split = tableData.split("@");
                        String data = String.format(format, split[0], split[1], split[2], split[3]);
                        bufferedWriter.write(data + " | \t " + list.get(i));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    hashMapEmpty = false;
                }
                bufferedWriter.newLine();
                if (errorTable.size() > 0 || missingTables != null) {
                    bufferedWriter.write("The comparison failed for following tables due to database error. Please check these following tables separately...");
                    bufferedWriter.newLine();
                    if (errorTable.size() > 0) {
                        for (String s : errorTable) {
                            if (s != null) {
                                bufferedWriter.write(s);
                                bufferedWriter.newLine();
                            }
                        }
                        if (hashMapEmpty) {
                            allFailed = true;
                        }
                    }
                    if (missingTables != null) {
                        String[] missingTableList = missingTables.split(",");
                        bufferedWriter.newLine();
                        for (String s : missingTableList) {
                            bufferedWriter.write("-- " + s.trim());
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.newLine();
                    }
                }
                bufferedWriter.close();

                if (!hashMap.isEmpty()) {
                    initializeHTMLReports(outputFileFormat, dataType, finalReport, file);
                }
            } catch (IOException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            list.clear();
            hashMap.clear();

            return !allFailed;
        }
        System.out.println("============== HEAP SIZE " + Runtime.getRuntime().freeMemory() + "===========");
        return false;
    }

    @SuppressWarnings({"UnusedAssignment", "null", "ImplicitArrayToString"})
    public void getDBResults(Connection conn, DBTable table, String connName, String dataType, int threadNum, String constraint) {
        switch (threadNum) {
            case 1:
                if (connName.equals("source1")) {
                    sourceClobData1.clear();
                    sourceColumnData1.clear();
                }
                if (connName.equals("target1")) {
                    targetColumnData1.clear();
                    targetClobData1.clear();
                }
                break;
            case 2:
                if (connName.equals("source2")) {
                    sourceClobData2.clear();
                    sourceColumnData2.clear();
                }
                if (connName.equals("target2")) {
                    targetColumnData2.clear();
                    targetClobData2.clear();
                }
                break;
            case 3:
                if (connName.equals("source3")) {
                    sourceClobData3.clear();
                    sourceColumnData3.clear();
                }
                if (connName.equals("target3")) {
                    targetColumnData3.clear();
                    targetClobData3.clear();
                }
                break;
        }
        String[] dBData = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        HashMap<String, List<File>> mp = new HashMap<>();
        String path = null;
        HashMap<String, List<String>> clobDataMap = null;
        if (dataType.equals("CLOB")) {

            clobDataMap = new DatabaseHelper().getClobData();

            path = System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump";
            String timestamp = sdf.format(new Date(System.currentTimeMillis()));
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
                new File(System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML").mkdir();
                path = System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML";
            } else {
                if (!new File(System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML").exists()) {
                    new File(System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML").mkdir();
                    path = System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML";
                } else {
                    path = System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML";
                    File pathdir = new File(path);
                    if (pathdir.listFiles() != null && pathdir.listFiles().length > 0) {
                        File backup = new File(System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML\\bkp_" + timestamp);
                        for (File contentFile : pathdir.listFiles()) {
                            if (contentFile != null && contentFile.isFile()) {
                                try {
                                    if (!backup.exists()) {
                                        new File(System.getProperty("user.home") + "\\Desktop\\DBComp-Results\\TempTableDump\\" + connName + "XML\\bkp_" + timestamp).mkdir();
                                    }
                                    Files.move(Paths.get(contentFile.getAbsolutePath()), Paths.get(path + "/bkp_" + timestamp + "/" + contentFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ex) {
                                    Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                }

            }
        }
        try {
            int count = 0;
            List<String> primaryColumns = new ArrayList<>();
            if (table.getPrimaryColumnList() == null) {
                primaryColumns.clear();
                primaryColumns.add("1");
            } else {
                for (String pk : table.getPrimaryColumnList()) {
                    if (!primaryColumns.contains(pk)) {
                        primaryColumns.add(pk);
                    }
                }
            }
            if(constraint == null){
                constraint = "1=1";
            }
            List<String> columnList = new ArrayList<>(table.getColumnList());
            String query = "SELECT count(*) FROM " + table.getName() +" WHERE "+constraint+"";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            System.out.println("Found " + count + " data in the table " + table.getName());
            String columns = null, primaryCols = primaryColumns.toString().replace("[", "").replace("]", ""), clobCols = null;

            //Checking if Table is all columns
            if ((primaryCols == null || primaryCols.isEmpty()) && (columnList == null || columnList.isEmpty())) {
                JOptionPane.showMessageDialog(null, "No columns found for this table");
                errorTable.add(table.getName() + " with error: No columns found");
            } else if (dataType.equals("CLOB")) {
                clobCols = clobDataMap.get(table.getName()).toString().replace("[", "").replace("]", "");
                query = "SELECT " + primaryCols + ", " + clobCols + " FROM " + table.getName() + " WHERE "+constraint+" ORDER BY 1 DESC";
            } else if (columnList == null || columnList.isEmpty()) {
                query = "SELECT " + primaryCols + " FROM " + table.getName() + " WHERE "+constraint+" ORDER BY 1 DESC";
            } else if (primaryCols == null || primaryCols.isEmpty()) {
                columns = table.getColumnList().toString().replace("[", "").replace("]", "");
                query = "SELECT " + columns + " FROM " + table.getName() + " WHERE "+constraint+" ORDER BY 1 DESC";
            } else {
                columns = table.getColumnList().toString().replace("[", "").replace("]", "");
                query = "SELECT " + primaryCols + ", " + columns + " FROM " + table.getName() + " WHERE "+constraint+" ORDER BY " + primaryCols + " DESC";
            }

            //query = "SELECT " + primaryCols + " FROM " + table.getName() + " where SCHEMA like '%FILEFMTDEF%' ORDER BY 1 DESC";
            System.out.println("Executing Query:" + query);
            ps = conn.prepareStatement(query);

            if (count < 1000) {
                ps.setFetchSize(300);
            } else if (count < 10000) {
                ps.setFetchSize(3000);
            } else if (count < 50000) {
                ps.setFetchSize(5000);
            } else {
                ps.setFetchSize(20000);
            }

            dBData = new String[count];

            long startTime = System.currentTimeMillis();
            rs = ps.executeQuery();
            long timeTaken = startTime - (System.currentTimeMillis());
            System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to process the query.");
            ResultSetMetaData metaData = rs.getMetaData();
            primaryColumnCount = primaryColumns.size();
            columnCount = metaData.getColumnCount();
            int j = 0;
            Clob clobData = null;
            Blob blobData = null;
            //StringBuilder data;
            if (rs != null) {
                int dumpCount = 0;
                if (dataType.equals("NON-CLOB")) {
                    ColumnSelectionPage.jLabel7.setText("Fetching records: 0 records done");
                    while (j < count && rs.next()) {
                        String data = "";
                        long time = System.currentTimeMillis();
                        //data = new StringBuilder("");
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            int type = metaData.getColumnType(i);
                            switch (type) {
                                case OracleTypes.CHAR:
                                case OracleTypes.VARCHAR:
                                    //data += String.valueOf(rs.getString(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (rs.getString(i).replace(" ", "_")) + (" ");
                                    } else {
                                        data += (rs.getString(i)) + (" ");
                                    }
                                    break;
                                case OracleTypes.DATE:
                                case OracleTypes.TIMESTAMP:
                                    //data += String.valueOf(rs.getTime(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (rs.getTimestamp(i).toString().replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    break;
                                case OracleTypes.INTEGER:
                                case OracleTypes.NUMERIC:
                                    //data += String.valueOf(rs.getLong(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (String.valueOf(rs.getBigDecimal(i)).replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    break;
                                case OracleTypes.FLOAT:
                                case OracleTypes.DOUBLE:
                                    //data += String.valueOf(rs.getDouble(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (String.valueOf(rs.getDouble(i)).replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    System.out.println("String RS.GETDOUBLE " + String.valueOf(rs.getDouble(i)));
                                    System.out.println("RS.GETDOUBLE " + rs.getDouble(i));
                                    break;
                                case OracleTypes.CLOB:
                                    data += "CLOB DATA SKIPPED.";
                                    //data += ("XML_DATA_FOUND_IN_THIS_COLUMN.CONSIDERING_THE_COMPLEXITY,_IT_WILL_BE_SKIPPED.") + (" ");
                                    break;
                                default:
                                    data += "CANNOT HANDLE THIS DATA_TYPE";
                                    break;
                            }
                        }
                        //data = new StringBuilder(data.toString().trim().replaceAll(" ", "|"));
                        data = data.trim().replaceAll(" ", " | ");
                        dBData[j++] = data;
                        //list.add(data);
                        dumpCount++;
                        if (connName.contains("source")) {
                            ColumnSelectionPage.valueUpdate(dumpCount, count, "source");
                        } else {
                            ColumnSelectionPage.valueUpdate(dumpCount, count, "target");
                        }

                    }
                    System.out.println("DBData current size : " + dBData.length);
                } else if (dataType.equals("CLOB")) {
                    File f = null;
                    int fileNameCounter = 1;
                    while (j < count && rs.next()) {
                        List<File> files = new ArrayList<>();
                        String data = "", text = "", nameOfFile = null;
                        long time = System.currentTimeMillis();
                        //data = new StringBuilder("");
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            int type = metaData.getColumnType(i);
                            String columnName = metaData.getColumnName(i);
                            switch (type) {
                                case OracleTypes.CHAR:
                                case OracleTypes.VARCHAR:
                                    //data += String.valueOf(rs.getString(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (rs.getString(i).replace(" ", "_")) + (" ");
                                    } else {
                                        data += (rs.getString(i)) + (" ");
                                    }
                                    break;
                                case OracleTypes.DATE:
                                case OracleTypes.TIMESTAMP:
                                    //data += String.valueOf(rs.getTime(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (rs.getTimestamp(i).toString().replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    break;
                                case OracleTypes.INTEGER:
                                case OracleTypes.NUMERIC:
                                    //data += String.valueOf(rs.getLong(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (String.valueOf(rs.getLong(i)).replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    break;
                                case OracleTypes.FLOAT:
                                case OracleTypes.DOUBLE:
                                    //data += String.valueOf(rs.getDouble(i)) + " ";
                                    if (rs.getString(i) != null) {
                                        data += (String.valueOf(rs.getDouble(i)).replace(" ", "_")) + (" ");
                                    } else {
                                        data += null + (" ");
                                    }
                                    break;
                                case Types.CLOB:
                                    clobData = rs.getClob(i);
                                    nameOfFile = table.getName() + "-" + columnName + "-" + fileNameCounter;
                                    f = new File(path + "\\" + nameOfFile);
                                    f.createNewFile();
                                    fileNameCounter++;
                                    try (PrintWriter out = new PrintWriter(f)) {
                                        if (clobData == null || clobData.getSubString(1, (int) clobData.length()).isEmpty()) {
                                            text = "NULL";
                                            out.println(text);
                                        } else {
                                            out.println(clobData.getSubString(1, (int) clobData.length()));
                                        }
                                        out.close();
                                        files.add(f);
                                    } catch (FileNotFoundException ex) {
                                        Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    //data += "XML DATA FOUND IN THIS COLUMN. CONSIDERING THE COMPLEXITY, IT WILL BE SKIPPED.";
                                    //data += ("XML_DATA_FOUND_IN_THIS_COLUMN.CONSIDERING_THE_COMPLEXITY,_IT_WILL_BE_SKIPPED.") + (" ");
                                    break;
                                case Types.BLOB:
                                    blobData = rs.getBlob(i);

                                    nameOfFile = table.getName() + "-BLOB-" + columnName + "-" + fileNameCounter;
                                    f = new File(path + "\\" + nameOfFile);
                                    f.createNewFile();
                                    fileNameCounter++;
                                    try (PrintWriter out = new PrintWriter(f)) {
                                        if (blobData == null || blobData.getBytes(1, (int) blobData.length()).length <= 0) {
                                            text = "NULL";
                                            out.println(text);
                                        } else {
                                            int index = 1, blobLength = (int) blobData.length();
                                            while (index < blobLength) {
                                                out.println(blobData.getBytes(index, (index + 511)));
                                                out.println();
                                                index += 512;
                                            }
                                            index -= 512;
                                            if (index < blobLength) {
                                                out.println(blobData.getBytes(index, blobLength));
                                            }
                                            //out.println(Arrays.toString(blobData.getBytes(1, (int) blobData.length())));
                                        }
                                        out.close();
                                        files.add(f);
                                    } catch (FileNotFoundException ex) {
                                        Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                default:
                                    data += "DATA_TYPE_OF_THIS_FIELD_NOT_RECOGNIZED";
                                    break;
                            }
                        }
                        data = data.trim().replaceAll(" ", " | ");
                        if (clobData != null || blobData != null) {
                            if (data == null || data.isEmpty()) {
                                data = "" + (counterForNoData++) + "";
                            }
                            mp.put(data, files);
                        }
                        dumpCount++;
                        if (connName.contains("source")) {
                            ColumnSelectionPage.valueUpdate(dumpCount, count, "source");
                        } else {
                            ColumnSelectionPage.valueUpdate(dumpCount, count, "target");
                        }
                    }
                }
            }
            ps.close();
            rs.close();
            metaData = null;
            timeTaken = startTime - (System.currentTimeMillis());
            System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to populate the data");
            primaryCols = null;
            primaryColumns.clear();
            primaryColumns = null;
            columnList.clear();
            columnList = null;
            System.out.println("");

            if (dataType.equals("NON-CLOB")) {
                switch (threadNum) {
                    case 1:
                        if (connName.equals("source1")) {
                            sourceColumnData1.addAll(Arrays.asList(dBData));
                        } else if (connName.equals("target1")) {
                            targetColumnData1.addAll(Arrays.asList(dBData));
                        }
                        break;
                    case 2:
                        if (connName.equals("source2")) {
                            sourceColumnData2.addAll(Arrays.asList(dBData));
                        } else if (connName.equals("target2")) {
                            targetColumnData2.addAll(Arrays.asList(dBData));
                        }
                        break;
                    case 3:
                        if (connName.equals("source3")) {
                            sourceColumnData3.addAll(Arrays.asList(dBData));
                        } else if (connName.equals("target3")) {
                            targetColumnData3.addAll(Arrays.asList(dBData));
                        }
                        break;
                }
            } else if (dataType.equals("CLOB")) {
                switch (threadNum) {
                    case 1:
                        if (connName.equals("source1")) {
                            System.out.println("SourceMap size : " + mp.size());
                            for (String s : mp.keySet()) {
                                sourceClobData1.put(s, mp.get(s));
                            }
                        } else if (connName.equals("target1")) {
                            System.out.println("TargetMap size : " + mp.size());
                            for (String s : mp.keySet()) {
                                targetClobData1.put(s, mp.get(s));
                            }
                        }
                        break;
                    case 2:
                        if (connName.equals("source2")) {
                            for (String s : mp.keySet()) {
                                sourceClobData2.put(s, mp.get(s));
                            }
                        } else if (connName.equals("target2")) {
                            for (String s : mp.keySet()) {
                                targetClobData2.put(s, mp.get(s));
                            }
                        }
                        break;
                    case 3:
                        if (connName.equals("source3")) {
                            for (String s : mp.keySet()) {
                                sourceClobData3.put(s, mp.get(s));
                            }
                        } else if (connName.equals("target3")) {
                            for (String s : mp.keySet()) {
                                targetClobData3.put(s, mp.get(s));
                            }
                        }
                        break;
                }
            }


        } catch (SQLException | IOException ex) {
            //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
            //Logger.getLogger(DatabaseHelperTest.class.getName()).log(Level.SEVERE, null, ex);
            errorTable.add(table.getName() + " with error:" + ex.getMessage());
        } catch (NullPointerException ex) {
            errorTable.add(table.getName() + " with error:" + ex.getMessage());
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Exception e) {
            errorTable.add(table.getName() + " with error:" + e.getMessage());
        } finally {
            try {
                if (rs != null || ps != null) {
                    rs.close();
                    ps.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        mp.clear();
        dBData = null;
        System.gc();
    }

    private void operateOnColumnData(DBTable table, String outputFileFormat, String actionUnique, String actiondifference, int threadNum) {
        String tableName = table.getName();
        File f = new File(directory + "//thread_report" + threadNum);
        BufferedWriter writer = null;
        boolean showSourceUnique = false, showTargetUnique = false, showAllMismatch = false;

        switch (actionUnique.toUpperCase()) {
            case "BOTH":
                showSourceUnique = true;
                showTargetUnique = true;
                break;
            case "SOURCE":
                showSourceUnique = true;
                break;
            case "TARGET":
                showTargetUnique = true;
                break;
            default:
                break;
        }

        if (actiondifference.toUpperCase().equals("ALL")) {
            showAllMismatch = true;
        }

        try {
            List<String> primaryColumns = new ArrayList<>();
            if (table.getPrimaryColumnList().size() > 0) {
                for (String pk : table.getPrimaryColumnList()) {
                    if (!primaryColumns.contains(pk)) {
                        primaryColumns.add(pk);
                    }
                }
            }

            writer = new BufferedWriter(new FileWriter(f, true));
            if (outputFileFormat.equals("TEXT")) {
                writer.write("Results for Table " + tableName + " on "
                        + "Columns ( " + primaryColumns.toString().replace("[", "").replace("]", "") + ", " + table.getColumnList().toString().replace("[", "").replace("]", "") + " )");
                writer.newLine();
            } else {

            }

            Integer integer;
            int sourceCount = 0, targetCount = 0, mismatchCount = 0;

            // Making temporary tables for processing
            Set<String> tempSource = null, tempTarget = null;
            switch (threadNum) {
                case 1:
                    tempSource = new HashSet<>(sourceColumnData1);
                    tempTarget = new HashSet<>(targetColumnData1);

                    System.out.println("Fetched source size:" + tempSource.size());
                    System.out.println("Fetched target size:" + tempTarget.size());

                    tempSource.removeAll(targetColumnData1);
                    tempTarget.removeAll(sourceColumnData1);

                    System.out.println("Conflicting Source:" + tempSource.size());
                    System.out.println("Conflicting Target:" + tempTarget.size());

                    break;
                case 2:
                    tempSource = new HashSet<>(sourceColumnData2);
                    tempTarget = new HashSet<>(targetColumnData2);

                    System.out.println("Fetched source size:" + tempSource.size());
                    System.out.println("Fetched target size:" + tempTarget.size());

                    tempSource.removeAll(targetColumnData2);
                    tempTarget.removeAll(sourceColumnData2);

                    System.out.println("Conflicting Source:" + tempSource.size());
                    System.out.println("Conflicting Target:" + tempTarget.size());

                    break;
                case 3:
                    tempSource = new HashSet<>(sourceColumnData3);
                    tempTarget = new HashSet<>(targetColumnData3);

                    System.out.println("Fetched source size:" + tempSource.size());
                    System.out.println("Fetched target size:" + tempTarget.size());

                    tempSource.removeAll(targetColumnData3);
                    tempTarget.removeAll(sourceColumnData3);

                    System.out.println("Conflicting Source:" + tempSource.size());
                    System.out.println("Conflicting Target:" + tempTarget.size());

                    break;
            }
            // Removing target similar data from source 

            Set<String> sourceConflict = new HashSet<>(tempSource);
            tempSource.clear();
            if (outputFileFormat.equals("TEXT")) {
                writer.newLine();
                writer.write("Conflicting or Unique Source Entity Count -- " + sourceConflict.size());
            }
            // Removing source similar data from target 
            Set<String> targetConflict = new HashSet<>(tempTarget);
            tempTarget.clear();
            if (outputFileFormat.equals("TEXT")) {
                writer.newLine();
                writer.write("Conflicting or Unique Target Entity Count -- " + targetConflict.size());
            }
            String sourcePrimaryColData = null, targetPrimaryColData = null;
            boolean toBeSkipped = false;

            long startTime = System.currentTimeMillis();
            if (sourceConflict.size() > 0 && targetConflict.size() > 0) {
                Set<String> uniqueSourceData = new HashSet<>(sourceConflict);
                Set<String> uniqueTargetData = new HashSet<>(targetConflict);

                if (showAllMismatch || (sourceConflict.size() < 3000 || targetConflict.size() < 3000)) {
                    for (String s : sourceConflict) {
                        if (s != null) {
                            int count = 0;
                            for (int i = 0; i < s.length(); i++) {
                                if (s.charAt(i) == '|') {
                                    count += 1;
                                }
                                if (i > 0 && count == primaryColumnCount) {
                                    sourcePrimaryColData = s.substring(0, i - 1);
                                    break;
                                }
                            }
                            if (sourcePrimaryColData == null) {
                                sourcePrimaryColData = s;
                                System.out.println("primaryColData is null");
                                //System.out.println("NULL Primary data to be searched upon target result" + primaryColData);
                            }
                            //System.out.println("Primary Data to be tested against Target Value : " + primaryColData);
                            for (String targetData : targetConflict) {
                                if (targetData != null) {
                                    count = 0;
                                    for (int i = 0; i < targetData.length(); i++) {
                                        if (targetData.charAt(i) == '|') {
                                            count += 1;
                                        }
                                        if (i > 0 && count == primaryColumnCount) {
                                            targetPrimaryColData = targetData.substring(0, i - 1);
                                            break;
                                        }
                                    }
                                    if (targetData.contains(sourcePrimaryColData) && targetPrimaryColData.equalsIgnoreCase(sourcePrimaryColData)) {

                                        System.out.println("Found source-target conflict");
                                        if (outputFileFormat.equals("TEXT")) {
                                            writer.newLine();
                                            writer.write("Source -- " + s);
                                            writer.newLine();
                                            writer.write("Target -- " + targetData);
                                            writer.newLine();
                                            writer.newLine();
                                            writer.flush();
                                        }

                                        //targetConflict.remove(targetData);
                                        uniqueTargetData.remove(targetData);
                                        uniqueSourceData.remove(s);
                                        mismatchCount++;
                                    }

                                }
                            }
                        }
                    }
                } else {
                    toBeSkipped = true;
                    integer = sourceConflict.size() + targetConflict.size();
                    mismatchCount = integer;
                    if (outputFileFormat.equals("TEXT")) {
                        writer.newLine();
                        writer.write("Table Comparison skipped due to massive mismatch (more than 3000) -- Found " + sourceConflict.size() + " source mismatches and " + targetConflict.size() + "target mismatches ");
                        writer.newLine();
                        writer.newLine();
                        writer.flush();
                    }
                }
                if (uniqueSourceData.isEmpty() && uniqueTargetData.isEmpty()) {
                    if (outputFileFormat.equals("TEXT")) {
                        writer.newLine();
                        writer.write("*********************************************");
                        writer.newLine();
                        writer.write("Both Source and Target DBs have same data....");
                        writer.newLine();
                        writer.write("*********************************************");
                        writer.newLine();
                        writer.flush();
                    }
                }
                if (!uniqueSourceData.isEmpty() && !toBeSkipped) {
                    if (outputFileFormat.equals("TEXT") && showSourceUnique) {
                        writer.newLine();
                        writer.write("******************************************");
                        writer.newLine();
                        writer.write("Source DBs has some additional data as....");
                        writer.newLine();
                        for (String s : uniqueSourceData) {
                            if (s != null) {
                                writer.write(s);
                                writer.newLine();
                                writer.flush();
                                sourceCount++;
                            }
                        }
                        writer.write("*********************************************");
                    }
                    uniqueSourceData.clear();
                }
                if (!uniqueTargetData.isEmpty() && !toBeSkipped) {
                    if (outputFileFormat.equals("TEXT") && showTargetUnique) {
                        writer.newLine();
                        writer.write("******************************************");
                        writer.newLine();
                        writer.write("Target DBs has some additional data as....");
                        writer.newLine();
                        for (String s : uniqueTargetData) {
                            if (s != null) {
                                writer.write(s);
                                writer.newLine();
                                writer.flush();
                                targetCount++;
                            }
                        }
                        writer.write("*********************************************");
                    }
                    uniqueTargetData.clear();
                }
                long timeTaken = startTime - (System.currentTimeMillis());
                System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to compare the data");
            } else if (sourceConflict.isEmpty() && targetConflict.isEmpty()) {
                if (outputFileFormat.equals("TEXT")) {
                    writer.newLine();
                    writer.write("*********************************************");
                    writer.newLine();
                    writer.write("Both Source and Target DBs have same data....");
                    writer.newLine();
                    writer.write("*********************************************");
                    writer.newLine();
                    writer.flush();
                }
            } else if (sourceConflict.size() > 0) {
                if (outputFileFormat.equals("TEXT") && showSourceUnique) {
                    writer.newLine();
                    writer.write("******************************************");
                    writer.newLine();
                    writer.write("Source DBs has some additional data as....");
                    writer.newLine();
                    for (String s : sourceConflict) {
                        if (s != null) {
                            writer.write(s);
                            writer.newLine();
                            writer.flush();
                            sourceCount++;
                        }
                    }
                    writer.write("*********************************************");
                }
                sourceConflict.clear();
            } else if (targetConflict.size() > 0) {
                if (outputFileFormat.equals("TEXT") && showTargetUnique) {
                    writer.newLine();
                    writer.write("******************************************");
                    writer.newLine();
                    writer.write("Target DBs has some additional data as....");
                    writer.newLine();
                    for (String s : targetConflict) {
                        if (s != null) {
                            writer.write(s);
                            writer.newLine();
                            writer.flush();
                            targetCount++;
                        }
                    }
                    writer.write("*********************************************");
                }
                targetConflict.clear();
            }
            integer = mismatchCount + sourceCount + targetCount;
            if (integer > 0) {
                hashMap.put(integer, tableName + "@" + mismatchCount + "@" + sourceCount + "@" + targetCount);
            }

            if (outputFileFormat.equals("TEXT")) {
                writer.newLine();
                writer.write("================================================================================");
                writer.newLine();
                writer.close();
            }
        } catch (IOException ex) {
            errorTable.add(tableName + " with error:" + ex.getMessage());
            try {
                if (outputFileFormat.equals("TEXT")) {
                    writer.close();
                }
            } catch (IOException ex1) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex1);
            }
            //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex.getStackTrace());
        } catch (Exception e) {
            errorTable.add(tableName + " with error:" + e.getMessage());
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        System.gc();
    }

    private void operateOnCLOBColumnData(DBTable table, String outputFileFormat, String actionUnique, String actiondifference, int threadNum) {
        String tableName = table.getName();
        File f = new File(directory + "//thread_report" + threadNum);
        BufferedWriter writer = null;
        boolean showSourceUnique = false, showTargetUnique = false, showAllMismatch = false;

        switch (actionUnique.toUpperCase()) {
            case "BOTH":
                showSourceUnique = true;
                showTargetUnique = true;
                break;
            case "SOURCE":
                showSourceUnique = true;
                break;
            case "TARGET":
                showTargetUnique = true;
                break;
            default:
                break;
        }

        if (actiondifference.toUpperCase().equals("ALL")) {
            showAllMismatch = true;
        }

        try {
            List<String> primaryColumns = new ArrayList<>();
            if (table.getPrimaryColumnList().size() > 0) {
                for (String pk : table.getPrimaryColumnList()) {
                    if (!primaryColumns.contains(pk)) {
                        primaryColumns.add(pk);
                    }
                }
            }

            writer = new BufferedWriter(new FileWriter(f, true));
            HashMap<String, List<String>> clobDataMap = new DatabaseHelper().getClobData();
            String clobCols = clobDataMap.get(table.getName()).toString().replace("[", "").replace("]", "");

            if (outputFileFormat.equals("TEXT")) {
                writer.write("Results for Table " + tableName + " on Columns ( " + primaryColumns.toString().replace("[", "").replace("]", "") + ", " + clobCols + " )");
                writer.newLine();
            }

            Integer integer = 0;
            long startTime = 0, timeTaken = 0;
            int sourceCount = 0, targetCount = 0, mismatchCount = 0;

            HashMap<String, List<File>> tempUniqueSourceMap = null, tempUniqueTargetMap = null;
            HashMap<String, List<File>> conflictSourceMap = new HashMap<>(), conflictTargetMap = new HashMap<>();

            switch (threadNum) {
                case 1:
                    tempUniqueSourceMap = new HashMap<>(sourceClobData1);
                    tempUniqueTargetMap = new HashMap<>(targetClobData1);

                    System.out.println("Amount of data fetched from source:" + tempUniqueSourceMap.size());
                    System.out.println("Amount of data fetched from target:" + tempUniqueTargetMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueSourceMap.size() > 0) {
                        for (String s : targetClobData1.keySet()) {
                            if (tempUniqueSourceMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueSourceMap.get(s));
                                conflictTargetMap.put(s, targetClobData1.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique source content");
                    System.out.println("tempSourceMap size:" + tempUniqueSourceMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueTargetMap.size() > 0) {
                        for (String s : sourceClobData1.keySet()) {
                            if (tempUniqueTargetMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueTargetMap.get(s));
                                conflictTargetMap.put(s, sourceClobData1.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique target content");
                    System.out.println("tempSourceMap size:" + tempUniqueTargetMap.size());

                    break;
                case 2:
                    tempUniqueSourceMap = new HashMap<>(sourceClobData2);
                    tempUniqueTargetMap = new HashMap<>(targetClobData2);

                    System.out.println("Amount of data fetched from source:" + tempUniqueSourceMap.size());
                    System.out.println("Amount of data fetched from target:" + tempUniqueTargetMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueSourceMap.size() > 0) {
                        for (String s : targetClobData2.keySet()) {
                            if (tempUniqueSourceMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueSourceMap.get(s));
                                conflictTargetMap.put(s, targetClobData2.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique source content");
                    System.out.println("tempSourceMap size:" + tempUniqueSourceMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueTargetMap.size() > 0) {
                        for (String s : sourceClobData2.keySet()) {
                            if (tempUniqueTargetMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueTargetMap.get(s));
                                conflictTargetMap.put(s, sourceClobData2.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique target content");
                    System.out.println("tempSourceMap size:" + tempUniqueTargetMap.size());

                    break;
                case 3:
                    tempUniqueSourceMap = new HashMap<>(sourceClobData3);
                    tempUniqueTargetMap = new HashMap<>(targetClobData3);

                    System.out.println("Amount of data fetched from source:" + tempUniqueSourceMap.size());
                    System.out.println("Amount of data fetched from target:" + tempUniqueTargetMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueSourceMap.size() > 0) {
                        for (String s : targetClobData3.keySet()) {
                            if (tempUniqueSourceMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueSourceMap.get(s));
                                conflictTargetMap.put(s, targetClobData3.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique source content");
                    System.out.println("tempUniqueSourceMap size:" + tempUniqueSourceMap.size());

                    startTime = System.currentTimeMillis();
                    if (tempUniqueTargetMap.size() > 0) {
                        for (String s : sourceClobData3.keySet()) {
                            if (tempUniqueTargetMap.containsKey(s)) {
                                conflictSourceMap.put(s, tempUniqueTargetMap.get(s));
                                conflictTargetMap.put(s, sourceClobData3.get(s));
                                tempUniqueSourceMap.remove(s);
                                tempUniqueTargetMap.remove(s);
                            }
                        }
                    }
                    timeTaken = startTime - (System.currentTimeMillis());
                    System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to find unique target content");
                    System.out.println("tempUniqueTargetMap size:" + tempUniqueTargetMap.size());
                    break;
            }

            if (outputFileFormat.equals("TEXT")) {
                writer.newLine();
                writer.write("Unique Source Entity Count -- " + tempUniqueSourceMap.size());

                writer.newLine();
                writer.write("Unique Target Entity Count -- " + tempUniqueTargetMap.size());
                writer.newLine();
            }
            boolean toBeSkipped = false;

            startTime = System.currentTimeMillis();
            if (conflictSourceMap.size() > 0 && conflictTargetMap.size() > 0) {
                if (showAllMismatch || (conflictSourceMap.size() < 3000 || conflictTargetMap.size() < 3000)) {
                    for (String s : conflictSourceMap.keySet()) {
                        if (s != null && conflictTargetMap.containsKey(s)) {
                            List<File> sourceFiles = conflictSourceMap.get(s);
                            List<File> targetFiles = conflictTargetMap.get(s);

                            if (sourceFiles.size() == targetFiles.size()) {
                                for (File sf : sourceFiles) {
                                    int index = sourceFiles.indexOf(sf);
                                    File tf = targetFiles.get(index);
                                    if (sf != null && tf != null) {
                                        BufferedReader source, target;
                                        source = new BufferedReader(new InputStreamReader(new FileInputStream(sf)));
                                        target = new BufferedReader(new InputStreamReader(new FileInputStream(tf)));
                                        XMLUnit.setIgnoreWhitespace(true);
                                        XMLUnit.setIgnoreAttributeOrder(true);
                                        XMLUnit.setCompareUnmatched(false);
                                        XMLUnit.setIgnoreComments(true);
                                        Diff xmlDiff;
                                        int totalDifferences = 0;
                                        try {
                                            if (sf.getName().contains("BLOB")) {
                                                System.out.println("Handling Blob file");
                                                throw new SAXException();
                                            }
                                            xmlDiff = new Diff(source, target); //for getting detailed differences between two xml files
                                            DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);
                                            List allDifferences = detailXmlDiff.getAllDifferences(); //showing differences found in two xml files 
                                            totalDifferences = allDifferences.size();
                                            int i = 0;
                                            if (totalDifferences > 0) {
                                                System.out.println("totalDifferences:" + totalDifferences);
                                                if (outputFileFormat.equals("TEXT")) {
                                                    writer.newLine();
                                                    writer.write("Error in RECORD - " + s);
                                                    writer.newLine();
                                                    writer.write("For more details, check these files - ");
                                                    writer.newLine();
                                                    writer.write("Source CLOB File - " + sf.getAbsolutePath());
                                                    writer.newLine();
                                                    writer.write("Target CLOB File - " + tf.getAbsolutePath());
                                                    writer.newLine();
                                                    writer.newLine();
                                                    writer.write("No. of conflicts - " + totalDifferences);
                                                    writer.newLine();
                                                    writer.newLine();
                                                    writer.write("Description - (More than 5 mismatches will be skipped)");
                                                    writer.newLine();
                                                    for (Iterator it = allDifferences.iterator(); it.hasNext();) {
                                                        if (i < 5) {
                                                            Difference object = (Difference) it.next();
                                                            writer.newLine();
                                                            writer.write("\t  ERROR in " + object.getDescription().toUpperCase() + " at location " + object.getControlNodeDetail().getXpathLocation());
                                                            writer.flush();
                                                            i++;
                                                        } else {
                                                            break;
                                                        }
                                                    }
                                                    writer.newLine();
                                                    writer.write(" - - - - - - - - - - - - - - - - - -");
                                                    writer.newLine();
                                                }
                                                mismatchCount++;
                                            }
                                            source.close();
                                            target.close();
                                        } catch (SAXException ex) {
                                            // The difference is probably failed out of Parse Exception (XML format not recognized)
                                            // re-initializing the buffer
                                            source.close();
                                            target.close();
                                            System.out.println("Inside File Comparison for record : " + s);
                                            System.out.println("Source File - " + sf.getAbsolutePath());
                                            System.out.println("Target File - " + tf.getAbsolutePath());

                                            Set<String> sourceMismatch = new HashSet<>();
                                            Set<String> targetMismatch = new HashSet<>();
                                            Set<String> sourceExtraData = new HashSet<>();
                                            Set<String> targetExtraData = new HashSet<>();
                                            int i = 0;

                                            /*
                                                source = new BufferedReader(new InputStreamReader(new FileInputStream(sf)));
                                                target = new BufferedReader(new InputStreamReader(new FileInputStream(tf)));
                                                
                                                while ((sourceData = source.readLine()) != null && (targetData = target.readLine()) != null) {
                                                if(i<2){
                                                System.out.println("Comparing "+sourceData.trim()+"\n with "+targetData.trim());
                                                }
                                                if (!sourceData.trim().equals(targetData.trim())) {
                                                totalDifferences++;
                                                sourceMismatch.add(sourceData);
                                                targetMismatch.add(targetData);
                                                }
                                                i++;
                                                }
                                                i=0;
                                                while ((sourceData = source.readLine()) != null) {
                                                totalDifferences++;
                                                sourceExtraData.add(sourceData);
                                                }
                                                while ((targetData = target.readLine()) != null) {
                                                totalDifferences++;
                                                targetExtraData.add(targetData);
                                                }
                                                
                                             */
                                            // The difference will be now treated as difference in files
                                            Scanner sourceScanner = new Scanner(sf);
                                            Scanner targetScanner = new Scanner(tf);

                                            StringBuilder sourceData = new StringBuilder(), targetData = new StringBuilder();

                                            while (sourceScanner.hasNextLine() && targetScanner.hasNextLine()) {
                                                sourceData.append(sourceScanner.nextLine());
                                                targetData.append(targetScanner.nextLine());
                                                if (!sourceData.toString().equalsIgnoreCase(targetData.toString())) {
                                                    totalDifferences++;
                                                    sourceMismatch.add(sourceData.toString());
                                                    targetMismatch.add(targetData.toString());
                                                }
                                                sourceData.setLength(0);
                                                targetData.setLength(0);
                                            }
                                            while (sourceScanner.hasNextLine()) {
                                                sourceData.append(sourceScanner.nextLine());
                                                totalDifferences++;
                                                sourceExtraData.add(sourceData.toString());
                                                sourceData.setLength(0);
                                            }
                                            while (targetScanner.hasNextLine()) {
                                                targetData.append(targetScanner.nextLine());
                                                totalDifferences++;
                                                targetExtraData.add(targetData.toString());
                                                targetData.setLength(0);
                                            }
                                            if (totalDifferences > 0) {
                                                System.out.println("totalDifferences:" + totalDifferences);
                                                if (outputFileFormat.equals("TEXT")) {
                                                    writer.newLine();
                                                    writer.write("Error in RECORD - " + s);
                                                    writer.newLine();
                                                    writer.write("For more details, check these files - ");
                                                    writer.newLine();
                                                    writer.write("Source CLOB File - " + sf.getAbsolutePath());
                                                    writer.newLine();
                                                    writer.write("Target CLOB File - " + tf.getAbsolutePath());
                                                    writer.newLine();
                                                    writer.newLine();
                                                    writer.write("No. of conflicts - " + totalDifferences);
                                                    writer.newLine();
                                                    writer.newLine();
                                                    writer.write("Description - (More than 5 mismatches will be skipped)");
                                                    writer.newLine();
                                                    Iterator its = sourceMismatch.iterator();
                                                    Iterator itt = sourceMismatch.iterator();
                                                    while (i < 5 && its.hasNext() && itt.hasNext()) {
                                                        writer.newLine();
                                                        writer.write("Source - " + (String) its.next());
                                                        writer.newLine();
                                                        writer.write("Target - " + (String) itt.next());
                                                        writer.newLine();
                                                        i++;
                                                    }
                                                    if (i < 5 && sourceExtraData.size() > 0) {
                                                        writer.newLine();
                                                        writer.write("Source Clob File has some extra data - ");
                                                        writer.newLine();
                                                        its = sourceExtraData.iterator();
                                                        while (i < 5 && its.hasNext()) {
                                                            writer.write("\t" + (String) its.next());
                                                            writer.newLine();
                                                            i++;
                                                        }
                                                    }
                                                    if (i < 5 && targetExtraData.size() > 0) {
                                                        writer.newLine();
                                                        writer.write("Target Clob File has some extra data - ");
                                                        writer.newLine();
                                                        itt = targetExtraData.iterator();
                                                        while (i < 5 && its.hasNext()) {
                                                            writer.write("\t" + (String) its.next());
                                                            writer.newLine();
                                                            i++;
                                                        }
                                                    }
                                                    writer.newLine();
                                                    writer.write(" - - - - - - - - - - - - - - - - - -");
                                                    writer.newLine();
                                                }
                                                mismatchCount++;
                                            }
                                            sourceScanner.close();
                                            targetScanner.close();
                                            sourceMismatch.clear();
                                            sourceMismatch = null;
                                            targetMismatch.clear();
                                            targetMismatch = null;
                                            sourceExtraData.clear();
                                            sourceExtraData = null;
                                            targetExtraData.clear();
                                            targetExtraData = null;
                                        }

                                        if (totalDifferences == 0) {
                                            //to delete files that do not have any mismatch
                                            sf.delete();
                                            tf.delete();
                                        }
                                        break;
                                        //sourceFiles.remove(sf);
                                        //targetFiles.remove(tf);
                                    }else{
                                        System.out.println("Skipping comparison due to missing file :");
                                        System.out.println("Source File : "+sf+"\t | Target File : "+tf);
                                    }
                                }
                            } else if (sourceFiles.size() > targetFiles.size()) {
                                if (outputFileFormat.equals("TEXT")) {
                                    writer.newLine();
                                    writer.write("Error in RECORD - " + s);
                                    writer.newLine();
                                    writer.write("Found extra XML records in Source for this record...");
                                    writer.newLine();
                                    writer.write(" - - - - - - - - - - - - - - - - - -");
                                    writer.newLine();
                                    writer.newLine();
                                }
                            } else if (sourceFiles.size() < targetFiles.size()) {
                                if (outputFileFormat.equals("TEXT")) {
                                    writer.newLine();
                                    writer.write("Error in RECORD - " + s);
                                    writer.newLine();
                                    writer.write("Found extra XML records in Target for this record...");
                                    writer.newLine();
                                    writer.write(" - - - - - - - - - - - - - - - - - -");
                                    writer.newLine();
                                    writer.newLine();
                                }
                            }
                        }
                    }
                } else {
                    toBeSkipped = true;
                    integer = conflictSourceMap.size() + conflictTargetMap.size();
                    mismatchCount = integer;
                    if (outputFileFormat.equals("TEXT")) {
                        writer.newLine();
                        writer.write("Table Comparison skipped due to massive mismatch (more than 3000) -- Found " + mismatchCount + " mismatches");
                        writer.newLine();
                        writer.newLine();
                        writer.flush();
                    }
                }
                timeTaken = startTime - (System.currentTimeMillis());
                System.out.println(TimeUnit.MILLISECONDS.toSeconds(timeTaken) + "secs taken to compare the data");
                if (!tempUniqueSourceMap.isEmpty() && !toBeSkipped) {
                    if (outputFileFormat.equals("TEXT") && showSourceUnique) {
                        writer.newLine();
                        writer.write("******************************************");
                        writer.newLine();
                        writer.write("Source DBs has some additional data as....");
                        writer.newLine();
                        for (String s : tempUniqueSourceMap.keySet()) {
                            if (s != null) {
                                if (outputFileFormat.equals("TEXT")) {
                                    writer.write(s + " |  Files:" + tempUniqueSourceMap.get(s).toString().replace("[", "").replace("]", ""));
                                    writer.newLine();
                                    writer.flush();
                                }
                                sourceCount++;
                            }
                        }
                        writer.write("*********************************************");
                    }
                    tempUniqueSourceMap.clear();
                }
                if (!tempUniqueTargetMap.isEmpty() && !toBeSkipped) {
                    if (outputFileFormat.equals("TEXT") && showTargetUnique) {
                        writer.newLine();
                        writer.write("******************************************");
                        writer.newLine();
                        writer.write("Target DBs has some additional data as....");
                        writer.newLine();
                        for (String s : tempUniqueTargetMap.keySet()) {
                            if (s != null) {
                                if (outputFileFormat.equals("TEXT")) {
                                    writer.write(s + " |  Files:" + tempUniqueTargetMap.get(s).toString().replace("[", "").replace("]", ""));
                                    writer.newLine();
                                    writer.flush();
                                }
                                targetCount++;
                            }
                        }
                        writer.write("*********************************************");
                    }
                    tempUniqueTargetMap.clear();
                }
                integer = mismatchCount + sourceCount + targetCount;
                if (integer > 0) {
                    hashMap.put(integer, tableName + "@" + mismatchCount + "@" + sourceCount + "@" + targetCount);
                }
            } else {
                if (outputFileFormat.equals("TEXT")) {
                    writer.newLine();
                    writer.write("*********************************************");
                    writer.newLine();
                    writer.write("Both Source and Target DBs have same data....");
                    writer.newLine();
                    writer.write("*********************************************");
                    writer.newLine();
                    writer.flush();
                }
            }
            if (outputFileFormat.equals("TEXT")) {
                writer.newLine();
                writer.write("================================================================================");
                writer.newLine();
                writer.close();
            }

        } catch (IOException ex) {
            if (ex.getMessage().equalsIgnoreCase("Content is not allowed in prolog")) {
                errorTable.add(tableName + " with error: Format of the file is not XML.");
            } else {
                errorTable.add(tableName + " with error:" + ex.getMessage());
            }
            try {
                if (outputFileFormat.equals("TEXT")) {
                    writer.close();
                }
            } catch (IOException ex1) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex1);
            }//JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
        } catch (Exception e) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, e);
            errorTable.add(tableName + " with error:" + e.getMessage());
        }
        System.gc();
    }

    public void checkForNETEnvironments(Connection sourceConn, String sourceDB, Connection targetConn, String targetDB) {
        try {
            if (sourceDB.contains("NET") && sourceDB.contains("PROD")) {
                adjustSchemaForNET(sourceConn, sourceDB, "prd1refwait");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PRD1CODEOWNB")) {
                adjustSchemaForNET(sourceConn, sourceDB, "PRD1CODEOWNB");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PRD1SEOWNB")) {
                adjustSchemaForNET(sourceConn, sourceDB, "PRD1SEOWNB");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PRD1PCOWNB")) {
                adjustSchemaForNET(sourceConn, sourceDB, "PRD1PCOWNB");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PRD1REFOWNB")) {
                adjustSchemaForNET(sourceConn, sourceDB, "PRD1REFOWNB");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PRD1REFWAIT")) {
                adjustSchemaForNET(sourceConn, sourceDB, "PRD1REFWAIT");
            } else if (sourceDB.contains("NET") && sourceDB.contains("SAREF2_PET")) {
                adjustSchemaForNET(sourceConn, sourceDB, "SAREF2_PET");
            } else if (sourceDB.contains("NET") && sourceDB.contains("PET")) {
                adjustSchemaForNET(sourceConn, sourceDB, "pet1refwait");
            }

            if (targetDB.contains("NET") && targetDB.contains("PROD")) {
                adjustSchemaForNET(targetConn, targetDB, "prd1refwait");
            } else if (targetDB.contains("NET") && targetDB.contains("PRD1CODEOWNB")) {
                adjustSchemaForNET(targetConn, targetDB, "PRD1CODEOWNB");
            } else if (targetDB.contains("NET") && targetDB.contains("PRD1SEOWNB")) {
                adjustSchemaForNET(targetConn, targetDB, "PRD1SEOWNB");
            } else if (targetDB.contains("NET") && targetDB.contains("PRD1PCOWNB")) {
                adjustSchemaForNET(targetConn, targetDB, "PRD1PCOWNB");
            } else if (targetDB.contains("NET") && targetDB.contains("PRD1REFOWNB")) {
                adjustSchemaForNET(targetConn, targetDB, "PRD1REFOWNB");
            } else if (targetDB.contains("NET") && targetDB.contains("PRD1REFWAIT")) {
                adjustSchemaForNET(targetConn, targetDB, "PRD1REFWAIT");
            } else if (targetDB.contains("NET") && targetDB.contains("SAREF2_PET")) {
                adjustSchemaForNET(targetConn, targetDB, "SAREF2_PET");
            } else if (targetDB.contains("NET") && targetDB.contains("PET")) {
                adjustSchemaForNET(targetConn, targetDB, "pet1refwait");
            }
            if (targetDB.contains("NET") && targetDB.contains("UAT")) {
                String[] split = targetDB.split("UAT");
                String replace = split[1].replace("[a-zA-Z]*", "");
                adjustSchemaForNET(targetConn, targetDB, "netapp" + replace);
            }
            if (sourceDB.contains("NET") && sourceDB.contains("UAT")) {
                String[] split = sourceDB.split("UAT");
                String replace = split[1].replace("[a-zA-Z]*", "");
                adjustSchemaForNET(sourceConn, sourceDB, "netapp" + replace);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void adjustSchemaForNET(Connection connection, String dbName, String schemaName) {
        PreparedStatement preparedStatement = null;
        try {

            Reference reference = new ConnectionHelper().getDBReference(dbName);
            DBConnection dbc = new ConnectionHelper().extractDBConnection(reference);
            String user = dbc.getUsername().toUpperCase();

            if (user.equals("CONSULTA_DICIONARIO") || user.startsWith("T34") || user.startsWith("PET")) {
                System.out.println("Altering session : alter session set current_schema = " + schemaName.toUpperCase());
                preparedStatement = connection.prepareStatement("alter session set current_schema = " + schemaName.toUpperCase());
                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    System.out.println("Session updated to prd1custc");
                }
            }

        } catch (SQLException | JAXBException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void initializeHTMLReports(String outputFileFormat, String dataType, File finalReport, File file) {
        if (outputFileFormat.equals("HTML")) {
            if (dataType.equals("NON-CLOB")) {
                System.out.println("Arguements Non-Clob html : " + finalReport.getAbsolutePath().replace("%20", " ") + " , " + file.getAbsolutePath().replace("%20", " ") + " , " + directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html") + " , " + directory + "\\HTML-Reports\\");

                new ReportGenerator().createHTMLreport(finalReport.getAbsolutePath().replace("%20", " "), file.getAbsolutePath().replace("%20", " "), directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html"), directory + "\\HTML-Reports\\", true);

            } else {
                System.out.println("Arguements Non-Clob html : " + finalReport.getAbsolutePath().replace("%20", " ") + " , " + file.getAbsolutePath().replace("%20", " ") + " , " + directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html") + " , " + directory + "\\HTML-Reports\\");
                new ReportGenerator().createHTMLreport(finalReport.getAbsolutePath().replace("%20", " "), file.getAbsolutePath().replace("%20", " "), directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html"), directory + "\\HTML-Reports\\", false);
            }
        }
    }

}
