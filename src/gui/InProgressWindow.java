/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * InProgressWindow.java
 *
 * Created on Jul 3, 2018, 11:33:49 AM
 */
package gui;

import dbHelper.DataOperation;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author rahuldub
 */
public class InProgressWindow extends javax.swing.JFrame {

    JFrame parentHomeWindow;
    Thread thread;

    /**
     * Creates new form InProgressWindow
     */
    public InProgressWindow() {
        initComponents();
    }

    InProgressWindow(HomePage HomeFrame, java.util.List<componentBean.SessionParameter> sessionParameters, int i) {
        this.parentHomeWindow = HomeFrame;
        if (i == 1)//for table load
        {
            componentBean.LoadTableThread m1 = new componentBean.LoadTableThread(HomeFrame, sessionParameters, this);
            thread = new Thread(m1);
            thread.start();
        }
        initComponents();
    }

    InProgressWindow(TableSelectionPage aThis, Object object, int i) {
        this.parentHomeWindow = aThis;

        //jLabel1.setText(jLabel1.getText().replace("tables", "columns").replace("database", "tables"));;
        componentBean.LoadColumnThread m1 = new componentBean.LoadColumnThread(aThis, this);
        thread = new Thread(m1);
        thread.start();
        initComponents();
        this.jLabel1.setText("Initializing columns for the selected tables. Please wait...");
    }

    InProgressWindow(ColumnSelectionPage aThis, Object object, int i) {
        this.parentHomeWindow = aThis;

        //jLabel1.setText(jLabel1.getText().replace("tables", "columns").replace("database", "tables"));;
        componentBean.LoadTaskThread m1 = new componentBean.LoadTaskThread(aThis, this);
        thread = new Thread(m1);
        thread.start();
        initComponents();
        this.jLabel1.setText("Processing data from individual tables. Please wait...");
    }

    InProgressWindow(SchemaOptionSelection aThis, Object object, int i) {
        this.parentHomeWindow = aThis;

        //jLabel1.setText(jLabel1.getText().replace("tables", "columns").replace("database", "tables"));;
        componentBean.LoadTaskThread m1 = new componentBean.LoadTaskThread(aThis, this);
        thread = new Thread(m1);
        thread.start();
        initComponents();
        this.jLabel1.setText("Processing data from individual tables. Please wait...");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Please wait");

        jButton1.setText("Run in Background");
        jButton1.setSelected(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Fetching tables from the selected database. Please wait for a while...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        parentHomeWindow.setState(JFrame.ICONIFIED);
        this.setState(JFrame.ICONIFIED);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InProgressWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    public void nowDispose() throws InterruptedException {

        this.dispose();

    }

}