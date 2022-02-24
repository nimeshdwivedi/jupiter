/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import dbHelper.DataOperation;
import gui.ColumnSelectionPage;
import gui.InProgressWindow;
import gui.SchemaOptionSelection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rahuldub
 */
public class LoadTaskThread implements Runnable {

    ColumnSelectionPage columnSelectionPage;
    SchemaOptionSelection schemaOptionSelection;
    InProgressWindow iPWindow;

    public LoadTaskThread() {

    }

    public LoadTaskThread(ColumnSelectionPage jf, InProgressWindow ip) {
        this.columnSelectionPage = jf;
        this.iPWindow = ip;
    }

    public LoadTaskThread(SchemaOptionSelection jf, InProgressWindow ip) {
        this.schemaOptionSelection = jf;
        this.iPWindow = ip;
    }

    public void run() {

        System.out.println("thread is running...");
        if (columnSelectionPage != null) {
            this.columnSelectionPage.loadComparisonData();
            try {
                iPWindow.nowDispose();
            } catch (InterruptedException ex) {
                System.out.println("The operation has been stopped/interrupted...");
            }
        }else{
            this.schemaOptionSelection.loadSchemaComparison();
            try {
                iPWindow.nowDispose();
            } catch (InterruptedException ex) {
                System.out.println("The operation has been stopped/interrupted...");
            }
        }
    }

    public static void main(String args[]) {
        LoadTaskThread m1 = new LoadTaskThread();
        //Thread t1 = new Thread(m1);
        //t1.start();
    }
}
