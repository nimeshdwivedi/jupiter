/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package componentBean;

import gui.InProgressWindow;
import gui.TableSelectionPage;

/**
 *
 * @author rahuldub
 */
public class LoadColumnThread implements Runnable
{
    TableSelectionPage aTableSelectionPage;
    InProgressWindow iPWindow;
    public LoadColumnThread()
    {

    }
    public LoadColumnThread(TableSelectionPage jf, InProgressWindow ip)
    {
        this.aTableSelectionPage=jf;
        this.iPWindow=ip;
        
    }
public void run()
{
System.out.println("thread is running...");
this.aTableSelectionPage.loadColumns();
        try {
            iPWindow.nowDispose();
        } catch (InterruptedException ex) {
            System.out.println("The operation has been stopped/interrupted...");
        }
}

public static void main(String args[]){
LoadColumnThread m1=new LoadColumnThread();
//Thread t1 =new Thread(m1);
//t1.start();
 }
}
