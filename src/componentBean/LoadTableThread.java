/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package componentBean;

import gui.HomePage;
import gui.InProgressWindow;
import gui.TableSelectionPage;
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rahuldub
 */
public class LoadTableThread implements Runnable {

    HomePage jFrame;
    InProgressWindow IPWindow;
    java.util.List<componentBean.SessionParameter> sessionParameters;

    public LoadTableThread(HomePage jFrame, java.util.List<componentBean.SessionParameter> sessionParameters) {

    }

    public LoadTableThread() {
    }

    public LoadTableThread(HomePage HomeFrame, List<SessionParameter> sessionParameters, InProgressWindow aThis) {
        this.jFrame = HomeFrame;
        this.sessionParameters = sessionParameters;
        this.IPWindow = aThis;
    }

    public void run() {
        this.jFrame.loadTables(this.IPWindow);
        try {
            IPWindow.nowDispose();
        } catch (InterruptedException ex) {
            System.out.println("The operation has been stopped/interrupted...");
        }
    }

    public static void main(String args[]) {
        LoadTableThread m1 = new LoadTableThread();
        //Thread t1 = new Thread(m1);
        //t1.start();
    }
}
