/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileHelper;

/**
 *
 * @author nimeshd
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rahuldub
 */
public class MainClass
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            //new ReportGenerator().createHTMLreport("C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\FINAL REPORT - Result_06-07-2018.txt","C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\Result_06-07-2018.txt",  "C:\\Users\\rahuldub\\Desktop\\Report work\\1.html", "C:\\Users\\rahuldub\\Desktop\\Report work\\", true);
            //new ReportGenerator().createHTMLreport("C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\FINAL REPORT - Result_24-07-2018_01_01.txt","C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\Result_24-07-2018_01_01.txt",  "C:\\Users\\rahuldub\\Desktop\\Report work\\1.html", "C:\\Users\\rahuldub\\Desktop\\Report work\\", false);
            //new ReportGenerator().createHTMLreport("C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\FINAL REPORT - Result_26-07-2018_07_38.txt","C:\\Users\\rahuldub\\Desktop\\DBComp-Results\\Result_26-07-2018_07_38.txt",  "C:\\Users\\rahuldub\\Desktop\\Report work\\1.html", "C:\\Users\\rahuldub\\Desktop\\Report work\\", true);
            new ReportGenerator().createHTMLreport("C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\FINAL REPORT - Result_16-08-2018_04_16.txt","C:\\Users\\nimeshd\\Desktop\\DBComp-Results\\Result_16-08-2018_04_16.txt",  "C:\\Users\\nimeshd\\Desktop\\Report work\\1.html", "C:\\Users\\nimeshd\\Desktop\\Report work\\", false);

        }
        catch(Exception e){e.printStackTrace();}
    }

}
