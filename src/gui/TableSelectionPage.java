/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import XMLHelper.DatabaseDetailHelper;
import componentBean.DBConnection;
import componentBean.DBTable;
import componentBean.DatabaseObjects;
import componentBean.SessionParameter;
import dbHelper.ConnectionProvider;
import dbHelper.DatabaseHelper;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.xml.bind.JAXBException;
import xmlBean.Reference;

/**
 *
 * @author nimeshd
 */
public class TableSelectionPage extends javax.swing.JFrame {

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    String directory = System.getProperty("user.home") + "\\Desktop\\DBComp-Results";
    private JProgressBar progressBar;
    DatabaseDetailHelper DBDetailHelper = new DatabaseDetailHelper();
    static Set<String> errorTables = new HashSet<>();
    List<String> selectedTables = new ArrayList<>();
    final List<String> tempSelectedTables = new ArrayList<>();
    static String sourceEditedUser = null, targetEditedUser = null;
    public static List<SessionParameter> sessionParameter = new ArrayList<>();
    private static DefaultListModel<String> sourceTableListModel = new DefaultListModel<>();
    private static DefaultListModel<String> targetTableListModel = new DefaultListModel<>();
    private static DefaultListModel<String> sourceSelectedTableListModel = new DefaultListModel<>();
    private static DefaultListModel<String> targetSelectedTableListModel = new DefaultListModel<>();
    private static String sourceDB, sourceDBUser;
    private static String targetDB, targetDBUser;
    private static List<String> sourceTables = new ArrayList<>();
    private static List<String> targetTables = new ArrayList<>();
    private static String dataOption, schemaOption, dataType;
    private static String wantDynamic;
    private static TableSelectionPage tableSelectionPage;

    /**
     * Creates new form NewJFrame
     */
    public TableSelectionPage() {
        progressBar = new JProgressBar();
        File f = new File(directory);
        if (!f.exists()) {
            f.mkdir();
        }
        initComponents();
        jLabel5.setText("Message: Tables Loaded");
        setLabels();
        if (targetTables.isEmpty() && sourceTables.isEmpty()) {
            jLabel5.setText("Message: No tables found in both the DBs for the said schema. Please modify schema");
            jLabel5.setForeground(Color.red);
        } else if (sourceTables.isEmpty()) {
            jLabel5.setText("Message: No tables found in source DB under this schema. Please modify schema");
            jLabel5.setForeground(Color.red);
        } else if (targetTables.isEmpty()) {
            jLabel5.setText("Message: No tables found in target DB under this schema. Please modify schema");
            jLabel5.setForeground(Color.red);
        } else {
            jLabel5.setText("Message: Tables Loaded");
            jLabel5.setForeground(Color.black);
        }
    }

    public TableSelectionPage(File f, final List<SessionParameter> sp) {
        try {
            sessionParameter = sp;
            boolean isSchemaCompare = false;
            for (SessionParameter parameter : sp) {
                System.out.println(parameter.getParamName() + " : " + parameter.getParamValue());
                switch (parameter.getParamName().toUpperCase()) {
                    case "SOURCE_DB":
                        sourceDB = parameter.getParamValue();
                        break;
                    case "TARGET_DB":
                        targetDB = parameter.getParamValue();
                        break;
                    case "DATA_OPTION":
                        dataOption = parameter.getParamValue();
                        break;
                    case "DATA_TYPE":
                        dataType = parameter.getParamValue();
                        break;
                    case "SCHEMA_OPTION":
                        schemaOption = parameter.getParamValue();
                        isSchemaCompare = true;
                        break;
                    case "NEW-CONFIG":
                        wantDynamic = parameter.getParamValue();
                        break;
                    case "SOURCE_USER":
                        sourceEditedUser = parameter.getParamValue();
                        break;
                    case "TARGET_USER":
                        targetEditedUser = parameter.getParamValue();
                        break;
                    default:
                        break;
                }
            }

            Reference ref = new XMLHelper.ConnectionHelper().getDBReference(sourceDB);

            final DBConnection source = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            sourceDBUser = source.getUsername().toUpperCase();

            ref = new XMLHelper.ConnectionHelper().getDBReference(targetDB);
            final DBConnection target = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            targetDBUser = target.getUsername().toUpperCase();

            settleUsersForConsulta(source, sourceDBUser, target, targetDBUser);

            SessionParameter parameter = new SessionParameter();
            parameter.setParamName("sourceDBUser");
            parameter.setParamValue(sourceDBUser);
            sessionParameter.add(parameter);
            parameter = null;
            parameter = new SessionParameter();
            parameter.setParamName("targetDBUser");
            parameter.setParamValue(targetDBUser);
            sessionParameter.add(parameter);

            selectedTables.clear();
            Scanner scanner = new Scanner(f);
            String data;
            while (scanner.hasNextLine()) {
                data = scanner.nextLine().toUpperCase().trim().replaceAll(" ", ",");
                if (data != null && data.length() > 0) {
                    if (data.contains(",")) {
                        String[] tableNames = data.split(",");
                        for (String tab : tableNames) {
                            if (tab != null && tab.length() > 0) {
                                selectedTables.add(tab);
                            }
                        }
                    } else {
                        selectedTables.add(data);
                    }
                }
            }
            if (selectedTables.size() > 0) {
                new InProgressWindow(this, null, 2).setVisible(true);
                //new ColumnSelectionPage().columnSelectionMain(sessionParameter, selectedTables, null);
                System.out.println("Calling fetchClobTables");
                Connection conn = new ConnectionProvider().getODSConnection(source);
                DatabaseHelper.fetchClobTables(conn, sourceDBUser);
                System.out.println("--- Size of ClobDataMap - " + new DatabaseHelper().getClobData().size());
            } else {
                JOptionPane.showMessageDialog(null, "No tables fetched. Empty File.");
            }
        } catch (FileNotFoundException | JAXBException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TableSelectionPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton12 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList<>();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Jupiter - Data Comparer Tool");
        setLocation(new java.awt.Point(300, 150));
        setName("tableSelectionFrame"); // NOI18N

        jButton12.setText("Cancel");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Source Objects"));

        jButton1.setText(">");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setText(">>");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList4.setModel(sourceTableListModel);
        jScrollPane4.setViewportView(jList4);

        jList3.setModel(sourceSelectedTableListModel);
        jScrollPane3.setViewportView(jList3);

        jButton7.setText("<");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton6.setText("<<");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel1.setText("All Objects");

        jLabel2.setText("Selected Objects");

        jTextField5.setToolTipText("Search Table Name");
        jTextField5.setAutoscrolls(false);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField5KeyTyped(evt);
            }
        });

        jCheckBox1.setText("Name starts with");

        jLabel14.setText("User             - ");

        jLabel15.setText("Database     - ");

        jLabel16.setText(":");
        jLabel16.setName("statusLabel2"); // NOI18N

        jLabel17.setText(":");
        jLabel17.setName("statusLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(15, 15, 15))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 29, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap())))
        );

        jButton17.setText("Finish");
        jButton17.setEnabled(false);

        jButton19.setText("Next");
        jButton19.setName("next"); // NOI18N
        jButton19.setSelected(true);
        jButton19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton19MousePressed(evt);
            }
        });
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Target Objects"));

        jTextField4.setToolTipText("Search Table Name");
        jTextField4.setAutoscrolls(false);
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4KeyTyped(evt);
            }
        });

        jList7.setModel(targetTableListModel);
        jScrollPane7.setViewportView(jList7);

        jList8.setModel(targetSelectedTableListModel);
        jScrollPane8.setViewportView(jList8);

        jLabel3.setText("All Objects");

        jLabel4.setText("Selected Objects");

        jButton3.setText(">");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton9.setText("<<");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("<");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton8.setText(">>");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel6.setText("Database     - ");

        jLabel7.setText("User             - ");

        jLabel8.setText(":");

        jLabel9.setText(":");

        jCheckBox2.setText("Name starts with");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(72, 72, 72)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel3))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 20, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addGap(57, 57, 57))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox2))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );

        jLabel5.setText("Message");

        jMenu1.setText("Connections");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Import");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Export");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Manage");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Exit");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Task");

        jMenuItem9.setText("Manage");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuItem10.setText("Import");
        jMenuItem10.setAutoscrolls(true);
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuItem11.setText("Export");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem6.setText("User Guide");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem8.setText("IDE Logs");
        jMenuItem8.setEnabled(false);
        jMenu3.add(jMenuItem8);

        jMenuItem13.setText("Check for Updates");
        jMenu3.add(jMenuItem13);

        jMenuItem7.setText("About");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton19)
                        .addGap(8, 8, 8)
                        .addComponent(jButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jButton17)
                    .addComponent(jButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        AboutDBComp.aboutMain();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jLabel5.setText("Message:");
        int count = 0;
        List<String> sourceSelectedTables = jList4.getSelectedValuesList();
        for (String s : sourceSelectedTables) {
            if (targetTableListModel.contains(s)) {
                if (!sourceSelectedTableListModel.contains(s) && !targetSelectedTableListModel.contains(s)) {
                    targetSelectedTableListModel.addElement(s);
                    sourceSelectedTableListModel.addElement(s);
                }
            } else {
                count++;
                if (!errorTables.contains(s + " with error: Object not found in Target Schema")) {
                    errorTables.add(s + " with error: Object not found in Target Schema");
                }
            }
        }
        if (count > 0) {
            String errorMessage = "Corresponding Tables in Target/Source DB was not found for " + count + " selected tables";
            JOptionPane.showMessageDialog(null, errorMessage);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        jLabel5.setText("Message:");
        int count = 0, i;
        sourceSelectedTableListModel.removeAllElements();
        DefaultListModel<String> selectedAllModel = (DefaultListModel<String>) jList4.getModel();
        for (i = 0; i < selectedAllModel.getSize(); i++) {
            if (targetTableListModel.contains(selectedAllModel.getElementAt(i))) {
                if (!sourceSelectedTableListModel.contains(selectedAllModel.getElementAt(i)) && !targetSelectedTableListModel.contains(selectedAllModel.getElementAt(i))) {
                    sourceSelectedTableListModel.addElement(selectedAllModel.getElementAt(i));
                    targetSelectedTableListModel.addElement(selectedAllModel.getElementAt(i));
                }
            } else {
                count++;
                if (!errorTables.contains(selectedAllModel.getElementAt(i) + " with error: Object not found in Target Schema")) {
                    errorTables.add(selectedAllModel.getElementAt(i) + " with error: Object not found in Target Schema");
                }
            }
        }
        if (count > 1) {
            JOptionPane.showMessageDialog(null, count + " Tables out of " + (i - 1) + " didn't correspond to any target table");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        sourceSelectedTableListModel.removeAllElements();
        targetSelectedTableListModel.removeAllElements();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        jLabel5.setText("Message:");
        List<String> selectedTableListToRemove = jList3.getSelectedValuesList();
        for (int i = 0; i < sourceSelectedTableListModel.getSize(); i++) {
            if (selectedTableListToRemove.contains(sourceSelectedTableListModel.getElementAt(i))) {
                sourceSelectedTableListModel.removeElementAt(i);
                targetSelectedTableListModel.removeElementAt(i);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jLabel5.setText("Message:");
        int count = 0;
        List<String> targetSelectedTables = jList7.getSelectedValuesList();
        for (String s : targetSelectedTables) {
            if (sourceTableListModel.contains(s)) {
                if (!targetSelectedTableListModel.contains(s) && !sourceSelectedTableListModel.contains(s)) {
                    targetSelectedTableListModel.addElement(s);
                    sourceSelectedTableListModel.addElement(s);
                }
            } else {
                count++;
                if (!errorTables.contains(s + " with error: Object not found in Source Schema")) {
                    errorTables.add(s + " with error: Object not found in Source Schema");
                }
            }
        }
        if (count > 0) {
            String errorMessage = "Corresponding Tables in Target/Source DB was not found for " + count + " selected tables";
            JOptionPane.showMessageDialog(null, errorMessage);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        int count = 0, i;
        jLabel5.setText("Message:");
        targetSelectedTableListModel.removeAllElements();
        DefaultListModel<String> selectedAllModel = (DefaultListModel<String>) jList7.getModel();
        for (i = 0; i < selectedAllModel.getSize(); i++) {
            if (sourceTableListModel.contains(selectedAllModel.getElementAt(i))) {
                if (!sourceSelectedTableListModel.contains(selectedAllModel.getElementAt(i)) && !targetSelectedTableListModel.contains(selectedAllModel.getElementAt(i))) {
                    sourceSelectedTableListModel.addElement(selectedAllModel.getElementAt(i));
                    targetSelectedTableListModel.addElement(selectedAllModel.getElementAt(i));
                }
            } else {
                count++;
                if (!errorTables.contains(selectedAllModel.getElementAt(i) + " with error: Object not found in Source Schema")) {
                    errorTables.add(selectedAllModel.getElementAt(i) + " with error: Object not found in Source Schema");
                }
            }

        }
        if (count > 1) {
            JOptionPane.showMessageDialog(null, count + " Tables out of " + (i - 1) + " didn't correspond to any source table");
            jLabel5.setText("Message: Make sure owners are relatable. A Reference DB might not contain same tables as Application DB.");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        sourceSelectedTableListModel.removeAllElements();
        targetSelectedTableListModel.removeAllElements();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        List<String> selectedTableListToRemove = jList8.getSelectedValuesList();
        for (int i = 0; i < targetSelectedTableListModel.getSize(); i++) {
            if (selectedTableListToRemove.contains(targetSelectedTableListModel.getElementAt(i))) {
                sourceSelectedTableListModel.removeElementAt(i);
                targetSelectedTableListModel.removeElementAt(i);
            }
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        if (schemaOption == null) {
            jLabel5.setText("INFO: Fetching columns for the selected tables. Please wait....");
            jLabel5.setForeground(Color.BLUE);
            // TODO add your handling code here:
            selectedTables.clear();
            for (int i = 0; i < sourceSelectedTableListModel.getSize(); i++) {
                if (targetSelectedTableListModel.contains(sourceSelectedTableListModel.getElementAt(i))) {
                    selectedTables.add(sourceSelectedTableListModel.getElementAt(i));
                }
            }
            SessionParameter sp = new SessionParameter();
            sp.setParamName("sourceDBUser");
            sp.setParamValue(sourceDBUser);
            sessionParameter.add(sp);
            sp = null;
            sp = new SessionParameter();
            sp.setParamName("targetDBUser");
            sp.setParamValue(targetDBUser);
            sessionParameter.add(sp);

            if (errorTables.size() > 0) {
                sp = null;
                sp = new SessionParameter();
                sp.setParamName("error_Table");
                sp.setParamValue(errorTables.toString().replace("[", "").replace("]", ""));
                sessionParameter.add(sp);
            }

            if (selectedTables.size() > 0) {
                new InProgressWindow(this, null, 2).setVisible(true);
                //new ColumnSelectionPage().columnSelectionMain(sessionParameter, selectedTables, null);
                jLabel17.setForeground(Color.black);
            } else {
                JOptionPane.showMessageDialog(null, "No valid tables selected. Please select atleast one table to proceed.");
                jLabel5.setText("Error: No Tables selected");
                jLabel5.setForeground(Color.RED);
            }
        } else {
            jLabel5.setText("INFO: Initializing DDL objects. Please wait....");
            jLabel5.setForeground(Color.BLUE);

            SessionParameter sp = new SessionParameter();
            sp.setParamName("sourceDBUser");
            sp.setParamValue(sourceDBUser);
            sessionParameter.add(sp);
            sp = null;
            sp = new SessionParameter();
            sp.setParamName("targetDBUser");
            sp.setParamValue(targetDBUser);
            sessionParameter.add(sp);

            String element, name[];
            DatabaseObjects databaseObjects = new DatabaseObjects();
            DBTable dBTable;

            for (int i = 0; i < sourceSelectedTableListModel.getSize(); i++) {
                element = sourceSelectedTableListModel.getElementAt(i);
                if (targetSelectedTableListModel.contains(element)) {
                    name = element.split("/");
                    switch (name[0]) {
                        case "table":
                            if (databaseObjects.getTables() == null) {
                                List<DBTable> tables = new ArrayList<>();
                                dBTable = new DBTable();
                                dBTable.setName(name[1]);
                                tables.add(dBTable);
                                databaseObjects.setTables(tables);
                            } else {
                                dBTable = new DBTable();
                                dBTable.setName(name[1]);
                                databaseObjects.getTables().add(dBTable);
                            }
                            break;
                        case "view":
                            if (databaseObjects.getViews() == null) {
                                Set<String> views = new HashSet<>();
                                views.add(name[1]);
                                databaseObjects.setViews(views);
                            } else {
                                databaseObjects.getViews().add(name[1]);
                            }
                            break;
                        case "sequence":
                            if (databaseObjects.getSequences() == null) {
                                Set<String> sequences = new HashSet<>();
                                sequences.add(name[1]);
                                databaseObjects.setSequences(sequences);
                            } else {
                                databaseObjects.getSequences().add(name[1]);
                            }
                            break;
                        case "synonym":
                            if (databaseObjects.getSynonyms() == null) {
                                Set<String> synonym = new HashSet<>();
                                synonym.add(name[1]);
                                databaseObjects.setSynonyms(synonym);
                            } else {
                                databaseObjects.getSynonyms().add(name[1]);
                            }
                            break;
                        case "trigger":
                            if (databaseObjects.getTriggers() == null) {
                                Set<String> trigger = new HashSet<>();
                                trigger.add(name[1]);
                                databaseObjects.setTriggers(trigger);
                            } else {
                                databaseObjects.getTriggers().add(name[1]);
                            }
                            break;
                        case "dblink":
                            if (databaseObjects.getDbLinks() == null) {
                                Set<String> dblink = new HashSet<>();
                                dblink.add(name[1]);
                                databaseObjects.setDbLinks(dblink);
                            } else {
                                databaseObjects.getDbLinks().add(name[1]);
                            }
                            break;
                        case "index":
                            if (databaseObjects.getIndexes() == null) {
                                Set<String> indexes = new HashSet<>();
                                indexes.add(name[1]);
                                databaseObjects.setIndexes(indexes);
                            } else {
                                databaseObjects.getIndexes().add(name[1]);
                            }
                            break;
                    }
                }
            }

            if (errorTables.size() > 0) {
                sp = null;
                sp = new SessionParameter();
                sp.setParamName("error_Table");
                sp.setParamValue(errorTables.toString().replace("[", "").replace("]", ""));
                sessionParameter.add(sp);
            }
            if (databaseObjects != null) {
                new SchemaOptionSelection().schemaSelectionMain(sessionParameter, databaseObjects, null);
                this.setVisible(false);
                //new ColumnSelectionPage().columnSelectionMain(sessionParameter, selectedTables, null);
                jLabel17.setForeground(Color.black);
            } else {
                JOptionPane.showMessageDialog(null, "No valid objects selected. Please select atleast one object to proceed.");
                jLabel5.setText("Error: No Tables selected");
                jLabel5.setForeground(Color.RED);
            }
        }
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        sourceTables.clear();
        targetTables.clear();
        errorTables.clear();
        selectedTables.clear();
        dispose();
        new HomePage().mainHome(null);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        ManageTasks.manageTasksMain(tableSelectionPage);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            //URL url = getClass().getClassLoader().getResource("resource/UserGuide.pdf");
            /*URL url = TableSelectionPage.class.getResource("/resource/UserGuide.pdf");

            System.out.println("URL to UserGuide: " + url.getPath());
            System.out.println(System.getProperty("java.class.path"));

            File f = new File(url.getFile());
            if (!f.exists()) {
                JOptionPane.showMessageDialog(null, "User Guide missing. Please contact the author.");
                System.out.println("Check URL - " + url);
                url = TableSelectionPage.class.getResource("/resource");
                System.out.println("Does Resource accessible? " + new File(url.getFile()).exists());
            } else {
                //Desktop.getDesktop().open(f);
            }*/

            File jarPath = new File(TableSelectionPage.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String deploymentPath = jarPath.getParentFile().getAbsolutePath().replace("%20", " ");
            String pathToUserGuide = deploymentPath + "\\resource\\UserGuide.pdf";

            File userGuide = new File(pathToUserGuide);
            if (userGuide.exists()) {
                Desktop.getDesktop().open(userGuide);
            } else {
                JOptionPane.showMessageDialog(null, "User Guide missing. Please contact the author.");
                System.out.println("Check URL - " + pathToUserGuide);
                System.out.println("Does Resource accessible? " + new File(pathToUserGuide).exists());
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "User Guide missing. Please contact the author.");
            URL url = TableSelectionPage.class.getResource("/resource");
            System.out.println("Does Resource accessible? " + new File(url.getFile()).exists());
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Error occurred: " + ex.getMessage() + ".  ");
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Error Occurred : " + ex.getMessage() + ". ");
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        ManageConnection.manageConnectionMain();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        new XMLHelper.ImportExportConnection().exportDBConnection();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        new XMLHelper.ImportExportConnection().importDBConnection();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        ManageConnection.manageConnectionMain();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        new XMLHelper.ImportExportTasks().importTasks();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        new XMLHelper.ImportExportTasks().exportTasks();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyTyped
        // TODO add your handling code here:
        String key = jTextField5.getText().toLowerCase();
        DefaultListModel<String> model = new DefaultListModel<>();
        jList4.setModel(sourceTableListModel);
        if (key != null) {
            for (String s : sourceTables) {
                if (jCheckBox1.isSelected() && s != null && s.toLowerCase().startsWith(key)) {
                    model.addElement(s);
                }
                if (!jCheckBox1.isSelected() && s != null && s.toLowerCase().contains(key)) {
                    model.addElement(s);
                }
            }
            if (model.size() == 0) {
                model.addElement("No Table Found");
            }
            jList4.setModel(model);
        } else {
            jList4.setModel(sourceTableListModel);
        }
    }//GEN-LAST:event_jTextField5KeyTyped

    private void jTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyTyped
        // TODO add your handling code here:
        String key = jTextField4.getText().toLowerCase();
        DefaultListModel<String> model = new DefaultListModel<>();
        jList7.setModel(targetTableListModel);
        if (key != null) {
            for (String s : targetTables) {
                if (jCheckBox2.isSelected() && s != null && s.toLowerCase().startsWith(key)) {
                    model.addElement(s);
                }
                if (!jCheckBox2.isSelected() && s != null && s.toLowerCase().contains(key)) {
                    model.addElement(s);
                }
            }
            if (model.size() == 0) {
                model.addElement("No Table Found");
            }
            jList7.setModel(model);
        } else {
            jList7.setModel(targetTableListModel);
        }
    }//GEN-LAST:event_jTextField4KeyTyped

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton19MousePressed
        jLabel5.setText("Message: Initializing columns for the selected tables. Please wait...");
        jLabel5.setForeground(Color.BLUE);
        //jButton19.setEnabled(false);
    }//GEN-LAST:event_jButton19MousePressed

    private void setLabels() {
        if (sourceDB != null && targetDB != null && targetDBUser != null && sourceDBUser != null) {
            jLabel16.setText(sourceDB);
            jLabel17.setText(sourceDBUser);
            jLabel8.setText(targetDB);
            jLabel9.setText(targetDBUser);
        }
    }

    public void tableSelectionMain(final List<SessionParameter> sp) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            sourceEditedUser = null;
            targetEditedUser = null;
            if (sp != null) {
                sessionParameter = sp;
                sourceTableListModel.removeAllElements();
                targetTableListModel.removeAllElements();
                sourceSelectedTableListModel.removeAllElements();
                targetSelectedTableListModel.removeAllElements();
                boolean isSchemaCompare = false;
                dataOption = null;
                schemaOption = null;

                for (SessionParameter parameter : sp) {
                    System.out.println(parameter.getParamName() + " : " + parameter.getParamValue());
                    switch (parameter.getParamName().toUpperCase()) {
                        case "SOURCE_DB":
                            sourceDB = parameter.getParamValue();
                            break;
                        case "TARGET_DB":
                            targetDB = parameter.getParamValue();
                            break;
                        case "DATA_OPTION":
                            dataOption = parameter.getParamValue();
                            break;
                        case "DATA_TYPE":
                            dataType = parameter.getParamValue();
                            break;
                        case "SCHEMA_OPTION":
                            schemaOption = parameter.getParamValue();
                            isSchemaCompare = true;
                            break;
                        case "NEW-CONFIG":
                            wantDynamic = parameter.getParamValue();
                            break;
                        case "SOURCE_USER":
                            sourceEditedUser = parameter.getParamValue();
                            break;
                        case "TARGET_USER":
                            targetEditedUser = parameter.getParamValue();
                            break;
                        default:
                            break;
                    }
                }
                if (sourceDB != null && targetDB != null && !isSchemaCompare) {
                    System.out.println("Waiting for data to be fetched and filled...");
                    initializeTables();
                    try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                            if ("Windows".equals(info.getName())) {
                                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                        StringBuilder sb = new StringBuilder(ex.toString());
                        for (StackTraceElement ste : ex.getStackTrace()) {
                            sb.append("\n\t at ");
                            sb.append(ste);
                        }
                        String trace = sb.toString();
                        //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);

                        JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". ");
                        dispose();
                        new HomePage().mainHome(null);
                    }
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tableSelectionPage = new TableSelectionPage();
                            tableSelectionPage.setVisible(true);
                        }
                    });

                    System.out.println("Calling fetchClobTables");
                    Reference ref = new XMLHelper.ConnectionHelper().getDBReference(sourceDB);
                    final DBConnection source = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
                    Connection conn = new ConnectionProvider().getODSConnection(source);
                    DatabaseHelper.fetchClobTables(conn, sourceDBUser);
                    System.out.println("--- Size of ClobDataMap - " + new DatabaseHelper().getClobData().size());

                } else if (isSchemaCompare) {
                    System.out.println("Fetching Schema objects...");
                    intializeDDLObjects();
                    try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                            if ("Windows".equals(info.getName())) {
                                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                        JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ".");
                        dispose();
                        new HomePage().mainHome(null);
                    }
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            tableSelectionPage = new TableSelectionPage();
                            tableSelectionPage.setVisible(true);
                        }
                    });
                } else {
                    System.out.println("Connections are null");
                    new HomePage().mainHome(null);
                }

                /*Thread threadToLoadSourceTable = new Thread() {
                    @Override
                    public void run() {

                        // CHange start to disable another thread
                    }
                };*/
 /*Thread threadToLoadTargetTable = new Thread() {
                    @Override
                    public void run() {
                        /*for (SessionParameter parameter : sp) {
                            if (parameter.getParamName().equals("TARGET_DB")) {
                                try {
                                    targetDB = parameter.getParamValue();
                                    Reference ref = new XMLHelper.ConnectionHelper().getDBReference(parameter.getParamValue());
                                    DBConnection target = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
                                    targetDBUser = target.getUsername().toUpperCase();
                                    if (targetDBUser.startsWith("CONSULTA")) {
                                        if (target.getConnectionName().toUpperCase().contains("PROD")) {
                                            System.out.println("Setting username PRD1REFWAIT");
                                            targetDBUser = "PRD1REFWAIT";
                                        } else if (target.getConnectionName().toUpperCase().contains("PET")) {
                                            System.out.println("Setting username PET1REFWAIT");
                                            targetDBUser = "PET1REFWAIT";
                                        } else if (target.getConnectionName().toUpperCase().contains("UAT")) {
                                            String uatNum = target.getConnectionName().toUpperCase().split("UAT")[1].replace("[a-zA-Z]", "");
                                            System.out.println("Setting username NETAPP" + uatNum);
                                            targetDBUser = "NETAPP" + uatNum;
                                        }
                                    }
                                    targetTables = DatabaseHelperTest.getTableList(target, targetDBUser);
                                    System.out.println("Fetched " + targetTables.size() + " tables from target db.");
                                    for (String table : targetTables) {
                                        targetTableListModel.addElement(table);
                                    }
                                    System.out.println("Here targetTableListModel size is" + targetTableListModel.size());
                                } catch (JAXBException | SQLException | InterruptedException ex) {
                                    JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please retry.");
                                    dispose();
                                    new HomePage().mainHome(null);
                                } catch (Exception ex) {
                                    System.out.println("Inside Exception");
                                    ex.printStackTrace();
                                }
                            }
                        }*
                    }
                };*/
            } else {
                this.setVisible(true);
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please retry.");
            dispose();
            new HomePage().mainHome(null);
        } catch (JAXBException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please retry.");
        } catch (ClassNotFoundException | SQLException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please retry.");
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please retry.");
        }
    }

    /*public static void main(String[] args) {
        /* Set the Nimbus look and feel 
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         *
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TableSelectionPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TableSelectionPage().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList7;
    private javax.swing.JList<String> jList8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

    public void initializeTables() {
        try {
            Reference ref = new XMLHelper.ConnectionHelper().getDBReference(sourceDB);

            final DBConnection source = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            sourceDBUser = source.getUsername().toUpperCase();

            ref = new XMLHelper.ConnectionHelper().getDBReference(targetDB);
            final DBConnection target = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            targetDBUser = target.getUsername().toUpperCase();

            settleUsersForConsulta(source, sourceDBUser, target, targetDBUser);

            final String sourceConnName = source.getConnectionName();
            final String targetConnName = target.getConnectionName();

            switch (dataType) {
                case "NON-CLOB":

                    System.out.println("Populating source table");
                    DatabaseObjects objects = null;
                    boolean fetchFromDB = true;
                    if (DBDetailHelper.checkIfDBSaved(sourceConnName, sourceDBUser) && wantDynamic.equals("FALSE")) {
                        List<DBTable> tables = DBDetailHelper.getTables(sourceConnName, sourceDBUser, "NON-CLOB");
                        if (tables != null && !tables.isEmpty()) {
                            for (DBTable dbt : tables) {
                                if (!sourceTableListModel.contains(dbt.getName())) {
                                    sourceTables.add(dbt.getName());
                                    sourceTableListModel.addElement(dbt.getName());
                                }
                            }
                            fetchFromDB = false;
                        }
                        System.out.println("-- Fetched " + sourceTables.size() + " tables from source db." + fetchFromDB);
                    }
                    if (fetchFromDB) {
                        sourceTables = DatabaseHelper.getTableList(source, sourceDBUser);
                        System.out.println("Fetched " + sourceTables.size() + " tables from source db.");
                        for (String table : sourceTables) {
                            if (!sourceTableListModel.contains(table)) {
                                sourceTableListModel.addElement(table);
                            }
                        }
                        System.out.println("Saving configuration");
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                List<DBTable> tables = new ArrayList<>();
                                for (String table : sourceTables) {
                                    DBTable dbt = new DBTable();
                                    dbt.setName(table);
                                    if (!tables.contains(dbt)) {
                                        tables.add(dbt);
                                    }
                                }
                                DatabaseObjects objects = new DatabaseObjects();
                                if (DBDetailHelper.checkIfDBSaved(sourceConnName, sourceDBUser)) {
                                    objects = DBDetailHelper.getDBObject(sourceConnName, sourceDBUser);
                                }
                                objects.setTables(tables);
                                DBDetailHelper.saveDBObject(sourceConnName, sourceDBUser, objects);
                            }
                        });
                    }
                    fetchFromDB = true;
                    System.out.println("Populating target table");
                    if (DBDetailHelper.checkIfDBSaved(targetConnName, targetDBUser) && wantDynamic.equals("FALSE")) {
                        List<DBTable> tables = DBDetailHelper.getTables(targetConnName, targetDBUser, "NON-CLOB");
                        if (tables != null && !tables.isEmpty()) {
                            for (DBTable dbt : tables) {
                                if (!targetTableListModel.contains(dbt.getName())) {
                                    targetTables.add(dbt.getName());
                                    targetTableListModel.addElement(dbt.getName());
                                }
                            }
                            fetchFromDB = false;
                        }
                        System.out.println("-- Fetched " + targetTables.size() + " tables from target db." + fetchFromDB);
                    }
                    if (fetchFromDB) {
                        System.out.println("Inside call to DB");
                        targetTables = DatabaseHelper.getTableList(target, targetDBUser);
                        System.out.println("Fetched " + targetTables.size() + " tables from target db.");
                        for (String table : targetTables) {
                            if (!targetTableListModel.contains(table)) {
                                targetTableListModel.addElement(table);
                            }
                        }
                        System.out.println("Saving configuration");
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                List<DBTable> tables = new ArrayList<>();
                                for (String table : targetTables) {
                                    DBTable dbt = new DBTable();
                                    dbt.setName(table);
                                    if (!tables.contains(dbt)) {
                                        tables.add(dbt);
                                    }
                                }
                                DatabaseObjects objects = new DatabaseObjects();
                                if (DBDetailHelper.checkIfDBSaved(targetConnName, targetDBUser)) {
                                    objects = DBDetailHelper.getDBObject(targetConnName, targetDBUser);
                                }
                                objects.setTables(tables);
                                DBDetailHelper.saveDBObject(targetConnName, targetDBUser, objects);
                            }
                        });
                    }

                    break;
                case "CLOB":
                    try {
                        System.out.println("Populating source table with CLOB data");
                        objects = null;
                        fetchFromDB = true;
                        if (DBDetailHelper.checkIfDBSaved(sourceConnName, sourceDBUser) && wantDynamic.equals("FALSE")) {
                            List<DBTable> tables = DBDetailHelper.getTables(sourceConnName, sourceDBUser, "CLOB");
                            if (tables != null && !tables.isEmpty()) {
                                for (DBTable dbt : tables) {
                                    if (!sourceTableListModel.contains(dbt.getName())) {
                                        sourceTables.add(dbt.getName());
                                        sourceTableListModel.addElement(dbt.getName());
                                    }
                                }
                                fetchFromDB = false;
                                System.out.println("Fetched " + sourceTables.size() + " tables from source db.");
                            }
                        }
                        if (fetchFromDB) {
                            sourceTables.clear();
                            sourceTables = DatabaseHelper.getCLOBTableList(source, sourceDBUser);
                            System.out.println("Fetched " + sourceTables.size() + " tables from source db.");
                            for (String table : sourceTables) {
                                if (!sourceTableListModel.contains(table)) {
                                    sourceTableListModel.addElement(table);
                                }
                            }
                            System.out.println("Saving configuration");
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap<String, List<String>> clobData = new DatabaseHelper().getClobData();
                                    List<DBTable> tables = new ArrayList<>();
                                    for (String table : sourceTables) {
                                        DBTable dbt = new DBTable();
                                        dbt.setName(table);
                                        dbt.setColumnList(clobData.get(dbt.getName()));
                                        if (!tables.contains(dbt)) {
                                            tables.add(dbt);
                                        }
                                    }
                                    DatabaseObjects objects = new DatabaseObjects();
                                    if (DBDetailHelper.checkIfDBSaved(sourceConnName, sourceDBUser)) {
                                        objects = DBDetailHelper.getDBObject(sourceConnName, sourceDBUser);
                                    }
                                    objects.setClobs(tables);
                                    DBDetailHelper.saveDBObject(sourceConnName, sourceDBUser, objects);
                                }
                            });
                        }
                        fetchFromDB = true;

                        System.out.println("Populating target table with CLOB data");

                        if (DBDetailHelper.checkIfDBSaved(targetConnName, targetDBUser) && wantDynamic.equals("FALSE")) {
                            List<DBTable> tables = DBDetailHelper.getTables(targetConnName, targetDBUser, "CLOB");
                            if (tables != null && !tables.isEmpty()) {
                                for (DBTable dbt : tables) {
                                    if (!targetTableListModel.contains(dbt.getName())) {
                                        targetTables.add(dbt.getName());
                                        targetTableListModel.addElement(dbt.getName());
                                    }
                                }
                                fetchFromDB = false;
                                System.out.println("Fetched " + targetTables.size() + " tables from source db.");
                            }

                        }
                        if (fetchFromDB) {
                            targetTables.clear();
                            targetTables = DatabaseHelper.getCLOBTableList(target, targetDBUser);
                            System.out.println("Fetched " + targetTables.size() + " tables from target db.");
                            for (String table : targetTables) {
                                if (!targetTableListModel.contains(table)) {
                                    targetTableListModel.addElement(table);
                                }
                            }
                            System.out.println("Saving configuration");
                            java.awt.EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    List<DBTable> tables = new ArrayList<>();
                                    HashMap<String, List<String>> clobData = new DatabaseHelper().getClobData();
                                    for (String table : targetTables) {
                                        DBTable dbt = new DBTable();
                                        dbt.setName(table);
                                        dbt.setColumnList(clobData.get(dbt.getName()));
                                        if (!tables.contains(dbt)) {
                                            tables.add(dbt);
                                        }
                                    }
                                    DatabaseObjects objects = new DatabaseObjects();
                                    if (DBDetailHelper.checkIfDBSaved(targetConnName, targetDBUser)) {
                                        objects = DBDetailHelper.getDBObject(targetConnName, targetDBUser);
                                    }
                                    objects.setClobs(tables);
                                    DBDetailHelper.saveDBObject(targetConnName, targetDBUser, objects);
                                }
                            });
                        }
                    } catch (SQLException | InterruptedException ex) {
                        StringBuilder sb = new StringBuilder(ex.toString());
                        for (StackTraceElement ste : ex.getStackTrace()) {
                            sb.append("\n\t at ");
                            sb.append(ste);
                        }
                        String trace = sb.toString();
                        //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
                        JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please and retry.");
                        dispose();
                        new HomePage().mainHome(null);
                    } catch (Exception ex) {
                        StringBuilder sb = new StringBuilder(ex.toString());
                        for (StackTraceElement ste : ex.getStackTrace()) {
                            sb.append("\n\t at ");
                            sb.append(ste);
                        }
                        String trace = sb.toString();
                        // IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
                    }
                    break;
                default:
                    /*System.out.println("Populating source table");
                    sourceTables = DatabaseHelper.getAllTableList(source, sourceDBUser);
                    System.out.println("Fetched " + sourceTables.size() + " tables from source db.");
                    for (String table : sourceTables) {
                        sourceTableListModel.addElement(table);
                    }
                    System.out.println("Populating target table");
                    targetTables = DatabaseHelper.getAllTableList(target, targetDBUser);
                    System.out.println("Fetched " + targetTables.size() + " tables from target db.");
                    for (String table : targetTables) {
                        targetTableListModel.addElement(table);
                    }
                    System.out.println("Here targetTableListModel size is" + targetTableListModel.size());*/
                    break;
            }

        } catch (JAXBException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please and retry.");
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
        }
    }

    public void loadColumns() {
        try {
            new ColumnSelectionPage().columnSelectionMain(sessionParameter, selectedTables, null);
            this.setVisible(false);

        } catch (JAXBException | InterruptedException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Unexpected Error Occurred:" + ex.getMessage() + ". Please and retry.");
            dispose();
            new HomePage().mainHome(null);
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
        }
    }

    private void intializeDDLObjects() {
        try {
            Reference ref = new XMLHelper.ConnectionHelper().getDBReference(sourceDB);

            final DBConnection source = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            sourceDBUser = source.getUsername().toUpperCase();

            ref = new XMLHelper.ConnectionHelper().getDBReference(targetDB);
            final DBConnection target = new XMLHelper.ConnectionHelper().extractDBConnection(ref);
            targetDBUser = target.getUsername().toUpperCase();

            settleUsersForConsulta(source, sourceDBUser, target, targetDBUser);

            final String sourceConnName = source.getConnectionName();
            final String targetConnName = target.getConnectionName();

            boolean fetchTables = false, fetchViews = false, fetchSequences = false, fetchSynonym = false, fetchDBLinks = false,
                    fetchTriggers = false, fetchIndexes = false;
            if (schemaOption.contains("ALL") || schemaOption.contains("Table")) {
                fetchTables = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Index")) {
                fetchIndexes = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("View")) {
                fetchViews = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Trigger")) {
                fetchTriggers = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Sequence")) {
                fetchSequences = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Synonym")) {
                fetchSynonym = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("DBLinks")) {
                fetchDBLinks = true;
            }

            sourceTables.clear();
            targetTables.clear();
            String tableName;
            DatabaseObjects sourceObjects;
            boolean fetchFromDB = true;
            if (DBDetailHelper.checkIfDBSaved(sourceConnName, sourceDBUser) && wantDynamic.equals("FALSE")) {
                //Fetching schema objects from saved configuration
                fetchFromDB = false;
                System.out.println("Fetching schema objects from saved configuration...");
                sourceObjects = DBDetailHelper.getDBObject(sourceConnName, sourceDBUser);
                if (sourceObjects != null) {
                    if (fetchTables) {
                        List<DBTable> tables = sourceObjects.getTables();
                        if (tables != null && !tables.isEmpty()) {
                            for (DBTable table : tables) {
                                tableName = table.getName();
                                if (!sourceTableListModel.contains("table/" + tableName)) {
                                    sourceTables.add("table/" + tableName);
                                    sourceTableListModel.addElement("table/" + tableName);
                                }
                            }
                            System.out.println("Fetched " + tables.size() + " tables for user " + sourceDBUser);
                            fetchTables = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchViews) {
                        System.out.println("Fetching views");
                        Set<String> views = sourceObjects.getViews();
                        if (views != null && !views.isEmpty()) {
                            for (String s : views) {
                                if (!sourceTableListModel.contains("view/" + s)) {
                                    sourceTables.add("view/" + s);
                                    sourceTableListModel.addElement("view/" + s);
                                }
                            }
                            System.out.println("Fetched " + views.size() + " views for user " + sourceDBUser);
                            fetchViews = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchSequences) {
                        System.out.println("Fetching sequences");
                        Set<String> sequences = sourceObjects.getSequences();
                        if (sequences != null && !sequences.isEmpty()) {
                            for (String s : sequences) {
                                if (!sourceTableListModel.contains("sequence/" + s)) {
                                    sourceTables.add("sequence/" + s);
                                    sourceTableListModel.addElement("sequence/" + s);
                                }
                            }
                            System.out.println("Fetched " + sequences.size() + " sequences for user " + sourceDBUser);
                            fetchSequences = false;
                        } else {
                            System.out.println("will fetch sequences from db");
                            fetchFromDB = true;
                        }
                    }
                    if (fetchSynonym) {
                        System.out.println("Fetching synonyms");
                        Set<String> synonyms = sourceObjects.getSynonyms();
                        if (synonyms != null && !synonyms.isEmpty()) {
                            for (String s : synonyms) {
                                if (!sourceTableListModel.contains("synonym/" + s)) {
                                    sourceTables.add("synonym/" + s);
                                    sourceTableListModel.addElement("synonym/" + s);
                                }
                            }
                            System.out.println("Fetched " + synonyms.size() + " synonyms for user " + sourceDBUser);
                            fetchSynonym = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchIndexes) {
                        System.out.println("Fetching indexes");
                        Set<String> indexes = sourceObjects.getIndexes();
                        if (indexes != null && !indexes.isEmpty()) {
                            for (String s : indexes) {
                                if (!sourceTableListModel.contains("index/" + s)) {
                                    sourceTables.add("index/" + s);
                                    sourceTableListModel.addElement("index/" + s);
                                }
                            }
                            System.out.println("Fetched " + indexes.size() + " indexes for user " + sourceDBUser);
                            fetchIndexes = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchTriggers) {
                        System.out.println("Fetching triggers");
                        Set<String> triggers = sourceObjects.getTriggers();
                        if (triggers != null && !triggers.isEmpty()) {
                            for (String s : triggers) {
                                if (!sourceTableListModel.contains("trigger/" + s)) {
                                    sourceTables.add("trigger/" + s);
                                    sourceTableListModel.addElement("trigger/" + s);
                                }
                            }
                            System.out.println("Fetched " + triggers.size() + " triggers for user " + sourceDBUser);
                            fetchTriggers = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchDBLinks) {
                        System.out.println("Fetching dbLinks");
                        Set<String> dbLinks = sourceObjects.getDbLinks();
                        if (dbLinks != null && !dbLinks.isEmpty()) {
                            for (String s : dbLinks) {
                                if (!sourceTableListModel.contains("dbLink/" + s)) {
                                    sourceTables.add("dbLink/" + s);
                                    sourceTableListModel.addElement("dbLink/" + s);
                                }
                            }
                            System.out.println("Fetched " + dbLinks.size() + " dbLinks for user " + sourceDBUser);
                            fetchDBLinks = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                }
            }
            if (fetchFromDB) {
                System.out.println("Fetching data from DB");
                System.out.println(fetchTables + "," + fetchSynonym + "," + fetchSequences + "," + fetchViews + "," + fetchTriggers + "," + fetchIndexes + "," + fetchDBLinks);
                sourceObjects = DatabaseHelper.fetchSchema(source, sourceDBUser, fetchTables, fetchSynonym, fetchSequences, fetchViews, fetchTriggers, fetchIndexes, fetchDBLinks);
                if (sourceObjects != null) {
                    populateObjects("S", sourceDBUser, sourceObjects, fetchTables, fetchSynonym, fetchSequences, fetchViews, fetchTriggers, fetchIndexes, fetchDBLinks);
                }
            }
            System.out.println("Fetched " + sourceTables.size() + " objects from source db.");

            if (schemaOption.contains("ALL") || schemaOption.contains("Table")) {
                fetchTables = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Index")) {
                fetchIndexes = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("View")) {
                fetchViews = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Trigger")) {
                fetchTriggers = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Sequence")) {
                fetchSequences = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("Synonym")) {
                fetchSynonym = true;
            }
            if (schemaOption.contains("ALL") || schemaOption.contains("DBLinks")) {
                fetchDBLinks = true;
            }

            System.out.println("Populating target table with CLOB data");
            DatabaseObjects targetObjects;
            fetchFromDB = true;

            if (DBDetailHelper.checkIfDBSaved(targetConnName, targetDBUser) && wantDynamic.equals("FALSE")) {
                System.out.println("Fetching schema objects from saved configuration...");
                fetchFromDB = false;
                targetObjects = DBDetailHelper.getDBObject(targetConnName, targetDBUser);
                if (targetObjects != null) {
                    if (fetchTables) {
                        List<DBTable> tables = targetObjects.getTables();
                        if (tables != null && !tables.isEmpty()) {
                            for (DBTable table : tables) {
                                tableName = table.getName();
                                if (!targetTables.contains(tableName)) {
                                    if (!targetTableListModel.contains("table/" + tableName)) {
                                        targetTables.add("table/" + tableName);
                                        targetTableListModel.addElement("table/" + tableName);
                                    }
                                }
                            }
                            System.out.println("Fetched " + tables.size() + " tables for user " + targetDBUser);
                            fetchTables = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchViews) {
                        Set<String> views = targetObjects.getViews();
                        if (views != null && !views.isEmpty()) {
                            for (String s : views) {
                                if (!targetTableListModel.contains("view/" + s)) {
                                    targetTables.add("view/" + s);
                                    targetTableListModel.addElement("view/" + s);
                                }
                            }
                            System.out.println("Fetched " + views.size() + " views for user " + targetDBUser);
                            fetchViews = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchSequences) {
                        Set<String> sequences = targetObjects.getSequences();
                        if (sequences != null && !sequences.isEmpty()) {
                            for (String s : sequences) {
                                if (!targetTableListModel.contains("sequence/" + s)) {
                                    targetTables.add("sequence/" + s);
                                    targetTableListModel.addElement("sequence/" + s);
                                }
                            }
                            System.out.println("Fetched " + sequences.size() + " sequences for user " + targetDBUser);
                            fetchSequences = false;
                        } else {
                            System.out.println("will fetch sequences from db");
                            fetchFromDB = true;
                        }
                    }
                    if (fetchSynonym) {
                        Set<String> synonyms = targetObjects.getSynonyms();
                        if (synonyms != null && !synonyms.isEmpty()) {
                            for (String s : synonyms) {
                                if (!targetTableListModel.contains("synonym/" + s)) {
                                    targetTables.add("synonym/" + s);
                                    targetTableListModel.addElement("synonym/" + s);
                                }
                            }
                            System.out.println("Fetched " + synonyms.size() + " synonyms for user " + targetDBUser);
                            fetchSynonym = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchIndexes) {
                        Set<String> indexes = targetObjects.getIndexes();
                        if (indexes != null && !indexes.isEmpty()) {
                            for (String s : indexes) {
                                if (!targetTableListModel.contains("index/" + s)) {
                                    targetTables.add("index/" + s);
                                    targetTableListModel.addElement("index/" + s);
                                }
                            }
                            System.out.println("Fetched " + indexes.size() + " indexes for user " + targetDBUser);
                            fetchIndexes = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchTriggers) {
                        Set<String> triggers = targetObjects.getTriggers();
                        if (triggers != null && !triggers.isEmpty()) {
                            for (String s : triggers) {
                                if (!targetTableListModel.contains("trigger/" + s)) {
                                    targetTables.add("trigger/" + s);
                                    targetTableListModel.addElement("trigger/" + s);
                                }
                            }
                            System.out.println("Fetched " + triggers.size() + " triggers for user " + targetDBUser);
                            fetchTriggers = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                    if (fetchDBLinks) {
                        Set<String> dbLinks = targetObjects.getDbLinks();
                        if (dbLinks != null && !dbLinks.isEmpty()) {
                            for (String s : dbLinks) {
                                if (!targetTableListModel.contains("dbLink/" + s)) {
                                    targetTables.add("dbLink/" + s);
                                    targetTableListModel.addElement("dbLink/" + s);
                                }
                            }
                            System.out.println("Fetched " + dbLinks.size() + " dbLinks for user " + targetDBUser);
                            fetchDBLinks = false;
                        } else {
                            fetchFromDB = true;
                        }
                    }
                }
            }
            if (fetchFromDB) {
                targetObjects = DatabaseHelper.fetchSchema(target, targetDBUser, fetchTables, fetchSynonym, fetchSequences, fetchViews, fetchTriggers, fetchIndexes, fetchDBLinks);
                if (targetObjects != null) {
                    populateObjects("T", targetDBUser, targetObjects, fetchTables, fetchSynonym, fetchSequences, fetchViews, fetchTriggers, fetchIndexes, fetchDBLinks);
                }
            }
            System.out.println("Fetched " + targetTables.size() + " objects from target db.");

        } catch (JAXBException ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Error Occurred : " + ex.getMessage() + ". ");
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder(ex.toString());
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\n\t at ");
                sb.append(ste);
            }
            String trace = sb.toString();
            //IDELogs.jTextArea1.setText(IDELogs.jTextArea1.getText() + "\n" + trace);
            JOptionPane.showMessageDialog(null, "Error Occurred : " + ex.getMessage() + ". ");
        }

    }

    private void populateObjects(String type, String dbUser, DatabaseObjects sourceObjects, boolean fetchTables, boolean fetchSynonym, boolean fetchSequences, boolean fetchViews, boolean fetchTriggers, boolean fetchIndexes, boolean fetchDBLinks) {
        String tableName;
        if (fetchTables) {
            List<DBTable> tables = sourceObjects.getTables();
            if (tables != null && !tables.isEmpty()) {
                for (DBTable table : tables) {
                    tableName = table.getName();
                    if (type.equals("S")) {
                        if (!sourceTables.contains("table/" + tableName)) {
                            sourceTables.add("table/" + tableName);
                            sourceTableListModel.addElement("table/" + tableName);
                        }
                    } else {
                        if (!targetTables.contains("table/" + tableName)) {
                            targetTables.add("table/" + tableName);
                            targetTableListModel.addElement("table/" + tableName);
                        }
                    }
                }
                System.out.println("Fetched " + tables.size() + " tables for user " + dbUser);
            }
        }
        if (fetchViews) {
            Set<String> views = sourceObjects.getViews();
            if (views != null && !views.isEmpty()) {
                for (String s : views) {
                    if (type.equals("S")) {
                        sourceTables.add("view/" + s);
                        sourceTableListModel.addElement("view/" + s);
                    } else {
                        targetTables.add("view/" + s);
                        targetTableListModel.addElement("view/" + s);
                    }
                }
                System.out.println("Fetched " + views.size() + " views for user " + dbUser);
            }
        }
        if (fetchSequences) {
            Set<String> sequences = sourceObjects.getSequences();
            if (sequences != null && !sequences.isEmpty()) {
                for (String s : sequences) {
                    if (type.equals("S")) {
                        sourceTables.add("sequence/" + s);
                        sourceTableListModel.addElement("sequence/" + s);
                    } else {
                        targetTables.add("sequence/" + s);
                        targetTableListModel.addElement("sequence/" + s);
                    }
                }
                System.out.println("Fetched " + sequences.size() + " sequences for user " + dbUser);
            }
        }
        if (fetchSynonym) {
            Set<String> synonyms = sourceObjects.getSynonyms();
            if (synonyms != null && !synonyms.isEmpty()) {
                for (String s : synonyms) {
                    if (type.equals("S")) {
                        sourceTables.add("synonym/" + s);
                        sourceTableListModel.addElement("synonym/" + s);
                    } else {
                        targetTables.add("synonym/" + s);
                        targetTableListModel.addElement("synonym/" + s);
                    }
                }
                System.out.println("Fetched " + synonyms.size() + " synonyms for user " + dbUser);
            }
        }
        if (fetchIndexes) {
            Set<String> indexes = sourceObjects.getIndexes();
            if (indexes != null && !indexes.isEmpty()) {
                for (String s : indexes) {
                    if (type.equals("S")) {
                        sourceTables.add("index/" + s);
                        sourceTableListModel.addElement("index/" + s);
                    } else {
                        targetTables.add("index/" + s);
                        targetTableListModel.addElement("index/" + s);
                    }
                }
                System.out.println("Fetched " + indexes.size() + " indexes for user " + dbUser);
            }
        }
        if (fetchTriggers) {
            Set<String> triggers = sourceObjects.getTriggers();
            if (triggers != null && !triggers.isEmpty()) {
                for (String s : triggers) {
                    if (type.equals("S")) {
                        sourceTables.add("trigger/" + s);
                        sourceTableListModel.addElement("trigger/" + s);
                    } else {
                        targetTables.add("trigger/" + s);
                        targetTableListModel.addElement("trigger/" + s);
                    }
                }
                System.out.println("Fetched " + triggers.size() + " triggers for user " + dbUser);
            }
        }
        if (fetchDBLinks) {
            Set<String> dbLinks = sourceObjects.getDbLinks();
            if (dbLinks != null && !dbLinks.isEmpty()) {
                for (String s : dbLinks) {
                    if (type.equals("S")) {
                        sourceTables.add("dblink/" + s);
                        sourceTableListModel.addElement("dblink/" + s);
                    } else {
                        targetTables.add("dblink/" + s);
                        targetTableListModel.addElement("dblink/" + s);
                    }
                }
                System.out.println("Fetched " + dbLinks.size() + " dbLinks for user " + dbUser);
            }
        }
    }

    private void settleUsersForConsulta(DBConnection source, String sourceDBUser1, DBConnection target, String targetDBUser1) {
        sourceDBUser = source.getUsername().toUpperCase();
        if (sourceDBUser.startsWith("CONSULTA") && sourceEditedUser == null) {
            if (source.getConnectionName().toUpperCase().contains("PROD") || source.getConnectionName().toUpperCase().contains("PRD1REFWAIT")) {
                System.out.println("Setting username PRD1REFWAIT");
                sourceDBUser = "PRD1REFWAIT";
            } else if (source.getConnectionName().toUpperCase().contains("PRD1CODEOWNB")) {
                System.out.println("Setting username PRD1CODEOWNB");
                sourceDBUser = "PRD1CODEOWNB";
            } else if (source.getConnectionName().toUpperCase().contains("PRD1SEOWNB")) {
                System.out.println("Setting username PRD1SEOWNB");
                sourceDBUser = "PRD1SEOWNB";
            } else if (source.getConnectionName().toUpperCase().contains("PRD1REFOWNB")) {
                System.out.println("Setting username PRD1REFOWNB");
                sourceDBUser = "PRD1REFOWNB";
            } else if (source.getConnectionName().toUpperCase().contains("PRD1PCOWNB")) {
                System.out.println("Setting username PRD1PCOWNB");
                sourceDBUser = "PRD1PCOWNB";
            } else if (source.getConnectionName().toUpperCase().contains("SAREF2_PET")) {
                System.out.println("Setting username SAREF2_PET");
                sourceDBUser = "SAREF2_PET";
            } else if (source.getConnectionName().toUpperCase().contains(" PET")) {
                System.out.println("Setting username PET1REFWAIT");
                sourceDBUser = "PET1REFWAIT";
            } else if (source.getConnectionName().toUpperCase().contains("UAT")) {
                String uatNum = source.getConnectionName().toLowerCase().split("uat")[1].replace("[a-zA-Z]", "");
                System.out.println("Setting username NETAPP" + uatNum);
                sourceDBUser = "NETAPP" + uatNum;
            }
        } else if (sourceEditedUser != null) {
            sourceDBUser = sourceEditedUser;
        }
        targetDBUser = target.getUsername().toUpperCase();
        if (targetDBUser.startsWith("CONSULTA") && targetEditedUser == null) {
            if (target.getConnectionName().toUpperCase().contains("PROD") || target.getConnectionName().toUpperCase().contains("PRD1REFWAIT")) {
                System.out.println("Setting username PRD1REFWAIT");
                targetDBUser = "PRD1REFWAIT";
            } else if (target.getConnectionName().toUpperCase().contains("PRD1CODEOWNB")) {
                System.out.println("Setting username PRD1CODEOWNB");
                targetDBUser = "PRD1CODEOWNB";
            } else if (target.getConnectionName().toUpperCase().contains("PRD1SEOWNB")) {
                System.out.println("Setting username PRD1SEOWNB");
                targetDBUser = "PRD1SEOWNB";
            } else if (target.getConnectionName().toUpperCase().contains("PRD1REFOWNB")) {
                System.out.println("Setting username PRD1REFOWNB");
                targetDBUser = "PRD1REFOWNB";
            } else if (target.getConnectionName().toUpperCase().contains("PRD1PCOWNB")) {
                System.out.println("Setting username PRD1PCOWNB");
                targetDBUser = "PRD1PCOWNB";
            } else if (target.getConnectionName().toUpperCase().contains("SAREF2_PET")) {
                System.out.println("Setting username SAREF2_PET");
                targetDBUser = "SAREF2_PET";
            } else if (target.getConnectionName().toUpperCase().contains(" PET")) {
                System.out.println("Setting username PET1REFWAIT");
                targetDBUser = "PET1REFWAIT";
            } else if (target.getConnectionName().toUpperCase().contains("UAT")) {
                String uatNum = target.getConnectionName().toUpperCase().split("UAT")[1].replace("[a-zA-Z]", "");
                System.out.println("Setting username NETAPP" + uatNum);
                targetDBUser = "NETAPP" + uatNum;
            }
        } else if (targetEditedUser != null) {
            targetDBUser = targetEditedUser;
        }
    }

}
