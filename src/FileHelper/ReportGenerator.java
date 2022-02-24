/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FileHelper;

import dbHelper.DatabaseHelper;
import static gui.ColumnSelectionPage.sourceDBName;
import static gui.ColumnSelectionPage.targetDBName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author rahuldub
 */
public class ReportGenerator {

    ArrayList<String> listOfTablesHavingMismatches = new ArrayList<>();

    public void createHTMLreport(String summaryReportPath, String detailedReportPath, String outputHTMLPathWithFileName, String outputHTMLPath, boolean isFlatDataReport) {
        ReportGenUtils util = new ReportGenUtils();
        util.listOfTablesHavingMismatches = this.listOfTablesHavingMismatches;
        //HTML Summary report generation start
        createHTMLSummaryReport(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, outputHTMLPath, isFlatDataReport);
        //HTML Summary report generation end
        //HTML detailed report generation start
        createHTMLDetailedReport(summaryReportPath, detailedReportPath, outputHTMLPath, isFlatDataReport);
        //HTML detailed report generation end

    }

    public void createHTMLSummaryReport(String summaryReportPath, String detailedReportPath, String outputHTMLPathWithFileName, String outputHTMLPath, boolean isFlatDataReport) {
        try {
            //HTML Summary report generation start
            ReportGenUtils util = new ReportGenUtils();
            util.listOfTablesHavingMismatches = this.listOfTablesHavingMismatches;
            util.createHeaderForSummaryReport(outputHTMLPathWithFileName);
            util.createBodyForSummaryReport(outputHTMLPathWithFileName, summaryReportPath);
            util.createFooterForSummaryReport(outputHTMLPathWithFileName);
            //HTML Summary report generation End
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error occurred while accessing necessary files : " + ex.getMessage());
        }
    }

    private void createHTMLDetailedReport(String summaryReportPath, String detailedReportPath, String outputHTMLPath, boolean isFlatDataReport) {
        ReportGenUtils util = new ReportGenUtils();
        util.listOfTablesHavingMismatches = this.listOfTablesHavingMismatches;
        /*for (String tableName : listOfTablesHavingMismatches)
        {
            String outputHTMLPathWithFileName=outputHTMLPath+"\\"+tableName+".html";
            String arrayOfColumns[]=new ParsingUtils().getArrayOfColumns(tableName, detailedReportPath);
            util.createHeaderForDetailedReportOldFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
            util.createBodyForDetailedReportOldFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
            util.createFooterForDetailedReportOldFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
        }*/
        if (isFlatDataReport) {
            for (String tableName : listOfTablesHavingMismatches) {
                try {
                    String outputHTMLPathWithFileName = outputHTMLPath + "\\" + tableName + "NewFormat.html";
                    String arrayOfColumns[] = new ParsingUtils().getArrayOfColumns(tableName, detailedReportPath);
                    util.createHeaderForDetailedReportNewFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns, outputHTMLPath);
                    util.createBodyForDetailedReportNewFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
                    util.copyJSfile(outputHTMLPath + "tableToExcel.js");
                    util.createFooterForDetailedReportOldFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error occurred while accessing necessary files : " + ex.getMessage());
                }
            }
        } else {
            for (String tableName : listOfTablesHavingMismatches) {
                try {
                    String outputHTMLPathWithFileName = outputHTMLPath + "\\" + tableName + "NewFormat.html";
                    String arrayOfColumns[] = new ParsingUtils().getArrayOfColumns(tableName, detailedReportPath);
                    util.createHeaderForDetailedReportNewFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns, outputHTMLPath);
                    util.createBodyForDetailedReportNewFormatNonFlatData(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
                    util.copyJSfile(outputHTMLPath + "tableToExcel.js");
                    util.createFooterForDetailedReportOldFormat(summaryReportPath, detailedReportPath, outputHTMLPathWithFileName, tableName, arrayOfColumns);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error occurred while accessing necessary files : " + ex.getMessage());
                }
            }
        }

    }
}
