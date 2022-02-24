/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbHelper;

import XMLHelper.DatabaseDetailHelper;
import componentBean.DBConnection;
import componentBean.DBTable;
import componentBean.DatabaseObjects;
import gui.HomePage;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nimeshd
 */
public class DatabaseHelper {

    private static Connection conn = null;
    static String directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
    static File file;
    static Set<String> clobTables = new HashSet<>();
    static HashMap<String, List<String>> clobData = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh_mm");

    public DatabaseHelper() {
        directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
        DatabaseHelper.file = new File(directory + "\\Result_" + sdf.format(new Date(System.currentTimeMillis())) + ".txt");
        File f = new File(directory);
        if (!f.exists()) {
            f.mkdir();
        } else {
            System.out.println("Folder exists");
        }
    }

    @SuppressWarnings({"CallToPrintStackTrace", "null"})
    public static List<String> getAllTableList(DBConnection dBConnection, String User) throws SQLException {
        List<String> tables = new ArrayList<>();
        String tableName;
        String[] dBTables = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Integer count = 0;
            //String owner = User.substring(0, 6);
            final String owner = User;
            conn = new ConnectionProvider().getODSConnection(dBConnection);
            Thread t = new Thread() {
                @Override
                public void run() {
                    fetchClobTables(conn, owner);
                }
            };
            t.start();
            //new DatabaseHelperTest().checkForNETEnvironments(conn, dBConnection.getConnectionName(), conn, dBConnection.getConnectionName());
            //ResultSet rsTableList = conn.getMetaData().getTables(null, User.toUpperCase(), "%", new String[]{"TABLE", "SYNONYM"});
            ps = conn.prepareStatement("SELECT count(distinct object_name) FROM all_objects WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
                dBTables = new String[rs.getInt(1)];
            }
            System.out.println(User + "count - " + count);
            //ps.close();
            //rs.close();
            System.out.println("SELECT /*+ parallel(c,4)*/ distinct object_name FROM all_objects c WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ distinct object_name FROM all_objects c WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            ps.setFetchSize(300);
            rs = ps.executeQuery();
            System.out.println(User + " -- " + rs);
            int i = 0;
            //while (i < count && rs != null && rs.next()) {
            while (i < count && rs != null && rs.next()) {
                //System.out.println("Inside RS"+ User);
                tableName = rs.getString(1);
                if (i < 10) {
                    System.out.println(i + " and " + rs.getString(1) + " FIRST for User " + User);
                }
                if (tableName != null && !tableName.toUpperCase().contains("XML")) {
                    //System.out.println(i+" and "+rs.getString(1)+" SECOND User "+User);
                    dBTables[i++] = rs.getString(1);

                } else if (tableName.toUpperCase().contains("XML")) {
                    //errorTable.add(tableName+" table Not included in the comparison due to the presence of CLOB data.");
                }
            }
            t.join();
            //ps.closeOnCompletion();
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("ERROR for User" + User + " and DB: " + dBConnection.getConnectionName());
            ex.printStackTrace();
            new HomePage().mainHome(null);
        } catch (InterruptedException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                rs.close();
                ps.close();
                //conn.close();
            }
        }
        System.out.println("DB: " + dBConnection.getConnectionName() + " fetched list size:" + dBTables.length);
        tables.addAll(Arrays.asList(dBTables));
        tables.remove(clobTables);
        return tables;
    }

    private static Set<String> getIndexesList(DBConnection dbConnection, String dbUser) {
        Set<String> indexList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ DISTINCT INDEX_NAME from all_ind_columns c where INDEX_OWNER = '" + dbUser + "' ORDER BY INDEX_NAME");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ DISTINCT INDEX_NAME from all_ind_columns c where INDEX_OWNER = '" + dbUser + "' ORDER BY INDEX_NAME");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    indexList.add(rs.getString(1));
                }
                final Set<String> indexes = indexList;
                System.out.println("Saving indexes configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setIndexes(indexes);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("Indexes fetched for " + dbUser + ": " + indexList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indexList;
    }

    private static Set<String> getViewsList(DBConnection dbConnection, String dbUser) {
        Set<String> viewsList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ DISTINCT VIEW_NAME from all_views c where OWNER = '" + dbUser + "' ORDER BY VIEW_NAME");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ DISTINCT VIEW_NAME from all_views c where OWNER = '" + dbUser + "' ORDER BY VIEW_NAME");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    viewsList.add(rs.getString(1));
                }
                final Set<String> views = viewsList;
                System.out.println("Saving views configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setViews(views);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("Views fetched for " + dbUser + ": " + viewsList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return viewsList;
    }

    private static Set<String> getSynonymsList(DBConnection dbConnection, String dbUser) {
        Set<String> synonymList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ SYNONYM_NAME from all_synonyms c where OWNER = '" + dbUser + "' ORDER BY SYNONYM_NAME");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ SYNONYM_NAME from all_synonyms c where OWNER = '" + dbUser + "' ORDER BY SYNONYM_NAME");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    synonymList.add(rs.getString(1));
                }
                final Set<String> synonyms = synonymList;
                System.out.println("Saving synonyms configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setSynonyms(synonyms);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("Synonyms fetched for " + dbUser + ": " + synonymList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return synonymList;
    }

    private static Set<String> getDBLInkList(DBConnection dbConnection, String dbUser) {
        Set<String> dbLinksList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ DB_LINK from all_db_links c where OWNER = '" + dbUser + "' ORDER BY DB_LINK");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ DB_LINK from all_db_links c where OWNER = '" + dbUser + "' ORDER BY DB_LINK");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    dbLinksList.add(rs.getString(1));
                }
                final Set<String> dbLinks = dbLinksList;
                System.out.println("Saving dbLinks configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setDbLinks(dbLinks);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("DB Links fetched for " + dbUser + ": " + dbLinksList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dbLinksList;
    }

    private static Set<String> getTriggerList(DBConnection dbConnection, String dbUser) {
        Set<String> triggerList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ TRIGGER_NAME from all_triggers c where OWNER = '" + dbUser + "' ORDER BY TRIGGER_NAME");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ TRIGGER_NAME from all_triggers c where OWNER = '" + dbUser + "' ORDER BY TRIGGER_NAME");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    triggerList.add(rs.getString(1));
                }
                final Set<String> triggers = triggerList;
                System.out.println("Saving triggers configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setTriggers(triggers);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("Triggers fetched for " + dbUser + ":" + triggerList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return triggerList;
    }

    private static Set<String> getSequenceList(DBConnection dbConnection, String dbUser) {
        Set<String> sequenceList = new HashSet<>();
        try {
            int count = 0;
            final String connName = dbConnection.getConnectionName(), user = dbUser;
            System.out.println("SELECT /*+ parallel(c,4)*/ SEQUENCE_NAME from all_sequences c where SEQUENCE_OWNER = '" + dbUser + "' ORDER BY SEQUENCE_NAME");
            PreparedStatement ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ SEQUENCE_NAME from all_sequences c where SEQUENCE_OWNER = '" + dbUser + "' ORDER BY SEQUENCE_NAME");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    sequenceList.add(rs.getString(1));
                }
                final Set<String> sequences = sequenceList;
                System.out.println("Saving sequences configuration");
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
                        DatabaseObjects objects = new DatabaseObjects();
                        if (DBDetailHelper.checkIfDBSaved(connName, user)) {
                            objects = DBDetailHelper.getDBObject(connName, user);
                        }
                        objects.setSequences(sequences);
                        DBDetailHelper.saveDBObject(connName, user, objects);
                    }
                });
            } else {
                return null;
            }
            System.out.println("Sequences fetched:" + sequenceList.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sequenceList;
    }

    @SuppressWarnings("UnusedAssignment")
    public static void fetchClobTables(Connection conn, String User) {
        System.out.println("Fetching CLOB Tables");
        clobTables.clear();
        clobData.clear();
        String tableName = null, column = null;
        try {
            int count = 0;
            List<String> set = null;
            System.out.println("Fetching clob tables: select /*+ parallel(c,4)*/ distinct TABLE_NAME, COLUMN_NAME from all_tab_cols c where owner = '" + User + "' and data_type in ('CLOB', 'BLOB')");
            PreparedStatement ps = conn.prepareStatement("select /*+ parallel(c,4)*/ distinct TABLE_NAME, COLUMN_NAME from all_tab_cols c where owner = '" + User + "' and data_type in ('CLOB', 'BLOB')");
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                System.out.println("Fetching Clob Tables for User " + User);
                while (rs.next()) {
                    tableName = rs.getString(1);
                    column = rs.getString(2);
                    if (tableName != null && !tableName.isEmpty()) {
                        if (!clobTables.contains(tableName)) {
                            clobTables.add(tableName);
                            if (!clobData.containsKey(tableName)) {
                                set = new ArrayList<>();
                                set.add(column);
                                clobData.put(tableName, set);
                            } else {
                                clobData.get(tableName).add(column);
                            }
                        }
                    }
                }
            }
            System.out.println("Tables fetched for CLob are:" + clobTables.size());
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HashMap<String, List<String>> getClobData() {
        return clobData;
    }

    public void clearClobData() {
        clobData.clear();
    }

    public void initializeClobDataFromSavedConfig(Connection c, List<DBTable> tables, String User) {
        if (tables != null && !tables.isEmpty()) {
            for (DBTable table : tables) {
                try {
                    List<String> set = null;
                    String tableName = table.getName(), column = null;
                    PreparedStatement ps = c.prepareStatement("select /*+ parallel(c,4)*/ distinct COLUMN_NAME from all_tab_cols c where owner = '" + User + "' and data_type in ('CLOB', 'BLOB') AND TABLE_NAME = '" + tableName + "'");
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        System.out.println("Fetching Clob columns for User " + User);
                        while (rs.next()) {
                            column = rs.getString(1);
                            if (tableName != null && !tableName.isEmpty()) {
                                if (!clobTables.contains(tableName)) {
                                    clobTables.add(tableName);
                                    if (!clobData.containsKey(tableName)) {
                                        set = new ArrayList<>();
                                        set.add(column);
                                        clobData.put(tableName, set);
                                    } else {
                                        clobData.get(tableName).add(column);
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Tables fetched for CLob are:" + clobTables.size());
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static DatabaseObjects fetchSchema(DBConnection dbConnection, String dbUser, boolean fetchTables, boolean fetchSynonym, boolean fetchSequences,
            boolean fetchViews, boolean fetchTriggers, boolean fetchIndexes, boolean fetchDBLinks) {

        DatabaseObjects objects = new DatabaseObjects();
        try {
            conn = new ConnectionProvider().getODSConnection(dbConnection);
            List<DBTable> dBTables = new ArrayList<>();
            DBTable table;
            if (fetchTables) {
                System.out.println("fetching tables");
                List<String> tableList = getTableList(dbConnection, dbUser);
                if (tableList != null && !tableList.isEmpty()) {
                    for (String s : tableList) {
                        table = new DBTable();
                        table.setName(s);
                        dBTables.add(table);
                    }
                }
                objects.setTables(dBTables);
            }
            if (fetchIndexes) {
                System.out.println("fetch Index");
                Set<String> indexList = getIndexesList(dbConnection, dbUser);
                if (indexList != null && !indexList.isEmpty()) {
                    objects.setIndexes(indexList);
                }
            }
            if (fetchViews) {
                System.out.println("fetch View ");
                Set<String> viewList = getViewsList(dbConnection, dbUser);
                if (viewList != null && !viewList.isEmpty()) {
                    objects.setViews(viewList);
                }
            }
            if (fetchTriggers) {
                System.out.println("fetch Trigger ");
                Set<String> triggers = getTriggerList(dbConnection, dbUser);
                if (triggers != null && !triggers.isEmpty()) {
                    objects.setTriggers(triggers);
                }
            }
            if (fetchSequences) {
                System.out.println("fetch Sequence ");
                Set<String> sequences = getSequenceList(dbConnection, dbUser);
                if (sequences != null && !sequences.isEmpty()) {
                    objects.setSequences(sequences);
                }
            }
            if (fetchSynonym) {
                System.out.println("fetch Synonym  ");
                Set<String> synonymList = getSynonymsList(dbConnection, dbUser);
                if (synonymList != null && !synonymList.isEmpty()) {
                    objects.setSynonyms(synonymList);
                }
            }
            if (fetchDBLinks) {
                System.out.println("fetch DBLinks  ");
                Set<String> dbLinks = getDBLInkList(dbConnection, dbUser);
                if (dbLinks != null && !dbLinks.isEmpty()) {
                    objects.setDbLinks(dbLinks);
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                /*try {
                    //conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }
        }

        return objects;
    }

    //private static final String[] columnToIgnore = new String[]{"DL_UPDATE_STAMP"};
    private static final String[] columnToIgnore = new String[]{"SYS_CREATION_DATE", "SYS_UPDATE_DATE", "OPERATOR_ID", "APPLICATION_ID", "DL_SERVICE_CODE", "DL_UPDATE_STAMP"};

    public static boolean testDBConnection(DBConnection connection) throws SQLException {
        try {
            conn = new ConnectionProvider().getODSConnection(connection);
            if (conn != null) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            System.out.println(trace);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return false;
    }

    public static List<String> getUserList(DBConnection dBConnection) throws SQLException {
        List<String> owners = new ArrayList<>();
        try {
            conn = new ConnectionProvider().getODSConnection(dBConnection);
            // Query to extract Table List corresponding to the owner
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT owner FROM all_objects");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                owners.add(rs.getString("owner"));
            }
        } catch (SQLException e) {

        } catch (ClassNotFoundException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            System.out.println(trace);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return owners;
    }

    @SuppressWarnings({"CallToPrintStackTrace", "null"})
    public static List<String> getTableList(DBConnection dBConnection, String User) {
        System.out.println("Inside get table list");
        List<String> tables = new ArrayList<>();
        String tableName;
        String[] dBTables = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Integer count = 0;
            //String owner = User.substring(0, 6);
            final String owner = User;
            conn = new ConnectionProvider().getODSConnection(dBConnection);
            /*Thread t = new Thread() {
                @Override
                public void run() {
                    fetchClobTables(conn, owner);
                }
            };
            t.start();*/
            //new DatabaseHelperTest().checkForNETEnvironments(conn, dBConnection.getConnectionName(), conn, dBConnection.getConnectionName());
            //ResultSet rsTableList = conn.getMetaData().getTables(null, User.toUpperCase(), "%", new String[]{"TABLE", "SYNONYM"});
            ps = conn.prepareStatement("SELECT count(distinct object_name) FROM all_objects WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
                dBTables = new String[rs.getInt(1)];
            }
            System.out.println(User + "count - " + count);
            //ps.close();
            //rs.close();
            System.out.println("SELECT /*+ parallel(c,4)*/ distinct object_name FROM all_objects c WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            ps = conn.prepareStatement("SELECT /*+ parallel(c,4)*/ distinct object_name FROM all_objects c WHERE owner LIKE '" + owner + "%' AND object_type = 'TABLE' ORDER BY object_name");
            ps.setFetchSize(300);
            rs = ps.executeQuery();
            //System.out.println(User + " -- " + rs);
            int i = 0;
            //while (i < count && rs != null && rs.next()) {
            while (i < count && rs != null && rs.next()) {
                //System.out.println("Inside RS"+ User);
                tableName = rs.getString(1);
                /*if (i < 10) {
                    System.out.println(i + " and " + rs.getString(1) + " FIRST for User " + User);
                }*/
                if (tableName != null) {
                    //System.out.println(i+" and "+rs.getString(1)+" SECOND User "+User);
                    dBTables[i++] = rs.getString(1);
                }
            }
            //t.join();
            //ps.closeOnCompletion();
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("ERROR for User" + User + " and DB: " + dBConnection.getConnectionName());
            ex.printStackTrace();
            new HomePage().mainHome(null);
        } finally {
            if (conn != null) {
                try {
                    rs.close();
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
                //conn.close();
            }
        }
        System.out.println("DB: " + dBConnection.getConnectionName() + " fetched list size:" + dBTables.length);
        tables.addAll(Arrays.asList(dBTables));
        //tables.removeAll(clobTables);
        return tables;
    }

    public static List<String> getCLOBTableList(DBConnection dBConnection, String User) throws SQLException, InterruptedException {
        List<String> tables = new ArrayList<>();
        String tableName;
        String[] dBTables = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Integer count = 0;
            //String owner = User.substring(0, 6);
            final String owner = User;
            conn = new ConnectionProvider().getODSConnection(dBConnection);
            fetchClobTables(conn, owner);
            tables.addAll(clobTables);
            //System.out.println("Clob Tables fetched for User "+User+" : "+clobTables.toString().replace("[", "").replace("]", ""));
            Collections.sort(tables);
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("ERROR for User" + User + " and DB: " + dBConnection.getConnectionName());
            //ex.printStackTrace();
            new HomePage().mainHome(null);
        }
        return tables;
    }

    public static List<String> getColumnList(Connection conn, String tname, String Owner, boolean onlyFlatColumns) throws SQLException {
        List<String> columnList = new ArrayList<>();
        String[] columns;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Integer count = 0;
            String query = null;
            //new DatabaseHelperTest().checkForNETEnvironments(conn, dBConnection.getConnectionName(), conn, dBConnection.getConnectionName());
            ps = conn.prepareStatement("SELECT count(DISTINCT column_name) FROM all_tab_cols c WHERE owner = '" + Owner + "' AND table_name = '" + tname + "'");
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
                columns = new String[rs.getInt(1)];
            }
            //System.out.println(count);
            ps.close();
            rs.close();
            query = "SELECT /*+ parallel(c,4)*/ DISTINCT column_name FROM all_tab_cols c WHERE owner = '" + Owner + "' AND table_name = '" + tname + "' AND DATA_TYPE in ('BLOB', 'CLOB')";
            if (onlyFlatColumns) {
                query = "SELECT /*+ parallel(c,4)*/ DISTINCT column_name FROM all_tab_cols c WHERE owner = '" + Owner + "' AND table_name = '" + tname + "' AND DATA_TYPE not in ('BLOB', 'CLOB')";
            }
            System.out.println("Executing Query: " + query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            int i = 0;
            while (i < count && rs.next()) {
                if (!checkColumnToIgnore(rs.getString("column_name"), columnToIgnore)) {
                    columnList.add(rs.getString("column_name"));
                }
                i++;
            }
        } catch (SQLException ex) {
            //JOptionPane.showMessageDialog(null, "Problem occured while fetching columns from db. Try again.\n Error:" + ex.getMessage());
            new HomePage().mainHome(null);
            Logger.getLogger(DatabaseHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            rs.close();
            ps.close();
        }
        System.out.println(columnList.toString());
        return columnList;
    }

    public static List<String> getPrimaryKeys(Connection conn, String tname) throws SQLException {
        List<String> list = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet primaryKeys = meta.getPrimaryKeys(null, null, tname);
        while (primaryKeys.next()) {
            list.add(primaryKeys.getString("COLUMN_NAME"));
        }
        primaryKeys.close();
        return list;
    }

    private static boolean checkColumnToIgnore(String columnName, String[] columnToIgnore) {
        for (String s : columnToIgnore) {
            if (s.equals(columnName) || s.contains("$")) {
                return true;
            }
        }
        return false;
    }

    public static String getCustomURL(DBConnection connection) {
        String custURL;
        if (connection.getServiceID() != null) {
            custURL = "jdbc:oracle:thin:@" + connection.getHost() + ":" + connection.getPort() + ":" + connection.getServiceID();
        } else {
            custURL = "jdbc:oracle:thin:@" + connection.getHost() + ":" + connection.getPort() + "/" + connection.getServiceName();
        }
        return custURL;
    }
}
