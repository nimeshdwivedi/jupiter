/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbHelper;

import FileHelper.ReportGenerator;
import componentBean.DBTable;
import componentBean.DatabaseObjects;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemBean.DBLinksBean;
import schemBean.IndexBean;
import schemBean.SequencesBean;
import schemBean.SynonymsBean;
import schemBean.TableBean;
import schemBean.TriggersBean;
import schemBean.ViewsBean;

/**
 *
 * @author nimeshd
 */
public class SchemaOperation {

    static String directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
    static File file;
    static String fileName;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh_mm");
    static HashMap<String, String> sourceIndexesMap = new HashMap<>(), targetIndexesMap = new HashMap<>();
    static HashMap<String, Set<TableBean.TableProperties>> sourceTablesMap = new HashMap<>(), targetTablesMap = new HashMap<>();
    static HashMap<String, String> sourceSequencesMap = new HashMap<>(), targetSequencesMap = new HashMap<>();
    static HashMap<String, String> sourceSynonymsMap = new HashMap<>(), targetSynonymsMap = new HashMap<>();
    static HashMap<String, String> sourceViewsMap = new HashMap<>(), targetViewsMap = new HashMap<>();
    static HashMap<String, String> sourceTriggersMap = new HashMap<>(), targetTriggersMap = new HashMap<>();
    static HashMap<String, String> sourceDBLinksMap = new HashMap<>(), targetDBLinksMap = new HashMap<>();

    static HashMap<Integer, String> tableResult = new HashMap<>();
    static HashMap<Integer, String> totalResults = new HashMap<>();

    BufferedWriter bufferedWriter = null;

    static String extraColumns = null;

    public static String returnDetailedReport() {
        return file.getAbsolutePath();
    }

    public static String returnFinalReport() {
        return fileName;
    }

    public boolean showSchemaDifferences(final Connection sourceConn, final Connection targetConn, final DatabaseObjects databaseObjects, final String sourceUser, final String targetUser, final String extraCols, final boolean compareByDefinition, final boolean showNameDifference, String outputPath, String outputFileFormat, String missingTables) {
        directory = outputPath;
        extraColumns = extraCols;
        file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
        if (outputFileFormat.equals("HTML")) {
            File f = new File(directory + "/HTML-Reports");
            if (!f.exists()) {
                f.mkdir();
            } else {
                File[] files = f.listFiles();
                for (File content : files) {
                    if (content.isFile()) {
                        if (!new File(directory + "/HTML-Reports/backup").exists()) {
                            new File(directory + "/HTML-Reports/backup").mkdir();
                        } else {
                            if (!content.getName().startsWith("My")) {
                                try {
                                    Files.move(Paths.get(content.getAbsolutePath()), Paths.get(directory + "/HTML-Reports/backup/" + content.getName()), StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ex) {
                                    Logger.getLogger(DataOperation.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        }

        final SchemaOperation operation1 = new SchemaOperation();

        System.out.println("Is Database Objects null : " + databaseObjects == null);
        System.out.println("Is Database Objects null : " + databaseObjects.getTables() == null);
        if (databaseObjects.getTables() != null && !databaseObjects.getTables().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "table", false);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "table", false);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Source Tables Size - " + sourceTablesMap.size());
            System.out.println("Target Tables Size - " + targetTablesMap.size());
            System.out.println("Comparing Tables...");
            compareTableSchema();
            System.out.println("Finishes Comparing Tables.");
        }
        if (databaseObjects.getIndexes() != null && !databaseObjects.getIndexes().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "index", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "index", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Source Indexes Size - " + sourceIndexesMap.size());
            System.out.println("Target Indexes Size - " + targetIndexesMap.size());
            System.out.println("Comparing indexes...");
            compareIndexes(compareByDefinition);
            System.out.println("Finishes Comparing indexes.");
        }
        if (databaseObjects.getSequences() != null && !databaseObjects.getSequences().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "sequence", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "sequence", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Source Sequence Size - " + sourceSequencesMap.size());
            System.out.println("Target Sequence Size - " + targetSequencesMap.size());
            System.out.println("Comparing sequences...");
            compareSequences();
            System.out.println("Finishes Comparing sequences.");
        }
        if (databaseObjects.getTriggers() != null && !databaseObjects.getTriggers().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "trigger", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "trigger", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Source Trigger Size - " + sourceTriggersMap.size());
            System.out.println("Target Trigger Size - " + targetTriggersMap.size());
            System.out.println("Comparing triggers...");
            compareTriggers(compareByDefinition);
            System.out.println("Finishes Comparing triggers.");
        }
        if (databaseObjects.getViews() != null && !databaseObjects.getViews().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "view", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "view", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Source View Size - " + sourceViewsMap.size());
            System.out.println("Target View Size - " + targetViewsMap.size());
            System.out.println("Comparing Views...");
            compareViews();
            System.out.println("Finishes Comparing Views.");
        }
        if (databaseObjects.getSynonyms() != null && !databaseObjects.getSynonyms().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "synonym", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "synonym", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Source Synonym Size - " + sourceSynonymsMap.size());
            System.out.println("Target Synonym Size - " + targetSynonymsMap.size());
            System.out.println("Comparing synonyms...");
            compareSynonyms();
            System.out.println("Finishes Comparing synonyms.");
        }
        if (databaseObjects.getDbLinks() != null && !databaseObjects.getDbLinks().isEmpty()) {
            Thread sourceTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(sourceConn, sourceUser, databaseObjects, "source", "dblink", compareByDefinition);
                }
            };
            sourceTableTH.start();

            Thread targetTableTH = new Thread() {
                @Override
                public void run() {
                    operation1.fetchObjectSchema(targetConn, targetUser, databaseObjects, "target", "dblink", compareByDefinition);
                }
            };
            targetTableTH.start();

            try {
                sourceTableTH.join();
                targetTableTH.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Source DBLink Size - " + sourceDBLinksMap.size());
            System.out.println("Target DBLink Size - " + targetDBLinksMap.size());
            System.out.println("Comparing dblinks...");
            compareDBLinks();
            System.out.println("Finishes Comparing dblinks.");
        }

        File dir = new File(directory);
        for (File threadFile : dir.listFiles()) {
            if (threadFile.getName().startsWith("SchemaCompare")) {
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
                    bufferedWriter.newLine();
                    for (int i = 0; i < 30; i++) {
                        bufferedWriter.write("~ ~ ");
                    }
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
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

        String sourceDB = "", targetDB = "";
        List list = new ArrayList(totalResults.keySet());
        Collections.sort(list, Collections.reverseOrder());
        List tableList = new ArrayList(tableResult.keySet());
        Collections.sort(tableList, Collections.reverseOrder());
        File finalReport = new File(file.getParentFile() + "\\FINAL REPORT - " + file.getName());
        fileName = finalReport.getAbsolutePath();
        try {

            String format = "%1$-30s|\t%2$-13s|\t%3$-13s|\t%4$-13s";
            bufferedWriter = new BufferedWriter(new FileWriter(finalReport, true));
            bufferedWriter.newLine();
            bufferedWriter.write("================================================================================");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("                       FINAL RESULTS   (DDL OBJECT LEVEL)                       ");
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
            bufferedWriter.write(" *** DDL Object Level mismatch shows the conflict count on object level only ***");
            bufferedWriter.newLine();
            bufferedWriter.write(" *** Not individual conflict count for a particular object ***");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("#MISMATCHES (Objects count in both source-target that have definition mismatch)");
            bufferedWriter.newLine();
            bufferedWriter.write("#UNIQUESOURCE (Objects count that have definition only in Source)");
            bufferedWriter.newLine();
            bufferedWriter.write("#UNIQUETARGET (Objects count that have definition only in Target)");
            bufferedWriter.newLine();
            bufferedWriter.write("#TOTALCONFLICTS (Total of all unique and mismatched object count)");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(String.format(format, "ObjectName", "Mismatches", "UniqueSource", "UniqueTarget") + " | \t TotalConflicts");
            bufferedWriter.newLine();
            if (totalResults.isEmpty()) {
                String data = String.format(format, "NA", "-", "-", "-");
                bufferedWriter.write(data + " | \t -");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("No conflicts found.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String tableData = totalResults.get(list.get(i));
                    String[] split = tableData.split("@");
                    String data = String.format(format, split[0], split[1], split[2], split[3]);
                    bufferedWriter.write(data + " | \t " + list.get(i));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            bufferedWriter.newLine();
            /*if (errorTable.size() > 0) {
                bufferedWriter.write("The comparison failed for following tables due to database error. Please check these following tables separately...");
                bufferedWriter.newLine();
                for (String s : errorTable) {
                    if (s != null) {
                        bufferedWriter.write(s);
                        bufferedWriter.newLine();
                    }
                }
            }*/
            if (missingTables != null) {
                bufferedWriter.newLine();
                bufferedWriter.write("Following objects were found missing:");
                bufferedWriter.newLine();
                String[] missingTableList = missingTables.split(",");
                bufferedWriter.newLine();
                for (String s : missingTableList) {
                    bufferedWriter.write("-- " + s.trim());
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }

            if (tableResult != null && !tableResult.isEmpty()) {
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("================================================================================");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("                       TABLE-DEFINITION COMPARISON RESULTS                      ");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("================================================================================");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(" *** This shows the table level conflict count i.e. the number of conflicts on table level***");
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write(String.format(format, "TableName", "Mismatches", "UniqueSource", "UniqueTarget") + " | \t TotalConflicts");
                bufferedWriter.newLine();
                for (int i = 0; i < tableList.size(); i++) {
                    String tableData = tableResult.get(tableList.get(i));
                    String[] split = tableData.split("@");
                    String data = String.format(format, split[0], split[1], split[2], split[3]);
                    bufferedWriter.write(data + " | \t " + tableList.get(i));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            if (!totalResults.isEmpty()) {
                initializeHTMLReports(outputFileFormat, "NON-CLOB", finalReport, file);
            }
        } catch (IOException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        list.clear();
        tableList.clear();
        totalResults.clear();

        return true;
    }

    private void fetchObjectSchema(Connection connection, String user, DatabaseObjects databaseObjects, String connectionType, String objectType, boolean compareByDefinition) {
        switch (objectType) {
            case "table":
                List<DBTable> tables = databaseObjects.getTables();
                String tableName,
                 query;

                System.out.println("Extra Columns: " + extraColumns);
                if (extraColumns != null) {
                    query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, " + extraColumns + " FROM ALL_TAB_COLS WHERE OWNER = '" + user.toUpperCase() + "' ";
                } else {
                    query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLS WHERE OWNER = '" + user.toUpperCase() + "' ";
                }
                if (tables != null && !tables.isEmpty()) {
                    // Way 1 to individually get table's data
                    TableBean.TableProperties tableProperties;

                    Set<Set<String>> tablesSet = new HashSet<>();
                    for (int i = 0; i < tables.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= tables.size()) {
                                break;
                            }
                            set.add(tables.get(j).getName());
                        }
                        tablesSet.add(set);
                    }
                    //WAY 2 b simply proceeding with all tables at once or in chunks
                    try {
                        Set<TableBean.TableProperties> tableAllData = null;
                        for (Set<String> set : tablesSet) {
                            String tableList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println(query + " AND TABLE_NAME in ('" + tableList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND TABLE_NAME in ('" + tableList + "') ORDER BY TABLE_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    tableProperties = new TableBean.TableProperties();
                                    tableName = resultSet.getString(1);
                                    tableProperties.setColumnName(resultSet.getString(2));
                                    tableProperties.setDataType(resultSet.getString(3));
                                    if (extraColumns != null) {
                                        if (extraColumns.contains("DATA_LENGTH")) {
                                            tableProperties.setDataLength(String.valueOf(resultSet.getBigDecimal("DATA_LENGTH")));
                                        }
                                        if (extraColumns.contains("DATA_PRECISION")) {
                                            tableProperties.setDataPrecision(String.valueOf(resultSet.getBigDecimal("DATA_PRECISION")));
                                        }
                                        if (extraColumns.contains("DATA_SCALE")) {
                                            tableProperties.setDataScale(String.valueOf(resultSet.getBigDecimal("DATA_SCALE")));
                                        }
                                        if (extraColumns.contains("NULLABLE")) {
                                            tableProperties.setNullable(resultSet.getString("NULLABLE"));
                                        }
                                        if (extraColumns.contains("DEFAULT_LENGTH")) {
                                            tableProperties.setDefaultLength(String.valueOf(resultSet.getBigDecimal("DEFAULT_LENGTH")));
                                        }
                                        if (extraColumns.contains("DATA_DEFAULT")) {
                                            tableProperties.setDefaultData(resultSet.getString("DATA_DEFAULT"));
                                        }
                                        if (extraColumns.contains("LOW_VALUE")) {
                                            tableProperties.setLowValue(resultSet.getString("LOW_VALUE"));
                                        }
                                        if (extraColumns.contains("HIGH_VALUE")) {
                                            tableProperties.setHighValue(resultSet.getString("HIGH_VALUE"));
                                        }
                                        if (extraColumns.contains("CHAR_LENGTH")) {
                                            tableProperties.setCharLength(String.valueOf(resultSet.getBigDecimal("CHAR_LENGTH")));
                                        }
                                    }
                                    if (connectionType.equals("source")) {
                                        if (sourceTablesMap.containsKey(tableName)) {
                                            sourceTablesMap.get(tableName).add(tableProperties);
                                        } else {
                                            tableAllData = new HashSet<>();
                                            tableAllData.add(tableProperties);
                                            sourceTablesMap.put(tableName, tableAllData);
                                        }
                                    } else {
                                        if (targetTablesMap.containsKey(tableName)) {
                                            targetTablesMap.get(tableName).add(tableProperties);
                                        } else {
                                            tableAllData = new HashSet<>();
                                            tableAllData.add(tableProperties);
                                            targetTablesMap.put(tableName, tableAllData);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case "index":
                List<String> indexes = new ArrayList<>();
                Set<String> s = databaseObjects.getIndexes();
                if (s != null && !s.isEmpty()) {
                    indexes.addAll(s);
                    Set<Set<String>> indexSet = new HashSet<>();
                    for (int i = 0; i < indexes.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= indexes.size()) {
                                break;
                            }
                            set.add(indexes.get(j));
                        }
                        indexSet.add(set);
                    }
                    IndexBean bean;
                    query = "SELECT TABLE_OWNER, TABLE_NAME, COLUMN_NAME, COLUMN_POSITION, COLUMN_LENGTH, CHAR_LENGTH, DESCEND, INDEX_NAME FROM all_ind_columns where INDEX_OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : indexSet) {
                            String indexList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND INDEX_NAME in ('" + indexList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND INDEX_NAME in ('" + indexList + "') ORDER BY INDEX_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new IndexBean();
                                    bean.setTableOwner(resultSet.getString(1));
                                    bean.setTableName(resultSet.getString(2));
                                    bean.setColumnName(resultSet.getString(3));
                                    bean.setColumnPosition(resultSet.getString(4));
                                    bean.setColumnLength(resultSet.getString(5));
                                    bean.setCharLength(resultSet.getString(6));
                                    bean.setDescend(resultSet.getString(7));
                                    if (connectionType.equals("source")) {
                                        sourceIndexesMap.put(resultSet.getString(8), bean.toString());
                                    } else {
                                        targetIndexesMap.put(resultSet.getString(8), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case "sequence":

                List<String> sequences = new ArrayList<>();
                s = databaseObjects.getSequences();
                if (s != null && !s.isEmpty()) {
                    sequences.addAll(s);
                    Set<Set<String>> sequenceSet = new HashSet<>();
                    for (int i = 0; i < sequences.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= sequences.size()) {
                                break;
                            }
                            set.add(sequences.get(j));
                        }
                        sequenceSet.add(set);
                    }
                    SequencesBean bean;
                    query = "SELECT SEQUENCE_NAME, MIN_VALUE, MAX_VALUE, INCREMENT_BY, CYCLE_FLAG, ORDER_FLAG, CACHE_SIZE FROM all_sequences where SEQUENCE_OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : sequenceSet) {
                            String sequenceList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND INDEX_NAME in ('" + sequenceList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND SEQUENCE_NAME in ('" + sequenceList + "') ORDER BY SEQUENCE_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new SequencesBean();
                                    bean.setSequenceName(resultSet.getString(1));
                                    bean.setMinValue(resultSet.getString(2));
                                    bean.setMaxValue(resultSet.getString(3));
                                    bean.setIncrementBy(resultSet.getString(4));
                                    bean.setCycleFlag(resultSet.getString(5));
                                    bean.setOrderFlag(resultSet.getString(6));
                                    bean.setCacheSize(resultSet.getString(7));
                                    if (connectionType.equals("source")) {
                                        sourceSequencesMap.put(bean.getSequenceName(), bean.toString());
                                    } else {
                                        targetSequencesMap.put(bean.getSequenceName(), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case "synonym":
                List<String> synonyms = new ArrayList<>();
                s = databaseObjects.getSynonyms();
                if (s != null && !s.isEmpty()) {
                    synonyms.addAll(s);
                    Set<Set<String>> synonymSet = new HashSet<>();
                    for (int i = 0; i < synonyms.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= synonyms.size()) {
                                break;
                            }
                            set.add(synonyms.get(j));
                        }
                        synonymSet.add(set);
                    }
                    SynonymsBean bean;
                    query = "SELECT SYNONYM_NAME, TABLE_NAME, DB_LINK FROM all_synonyms where OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : synonymSet) {
                            String synonymList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND SYNONYM_NAME in ('" + synonymList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND SYNONYM_NAME in ('" + synonymList + "') ORDER BY SYNONYM_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new SynonymsBean();
                                    bean.setSynonymName(resultSet.getString(1));
                                    bean.setTableName(resultSet.getString(2));
                                    bean.setDbLink(resultSet.getString(3));
                                    if (connectionType.equals("source")) {
                                        sourceSynonymsMap.put(bean.getSynonymName(), bean.toString());
                                    } else {
                                        targetSynonymsMap.put(bean.getSynonymName(), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "dblink":
                List<String> dblinks = new ArrayList<>();
                s = databaseObjects.getDbLinks();
                if (s != null && !s.isEmpty()) {
                    dblinks.addAll(s);
                    Set<Set<String>> dblinkSet = new HashSet<>();
                    for (int i = 0; i < dblinks.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= dblinks.size()) {
                                break;
                            }
                            set.add(dblinks.get(j));
                        }
                        dblinkSet.add(set);
                    }
                    DBLinksBean bean;
                    query = "SELECT DB_LINK, USERNAME, HOST FROM all_sequences where OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : dblinkSet) {
                            String dblinkList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND DB_LINK in ('" + dblinkList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND DB_LINK in ('" + dblinkList + "') ORDER BY DB_LINK");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new DBLinksBean();
                                    bean.setDbLink(resultSet.getString(1));
                                    bean.setUsername(resultSet.getString(2));
                                    bean.setHost(resultSet.getString(3));
                                    if (connectionType.equals("source")) {
                                        sourceDBLinksMap.put(bean.getDbLink(), bean.toString());
                                    } else {
                                        targetDBLinksMap.put(bean.getDbLink(), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "trigger":
                List<String> triggers = new ArrayList<>();
                s = databaseObjects.getTriggers();
                if (s != null && !s.isEmpty()) {
                    triggers.addAll(s);
                    Set<Set<String>> triggerSet = new HashSet<>();
                    for (int i = 0; i < triggers.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= triggers.size()) {
                                break;
                            }
                            set.add(triggers.get(j));
                        }
                        triggerSet.add(set);
                    }
                    TriggersBean bean;
                    query = "SELECT TRIGGER_NAME, TRIGGER_TYPE, TRIGGERING_EVENT, BASE_OBJECT_TYPE, TABLE_NAME, COLUMN_NAME, REFERENCING_NAMES, \n"
                            + "WHEN_CLAUSE, STATUS, DESCRIPTION, ACTION_TYPE, TRIGGER_BODY, FIRE_ONCE, APPLY_SERVER_ONLY, BEFORE_STATEMENT, BEFORE_ROW, \n"
                            + "AFTER_ROW, AFTER_STATEMENT, INSTEAD_OF_ROW FROM all_triggers where OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : triggerSet) {
                            String sequenceList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND TRIGGER_NAME in ('" + sequenceList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND TRIGGER_NAME in ('" + sequenceList + "') ORDER BY TRIGGER_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new TriggersBean();
                                    String triggerBody = resultSet.getString(12);
                                    bean.setTriggerName(resultSet.getString(1));
                                    bean.setTriggerType(resultSet.getString(2));
                                    bean.setTriggeringEvent(resultSet.getString(3));
                                    bean.setBaseObjectType(resultSet.getString(4));
                                    bean.setTableName(resultSet.getString(5));
                                    bean.setColumnName(resultSet.getString(6));
                                    bean.setReferencingName(resultSet.getString(7));
                                    bean.setWhenClause(resultSet.getString(8));
                                    bean.setStatus(resultSet.getString(9));
                                    bean.setDescription(resultSet.getString(10));
                                    bean.setActionType(resultSet.getString(11));
                                    bean.setTriggerBody(triggerBody);
                                    bean.setFireOnce(resultSet.getString(13));
                                    bean.setApplyServerOnly(resultSet.getString(14));
                                    bean.setBeforeStatement(resultSet.getString(15));
                                    bean.setBeforeRow(resultSet.getString(16));
                                    bean.setAfterRow(resultSet.getString(17));
                                    bean.setAfterStatement(resultSet.getString(18));
                                    bean.setInsteadOfRow(resultSet.getString(19));
                                    if (connectionType.equals("source")) {
                                        sourceTriggersMap.put(bean.getTriggerName(), bean.toString());
                                    } else {
                                        targetTriggersMap.put(bean.getTriggerName(), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "view":
                List<String> views = new ArrayList<>();
                s = databaseObjects.getViews();
                if (s != null && !s.isEmpty()) {
                    views.addAll(s);
                    Set<Set<String>> viewSet = new HashSet<>();
                    for (int i = 0; i < views.size(); i += 50) {
                        Set<String> set = new HashSet<>();
                        int k = i + 50;
                        for (int j = i; j < k; j++) {
                            if (j >= views.size()) {
                                break;
                            }
                            set.add(views.get(j));
                        }
                        viewSet.add(set);
                    }
                    ViewsBean bean;
                    query = "SELECT VIEW_NAME, TEXT, VIEW_TYPE, EDITIONING_VIEW, READ_ONLY FROM all_views where OWNER = '" + user + "'";
                    try {
                        for (Set<String> set : viewSet) {
                            String viewList = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "','");
                            System.out.println("Getting data from db:");
                            System.out.println(query + " AND VIEW_NAME in ('" + viewList + "')");
                            PreparedStatement preparedStatement = connection.prepareStatement(query + " AND VIEW_NAME in ('" + viewList + "') ORDER BY VIEW_NAME");
                            ResultSet resultSet = preparedStatement.executeQuery();
                            if (resultSet != null) {
                                while (resultSet.next()) {
                                    bean = new ViewsBean();
                                    String text = resultSet.getString(2);
                                    bean.setViewName(resultSet.getString(1));
                                    bean.setText(text);
                                    bean.setViewType(resultSet.getString(3));
                                    bean.setEditioningView(resultSet.getString(4));
                                    bean.setReadOnly(resultSet.getString(5));
                                    if (connectionType.equals("source")) {
                                        sourceViewsMap.put(bean.getViewName(), bean.toString());
                                    } else {
                                        targetViewsMap.put(bean.getViewName(), bean.toString());
                                    }
                                }
                            }
                        }
                    } catch (SQLException | NullPointerException ex) {
                        System.out.println("Error occurred :" + ex.getMessage());
                        Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
        }
    }

    private void compareTableSchema() {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Tables_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        Set<TableBean.TableProperties> sourceTableData, targetTableData, sourceOnlyData, targetOnlyData;
        int tableExtraSourceCount = 0, tableMismatchCount = 0, tableExtraTargetCount = 0;
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            if (extraColumns == null || extraColumns.isEmpty()) {
                writer.write("Results for Tables on Properties ( COLUMN_NAME, DATA_TYPE )");
            } else {
                writer.write("Results for Tables on Properties ( COLUMN_NAME, DATA_TYPE, " + extraColumns.replace(",", " , ") + ")");
            }
            System.out.println("Source Table Map Key Set: " + sourceTablesMap.keySet());
            for (String s : sourceTablesMap.keySet()) {
                System.out.println("Checking for " + s);
                extraSourceCount = 0;
                extraTargetCount = 0;
                mismatchCount = 0;

                writer.newLine();

                sourceTableData = sourceTablesMap.get(s);
                targetTableData = targetTablesMap.get(s);

                sourceOnlyData = sourceTableData;
                sourceOnlyData.removeAll(targetTableData);

                targetOnlyData = targetTableData;
                targetOnlyData.removeAll(sourceTableData);

                //Handling of mismatches
                String column, propertyName, sourceValue, targetValue;
                int count = 0, columnDefinitionMismatch = 0;
                boolean presentInBoth, ifPresentInSource;
                String[] mismatchValues, conflictVal;
                String format = "%1$-30s  |  %2$-15s  |  %3$-20s  |  %4$-20s";
                for (TableBean.TableProperties source : sourceOnlyData) {
                    System.out.println("Checking for column: " + source.getColumnName());
                    presentInBoth = false;
                    column = source.getColumnName();
                    for (TableBean.TableProperties target : targetOnlyData) {
                        if (target.getColumnName().equalsIgnoreCase(column)) {
                            presentInBoth = true;
                            String mismatches = source.compareWith(target);
                            if (mismatches != null && mismatches.length() > 0) {
                                System.out.println("Mismatch found");
                                mismatchValues = mismatches.split("-AND-");
                                columnDefinitionMismatch = mismatchValues.length;
                                if (count == 0) {
                                    count++;
                                    writer.newLine();
                                    writer.newLine();
                                    writer.write("Conflicts in Table : \t" + s);
                                    writer.newLine();
                                    writer.newLine();
                                    writer.write("-->  Mismatches : ");
                                    writer.newLine();
                                    writer.newLine();
                                    for (int i = 0; i < 100; i++) {
                                        writer.write("-");
                                    }
                                    writer.newLine();
                                    writer.write(String.format(format, "Column Name", "Property Name", "Source Definition", "Target Definition"));
                                    writer.newLine();
                                    for (int i = 0; i < 100; i++) {
                                        writer.write("-");
                                    }
                                    writer.newLine();
                                }
                                boolean sameColumn = true;
                                for (String conflict : mismatchValues) {
                                    conflictVal = conflict.split(" @ ");
                                    propertyName = conflictVal[0];
                                    sourceValue = conflictVal[1];
                                    targetValue = conflictVal[2];
                                    if (sameColumn) {
                                        writer.write(String.format(format, column, propertyName, sourceValue, targetValue));
                                    } else {
                                        writer.write(String.format(format, " ", propertyName, sourceValue, targetValue));
                                    }
                                    sameColumn = false;
                                    writer.newLine();
                                }
                                writer.flush();
                                mismatchCount++;
                                break;
                            }
                        }
                    }
                    if (!presentInBoth) {
                        sourceUnique.add(source);
                    }
                }
                writer.newLine();
                String colFormat = " %1$-30s | %2$-10s | %3$-10s | %4$-14s | %5$-10s | %6$-10s | %7$-13s | %8$-12s | %9$-10s | %10$-10s | %11$-10s ";
                if (!sourceUnique.isEmpty()) {
                    extraSourceCount = sourceUnique.size();
                    System.out.println("Found extra data in Source");
                    writer.newLine();
                    writer.write("-->  Found extra DDL definition in Source");
                    writer.newLine();
                    writer.newLine();
                    writer.write(String.format(colFormat, "columnName", "dataType", "dataLength", "dataPrecision", "dataScale", "nullable", "defaultLength", "defaultData", "lowValue", "highValue", "charLength"));
                    writer.newLine();
                    for (int i = 0; i < 139; i++) {
                        writer.write("-");
                    }
                    writer.newLine();
                    for (TableBean.TableProperties properties : sourceUnique) {
                        writer.write(properties.toString());
                        writer.newLine();
                        writer.flush();
                    }
                }
                writer.newLine();
                System.out.println("Checking column presence in Target");
                for (TableBean.TableProperties target : targetOnlyData) {
                    ifPresentInSource = false;
                    column = target.getColumnName();
                    for (TableBean.TableProperties source : sourceOnlyData) {
                        if (target.getColumnName().equalsIgnoreCase(column)) {
                            ifPresentInSource = true;
                            break;
                        }
                    }
                    if (!ifPresentInSource) {
                        targetUnique.add(target);
                    }
                }
                if (!targetUnique.isEmpty()) {
                    extraTargetCount = targetUnique.size();
                    writer.newLine();
                    writer.write("-->  Found extra DDL definition in Target");
                    writer.newLine();
                    writer.newLine();
                    writer.write(String.format(colFormat, "columnName", "dataType", "dataLength", "dataPrecision", "dataScale", "nullable", "defaultLength", "defaultData", "lowValue", "highValue", "charLength"));
                    writer.newLine();
                    for (TableBean.TableProperties properties : targetUnique) {
                        writer.write(properties.toString());
                        writer.newLine();
                        writer.flush();
                    }
                }
                Integer integer = mismatchCount + extraSourceCount + extraTargetCount;
                if (integer > 0) {
                    tableResult.put(integer, s + "@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
                }
                if (extraSourceCount > 0) {
                    tableExtraSourceCount += 1;
                }
                if (extraTargetCount > 0) {
                    tableExtraTargetCount += 1;
                }
                if (mismatchCount > 0) {
                    tableMismatchCount += 1;
                }

                writer.newLine();
                for (int i = 0; i < 30; i++) {
                    writer.write("#-#-");
                }
                writer.newLine();
                sourceTableData.clear();
                targetTableData.clear();
                sourceUnique.clear();
                targetUnique.clear();
                sourceOnlyData.clear();
                targetOnlyData.clear();
            }
            writer.close();
            Integer integer = tableMismatchCount + tableExtraSourceCount + tableExtraTargetCount;
            if (integer > 0) {
                totalResults.put(integer, "Tables@" + tableMismatchCount + "@" + tableExtraSourceCount + "@" + tableExtraTargetCount);
            }
            sourceTablesMap.clear();
            targetTablesMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareIndexes(boolean compareByDefinition) {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Indexes_" + timestamp + ".txt");

        List<String> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for Indexes on Properties ( INDEX_NAME, TABLE_OWNER, TABLE_NAME, COLUMN_NAME, COLUMN_POSITION, COLUMN_LENGTH, CHAR_LENGTH, DESCEND )");
            writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String format = "%1$-15s | %2$-13s | %3$-20s | %4$-25s | %5$-15s | %6$-12s | %7$-10s | %8$-7s";

            for (String s : sourceIndexesMap.keySet()) {
                if (targetIndexesMap.containsKey(s)) {
                    if (!targetIndexesMap.get(s).equals(sourceIndexesMap.get(s))) {
                        writer.newLine();
                        writer.write("Mismatch found in Index " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("            " + String.format(format, "INDEX_NAME", "TABLE_OWNER", "TABLE_NAME", "COLUMN_NAME", "COLUMN_POSITION", "COLUMN_LENGTH", "CHAR_LENGTH", "DESCEND"));
                        writer.newLine();
                        for (int i = 0; i < 35; i++) {
                            writer.write("----");
                        }
                        writer.newLine();
                        writer.write("Source -->  " + sourceIndexesMap.get(s));
                        writer.newLine();
                        writer.write("Target -->  " + targetIndexesMap.get(s));
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "Indexes@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceIndexesMap.clear();
            targetIndexesMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareSequences() {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Sequences_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for Sequences on Properties ( SEQUENCE_NAME, MIN_VALUE,MAX_VALUE, INCREMENT_BY, CYCLE_FLAG, ORDER_FLAG, CACHE_SIZE )");
            writer.newLine();writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String format = "%1$-20s | %2$-15s | %3$-35s | %4$-12s | %5$-10s | %6$-10s | %7$-10s";        
            for (String s : sourceSequencesMap.keySet()) {
                if (targetSequencesMap.containsKey(s)) {
                    if (!targetSequencesMap.get(s).equals(sourceSequencesMap.get(s))) {
                        writer.newLine();
                        writer.write("Mismatch found in Sequence " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("              " + String.format(format, "SEQUENCE_NAME", "MIN_VALUE", "MAX_VALUE", "INCREMENT_BY", "CYCLE_FLAG", "ORDER_FLAG", "CACHE_SIZE"));
                        writer.newLine();
                        for (int i = 0; i < 50; i++) {
                            writer.write("--");
                        }
                        writer.newLine();
                        writer.write("Source -->    " + sourceSequencesMap.get(s));
                        writer.newLine();
                        writer.write("Target -->    " + targetSequencesMap.get(s));
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "Sequences@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceSequencesMap.clear();
            targetSequencesMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareTriggers(boolean compareByDefinition) {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Triggers_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for Triggers on Properties ( TRIGGER_NAME, TRIGGER_TYPE, TRIGGERING_EVENT, BASE_OBJECT_TYPE, TABLE_NAME, COLUMN_NAME, REFERENCING_NAMES, \n"
                    + "WHEN_CLAUSE, STATUS, DESCRIPTION, ACTION_TYPE, TRIGGER_BODY, FIRE_ONCE, APPLY_SERVER_ONLY, BEFORE_STATEMENT, BEFORE_ROW, AFTER_ROW, AFTER_STATEMENT, INSTEAD_OF_ROW )");
            writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String data = null, description = null, body = null;
            String[] triggerDataSplit;
            String format = "%1$-25s | %2$-16s | %3$-27s | %4$-13s | %5$-20s | %6$-25s | %7$-34s | %8$-12s | %9$-10s | %10$-34s | %11$-8s | %12$-15s | %13$-15s | %14$-10s | %15$-10s | %16$-15s | %17$-12s";        
            for (String s : sourceTriggersMap.keySet()) {
                if (targetTriggersMap.containsKey(s)) {
                    if (!targetTriggersMap.get(s).equals(sourceTriggersMap.get(s))) {
                        triggerDataSplit = sourceTriggersMap.get(s).split("@");
                        try {
                            data = triggerDataSplit[0];
                            description = triggerDataSplit[1];
                            body = triggerDataSplit[2];
                        } catch (IndexOutOfBoundsException exception) {
                            //do nothing
                        }
                        writer.newLine();
                        writer.write("Mismatch found in Trigger " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("\t            " + String.format(format, "TRIGGER_NAME", "TRIGGER_TYPE", "TRIGGERING_EVENT", "BASE_OBJECT_TYPE", "TABLE_NAME", "COLUMN_NAME", "REFERENCING_NAMES",
                                "WHEN_CLAUSE", "STATUS", "ACTION_TYPE", "FIRE_ONCE", "APPLY_SERVER_ONLY", "BEFORE_STATEMENT", "BEFORE_ROW", "AFTER_ROW", "AFTER_STATEMENT", "INSTEAD_OF_ROW"));
                        writer.newLine();
                        for (int i = 0; i < 50; i++) {
                            writer.write("--");
                        }
                        writer.newLine();
                        writer.write("Source -->    " + data);
                        writer.newLine();
                        writer.write("\t " + description);
                        writer.newLine();
                        writer.write("\t " + body);
                        writer.newLine();
                        writer.newLine();
                        triggerDataSplit = targetTriggersMap.get(s).split("@");
                        try {
                            data = triggerDataSplit[0];
                            description = triggerDataSplit[1];
                            body = triggerDataSplit[2];
                        } catch (IndexOutOfBoundsException exception) {
                            //do nothing
                        }
                        writer.write("Target -->    " + data);
                        writer.newLine();
                        writer.write("\t " + description);
                        writer.newLine();
                        writer.write("\t " + body);
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "Triggers@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceTriggersMap.clear();
            targetTriggersMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareViews() {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Views_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for Views on Properties ( VIEW_NAME, TEXT, VIEW_TYPE, EDITIONING_VIEW, READ_ONLY )");
            writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String format = "%1$-30s | %2$-8s | %3$-15s | %4$-9s | %5$-500s";        
            for (String s : sourceViewsMap.keySet()) {
                if (targetViewsMap.containsKey(s)) {
                    if (!targetViewsMap.get(s).equals(sourceViewsMap.get(s))) {
                        writer.newLine();
                        writer.write("Mismatch found in View " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("              " + String.format(format, "VIEW_NAME", "VIEW_TYPE", "EDITIONING_VIEW", "READ_ONLY", "TEXT"));
                        writer.newLine();
                        for (int i = 0; i < 50; i++) {
                            writer.write("--");
                        }
                        writer.newLine();
                        writer.write("Source -->    " + sourceViewsMap.get(s));
                        writer.newLine();
                        writer.write("Target -->    " + targetViewsMap.get(s));
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "Views@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceViewsMap.clear();
            targetViewsMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareSynonyms() {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_Synonyms_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for Synonyms on Properties ( SYNONYM_NAME, TABLE_NAME, DB_LINK )");
            writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String format = "%1$-20s | %2$-20s | %3$-20s";        
            for (String s : sourceSynonymsMap.keySet()) {
                if (targetSynonymsMap.containsKey(s)) {
                    if (!targetIndexesMap.get(s).equals(sourceSynonymsMap.get(s))) {
                        writer.newLine();
                        writer.write("Mismatch found in Synonym " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("              " + String.format(format, "SYNONYM_NAME", "TABLE_NAME", "DB_LINK"));
                        writer.newLine();
                        for (int i = 0; i < 50; i++) {
                            writer.write("--");
                        }
                        writer.newLine();
                        writer.write("Source -->    " + sourceSynonymsMap.get(s));
                        writer.newLine();
                        writer.write("Target -->    " + targetSynonymsMap.get(s));
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "Synonyms@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceSynonymsMap.clear();
            targetSynonymsMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareDBLinks() {
        String timestamp = sdf.format(new Date(System.currentTimeMillis()));
        File f = new File(directory + "//SchemaCompare_DBLinks_" + timestamp + ".txt");

        List<TableBean.TableProperties> sourceUnique = new ArrayList<>(), targetUnique = new ArrayList<>();
        int extraSourceCount, mismatchCount, extraTargetCount;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));

            writer.newLine();
            writer.write("Results for DBlinks on Properties ( OWNER, DB_LINK, USERNAME, HOST )");
            writer.newLine();

            mismatchCount = 0;
            extraSourceCount = 0;
            extraTargetCount = 0;
            String format = "%1$-15s | %2$-13s | %3$-13s | %4$-13s";
            for (String s : sourceDBLinksMap.keySet()) {
                if (targetDBLinksMap.containsKey(s)) {
                    if (!targetDBLinksMap.get(s).equals(sourceDBLinksMap.get(s))) {
                        writer.newLine();
                        writer.write("Mismatch found in DBLink " + s);
                        writer.newLine();
                        writer.newLine();
                        writer.write("            " + String.format(format, "OWNER", "DB_LINK", "USERNAME", "HOST"));
                        writer.newLine();
                        for (int i = 0; i < 50; i++) {
                            writer.write("--");
                        }
                        writer.newLine();
                        writer.write("Source -->    " + sourceDBLinksMap.get(s));
                        writer.newLine();
                        writer.write("Target -->    " + targetDBLinksMap.get(s));
                        writer.newLine();
                        writer.newLine();
                        for (int i = 0; i < 30; i++) {
                            writer.write("#-#-");
                        }
                        writer.newLine();
                        writer.flush();
                        mismatchCount++;
                        break;
                    }
                }
            }
            writer.close();
            Integer integer = mismatchCount;
            if (integer > 0) {
                totalResults.put(integer, "DBlinks@" + mismatchCount + "@" + extraSourceCount + "@" + extraTargetCount);
            }
            sourceDBLinksMap.clear();
            targetDBLinksMap.clear();
        } catch (IOException ex) {
            Logger.getLogger(SchemaOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeHTMLReports(String outputFileFormat, String dataType, File finalReport, File file) {
        if (outputFileFormat.equals("HTML")) {
            if (dataType.equals("NON-CLOB")) {
                System.out.println("Arguements Non-Clob html : " + finalReport.getAbsolutePath() + " , " + file.getAbsolutePath() + " , " + directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html") + " , " + directory + "\\HTML-Reports\\");

                new ReportGenerator().createHTMLreport(finalReport.getAbsolutePath(), file.getAbsolutePath(), directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html"), directory + "\\HTML-Reports\\", true);

            } else {
                System.out.println("Arguements Non-Clob html : " + finalReport.getAbsolutePath() + " , " + file.getAbsolutePath() + " , " + directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html") + " , " + directory + "\\HTML-Reports\\");
                new ReportGenerator().createHTMLreport(finalReport.getAbsolutePath(), file.getAbsolutePath(), directory + "\\HTML-Reports\\" + finalReport.getName().replace("txt", "html"), directory + "\\HTML-Reports\\", false);
            }
        }
    }
}
