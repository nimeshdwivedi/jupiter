/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author rahuldub
 */
public class ReportGenUtils {

    ArrayList<String> listOfTablesHavingMismatches = new ArrayList<>();

    String headerHavingColumnsNewFormat = "";
    String deploymentPath, filesPath, resourcesPath;

    public ReportGenUtils() {
        File jarPath = new File(FileHandlingUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        this.deploymentPath = jarPath.getAbsolutePath();
        //this.deploymentPath = jarPath.getParentFile().getAbsolutePath().replace("%20", " ");
        this.filesPath = deploymentPath + "\\files\\";
        this.resourcesPath = deploymentPath + "\\resource\\";
    }

    void createHeaderForSummaryReport(String outputFileName) throws FileNotFoundException, IOException {
        String content = getHeaderContentForSummaryReport();
        new FileHandlingUtils().writeContentToFile(outputFileName, content, false);//False because it is header.
    }

    String getHeaderContentForSummaryReport() {
        BufferedReader reader = null;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            //URL url = ReportGenUtils.class.getResource("/files/header.txt");
            //File f = new File(url.getFile());
            File f = new File(filesPath + "header.txt");
            reader = new FileHandlingUtils().getBufferedReaderByFileName(f.getAbsolutePath());
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ReportGenUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return stringBuffer.toString();
    }

    void createBodyForSummaryReport(String outputFileName, String txtFilePath) throws FileNotFoundException, IOException {
        String content = getBodyContentForSummaryReport(txtFilePath);
        new FileHandlingUtils().writeContentToFile(outputFileName, content, true);//True because needs to be appended.
    }

    String getBodyContentForSummaryReport(String txtSummaryReportLocation) {
        BufferedReader reader = null;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            reader = new FileHandlingUtils().getBufferedReaderByFileName(txtSummaryReportLocation);
            String line;
            boolean startReadingNow = false;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.contains("The comparison failed")) {
                        break;
                    }
                    if (startReadingNow && line.length() > 4) {

                        String[] arrayOfContent = line.split(Pattern.quote("|"));

                        stringBuffer.append("<tr style=\"border: double; border-color: #000000\">");
                        stringBuffer.append("\n");
                        //stringBuffer.append("<td style=\"width: 100px; height: 45px;\">&nbsp;<a href=\"./").append(arrayOfContent[0].trim()).append(">").append(arrayOfContent[0].trim()).append("</a></td>");stringBuffer.append(arrayOfContent[0].trim());stringBuffer.append("</td>");
                        stringBuffer.append("<td scope=\"col\" width=\"35%\">&nbsp; <a href=\"./");
                        stringBuffer.append(arrayOfContent[0].trim()).append("NewFormat.html\">");
                        stringBuffer.append(arrayOfContent[0].trim());
                        stringBuffer.append("</a></td></td>\n");
                        stringBuffer.append("\n");

                        stringBuffer.append("<td scope=\"col\" width=\"15%\">&nbsp;");
                        stringBuffer.append(arrayOfContent[1].trim());
                        stringBuffer.append("</td>\n");

                        stringBuffer.append("<td scope=\"col\" width=\"15%\">&nbsp;");
                        stringBuffer.append(arrayOfContent[2].trim());
                        stringBuffer.append("</td>\n");
                        stringBuffer.append("<td scope=\"col\" width=\"15%\">&nbsp;");
                        stringBuffer.append(arrayOfContent[3].trim());
                        stringBuffer.append("</td>\n");
                        stringBuffer.append("<td scope=\"col\" width=\"20%\">&nbsp;");
                        stringBuffer.append(arrayOfContent[4].trim());
                        stringBuffer.append("</td>\n");

                        stringBuffer.append("</tr>\n");
                        listOfTablesHavingMismatches.add(arrayOfContent[0].trim());
                    }
                    if (line.contains("TableName")) {
                        startReadingNow = true;
                    }

                } catch (Exception e) {
                    System.out.println("Error occurred in Line: " + line + "\n " + e.getMessage());
                }
            }

            BufferedReader reader1 = new FileHandlingUtils().getBufferedReaderByFileName(txtSummaryReportLocation);
            String line1;
            boolean startReadingNow1 = false;

            while ((line1 = reader1.readLine()) != null) {
                if (line1.contains("The comparison failed")) {

                    startReadingNow1 = true;
                    stringBuffer.append("</tbody>\n" + "  </table>");
                    stringBuffer.append("<!-- Hero Section -->\n"
                            + "  <section class=\"hero\" id=\"hero\">\n"
                            + "    <h4 class=\"hero_header\"> -- </h4>\n"
                            + "</section>\n"
                            + "<table width=\"95%\" style=\"margin-left: 40px; margin-right: 40px\">\n"
                            + "	      <tr style=\"border: double; border-color: #000000\" >\n"
                            + "        <th scope=\"col\" colspan = \"2\" style=\"background-color: #CFCFCF\">Comparison failed for following tables. Please check these separately</th>\n"
                            + "	      </tr>\n"
                            + "<tr style=\"border: double; border-color: #000000\">\n"
                            + "        <th scope=\"col\" width=\"35%\" style=\"background-color: #CFCFCF\">Table Name</th>\n"
                            + "        <th scope=\"col\" width=\"65%\" style=\"background-color: #CFCFCF\">Error Message</th>\n"
                            + "	      </tr>\n"
                            + "<tr style=\"border: double; border-color: #000000\">\n"
                            + "<td scope=\"col\" width=\"15%\">&nbsp;\n"
                            + "	<tr style=\"border: double; border-color: #000000\">\n");
                }
                if (startReadingNow1 && line1.length() > 4 && line1.contains("with error:")) {
                    stringBuffer.append("<td scope=\"col	\" width=\"35%\">&nbsp;");
                    stringBuffer.append(line1.substring(0, line1.indexOf("with error:")));
                    stringBuffer.append("</td>");
                    //<td scope="col	" width="65%">&nbsp; Error message </td>
                    stringBuffer.append("<td scope=\"col	\" width=\"65%\">&nbsp;");
                    stringBuffer.append(line1.substring(line1.indexOf("with error:") + 11));
                    stringBuffer.append("</td>");
                    stringBuffer.append("</td></tr>");

                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReportGenUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ReportGenUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Body Part : " + stringBuffer.toString());
        return stringBuffer.toString();
    }

    void createFooterForSummaryReport(String outputFileName) throws FileNotFoundException, IOException {
        String content = getFooterContentForSummaryReport();
        new FileHandlingUtils().writeContentToFile(outputFileName, content, true);//True because needs to be appended.
    }

    String getFooterContentForSummaryReport() throws FileNotFoundException, IOException {
        //URL url = ReportGenUtils.class.getResource("/files/footer.txt");
        //File f = new File(url.getFile());
        File f = new File(filesPath + "footer.txt");
        BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(f.getAbsolutePath());
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    void createHeaderForDetailedReportOldFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[]) throws FileNotFoundException, IOException {
        String content = getHeaderContentForDetailedReportOldFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, false);//False because it is header.
    }

    String getHeaderContentForDetailedReportOldFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        //URL url = ReportGenUtils.class.getResource("/files/headerDetailedReport.txt");
        //File f = new File(url.getFile());
        File f = new File(filesPath + "headerDetailedReport.txt");
        BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(f.getAbsolutePath());
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(tableNm));
            line = line.replaceAll("tableNameComesHere", tableNm);
            if (line.contains(tableNm));
            line = line.replaceAll("numberOfColumnsComesHere", String.valueOf(arrayCol.length));
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        stringBuffer.append("<tr>");
        String columnTagReference = "\n<td>&nbsp;ColumnNameComesHere</td>";

        for (int i = 0; i < 2; i++) {
            for (String colName : arrayCol) {
                stringBuffer.append(columnTagReference.replaceAll("ColumnNameComesHere", colName));
            }
        }

        stringBuffer.append("\n</tr>\n");
        return stringBuffer.toString();
    }

    void createBodyForDetailedReportOldFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[]) throws FileNotFoundException, IOException {
        String content = getBodyContentForDetailedReportOldFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, true);//True as needs to be appended here.
    }

    String getBodyContentForDetailedReportOldFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        String[] arrayHavingAllLinesInDetailedReport = new ParsingUtils().getAllLines(tableNm, fileToRead);
        for (int i = 0; i < arrayHavingAllLinesInDetailedReport.length; i++) {
            //System.out.println("arrayHavingAllLinesInDetailedReport[i]"+arrayHavingAllLinesInDetailedReport[i]);
            arrayHavingAllLinesInDetailedReport[i] = arrayHavingAllLinesInDetailedReport[i].replaceAll("null", "");
            arrayHavingAllLinesInDetailedReport[i] = new String(arrayHavingAllLinesInDetailedReport[i].getBytes("ISO-8859-1"), "UTF-8");
            //System.out.println("arrayHavingAllLinesInDetailedReport[i]"+arrayHavingAllLinesInDetailedReport[i]);
        }

        String[] ArrayHavingAllLinesOfThisTable = new ParsingUtils().getAllLinesByTableName(tableNm, arrayHavingAllLinesInDetailedReport);
        ArrayList<ArrayList<String>> list = new ArrayProcessHelper().getMissInCount(ArrayHavingAllLinesOfThisTable, tableNm);
        StringBuilder stringBuffer = new StringBuilder();

        for (String str : list.get(0))//additional in source
        {
            stringBuffer.append("<tr>\n");
            String[] arrayOfAdditionalInSource = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInSource) {
                stringBuffer.append("<td>\n");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("\n</td>\n");
            }
            for (String oneAddItem : arrayOfAdditionalInSource) {
                stringBuffer.append("<td>\n");
                stringBuffer.append("");
                stringBuffer.append("</td>\n");
            }
            stringBuffer.append("\n</tr>");
        }

        for (String str : list.get(1))//additional in target
        {
            stringBuffer.append("\n<tr>\n");
            String[] arrayOfAdditionalInTarget = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInTarget) {
                stringBuffer.append("<td>");
                stringBuffer.append("");
                stringBuffer.append("</td>");
            }
            for (String oneAddItem : arrayOfAdditionalInTarget) {
                stringBuffer.append("<td>");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("</td>");
            }
            stringBuffer.append("\n</tr>\n");
        }
        ArrayList<ArrayList<String>> list1 = new ArrayProcessHelper().getMismatchList(ArrayHavingAllLinesOfThisTable, tableNm);
        {
            //This is array of String data with pipe delimiter of source table
            String[] sourceData = list1.get(0).toArray(new String[list1.size()]);
            //This is array of String data with pipe delimiter of Target table
            String[] TargetData = list1.get(1).toArray(new String[list1.size()]);
            if (sourceData.length > 0) {
                for (int i = 0; i < sourceData.length; i++) {
                    if (sourceData[i] != null) {
                        String[] actualSourceDataArray = sourceData[i].split(" \\| ");
                        String[] actualTargetDataArray = TargetData[i].split(" \\| ");
                        stringBuffer.append("\n<tr>\n");
                        for (int j = 0; j < actualSourceDataArray.length; j++) {
                            if (!actualSourceDataArray[j].equals(actualTargetDataArray[j])) {
                                actualSourceDataArray[j] = "\n<td style=\"background-color: pink;\">" + actualSourceDataArray[j] + "</td>\n";
                                actualTargetDataArray[j] = "\n<td style=\"background-color: pink;\">" + actualTargetDataArray[j] + "</td>\n";
                                stringBuffer.append(actualSourceDataArray[j]);
                            } else {
                                actualSourceDataArray[j] = "\n<td>" + actualSourceDataArray[j] + "</td>\n";//  "\n<td style=\"background-color: pink;\">"+actualSourceDataArray[i]+"</td>\n";
                                actualTargetDataArray[j] = "\n<td>" + actualTargetDataArray[j] + "</td>\n";
                                stringBuffer.append(actualSourceDataArray[j]);
                            }
                        }
                        for (String actualTargetDataArray1 : actualTargetDataArray) {
                            stringBuffer.append(actualTargetDataArray1);
                        }
                        stringBuffer.append("\n<tr>\n");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }

    void createFooterForDetailedReportOldFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[]) throws FileNotFoundException, IOException {
        String content = getFooterContentForDetailedReportOldFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, true);//True because it is footer.
    }

    String getFooterContentForDetailedReportOldFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        //URL url = ReportGenUtils.class.getResource("/files/footerDetailedReport.txt");
        //File f = new File(url.getFile());
        File f = new File(filesPath + "footerDetailedReport.txt");
        BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(f.getAbsolutePath());
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    void createHeaderForDetailedReportNewFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[], String outputFilePath) throws FileNotFoundException, IOException {
        String content = getHeaderContentForDetailedReportNewFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, false);//False because it is header.

        //new FileHandlingUtils().copyFile(new File(ReportGenUtils.class.getResource("/files/MyScript.txt").getFile()), new File(outputFilePath + "/My Script.txt"));
        FileHandlingUtils.copyFile(new File(filesPath + "MyScript.txt"), new File(outputFilePath + "/My Script.txt"));
        System.out.println("source ka path : " + new File(ReportGenUtils.class.getResource("/files/MyScript.txt").getFile()).getAbsolutePath());
        System.out.println("dest ka path : " + outputHTMLPath + "/My Script.txt");
    }

    String getHeaderContentForDetailedReportNewFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        headerHavingColumnsNewFormat = "<tr>";
        //URL url = ReportGenUtils.class.getResource("/files/headerDetailedReportNewFormat.txt");
        //File f = new File(url.getFile());
        File f = new File(filesPath + "headerDetailedReportNewFormat.txt");
        BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(f.getAbsolutePath());
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("tableNameComesHere"));
            line = line.replaceAll("tableNameComesHere", tableNm);
            stringBuffer.append(line).append("\n");
        }
        String columnTagReference = "<th scope=\"col\" width=\"auto\" style=\"border: double; border-color: #000000; background-color: #CFCFCF\">columnName</th>";//"
        String columnHeaderTagReference = "<th scope=\"col\" width=\"auto\" style=\"border: double; border-color: #000000; background-color: #CFCFCF\">columnName</th>";//"background-color: yellow;"
        for (String colName : arrayCol) {
            //stringBuffer.append(columnHeaderTagReference.replaceAll("columnName", colName)+"\n");
            headerHavingColumnsNewFormat = headerHavingColumnsNewFormat + columnHeaderTagReference.replaceAll("columnName", colName) + "\n";
        }
        headerHavingColumnsNewFormat = headerHavingColumnsNewFormat + "</tr>";
        stringBuffer.append("</tr>");
        //System.out.println("stringBuffer.toString()"+stringBuffer.toString());
        return stringBuffer.toString();
    }

    void createBodyForDetailedReportNewFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[]) throws FileNotFoundException, IOException {
        String content = getBodyContentForDetailedReportNewFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, true);//True as needs to be appended here.
    }

    String getBodyContentForDetailedReportNewFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        String[] arrayHavingAllLinesInDetailedReport = new ParsingUtils().getAllLines(tableNm, fileToRead);
        for (int i = 0; i < arrayHavingAllLinesInDetailedReport.length; i++) {
            arrayHavingAllLinesInDetailedReport[i] = new String(arrayHavingAllLinesInDetailedReport[i].getBytes("ISO-8859-1"), "UTF-8");
        }
        String[] ArrayHavingAllLinesOfThisTable = new ParsingUtils().getAllLinesByTableName(tableNm, arrayHavingAllLinesInDetailedReport);
        ArrayList<ArrayList<String>> list = new ArrayProcessHelper().getMissInCount(ArrayHavingAllLinesOfThisTable, tableNm);
        StringBuilder stringBuffer = new StringBuilder();

        ArrayList<ArrayList<String>> list1 = new ArrayProcessHelper().getMismatchList(ArrayHavingAllLinesOfThisTable, tableNm);
        {
            //This is array of String data with pipe delimiter of source table
            String[] sourceData = list1.get(0).toArray(new String[list1.size()]);
            //This is array of String data with pipe delimiter of Target table
            String[] TargetData = list1.get(1).toArray(new String[list1.size()]);
            if (sourceData.length > 0) {
                for (int i = 0; i < sourceData.length; i++) {
                    if (sourceData[i] != null) {
                        if (i == 0) {
                            stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length).append("\"><h2>Mismatches Between Source and Target</h2></td>\n</tr>\n");
                            stringBuffer.append(headerHavingColumnsNewFormat);
                            //System.out.println("stringBuffer.append(headerHavingColumnsNewFormat"+headerHavingColumnsNewFormat);
                        }
                        String[] actualSourceDataArray = sourceData[i].split(" \\| ");
                        String[] actualTargetDataArray = TargetData[i].split(" \\| ");

                        stringBuffer.append("\n<tr>\n");
                        for (int j = 0; j < actualSourceDataArray.length; j++) {
                            if (!actualSourceDataArray[j].equals(actualTargetDataArray[j])) {
                                actualSourceDataArray[j] = "\n<td style=\"background-color: pink; \"> Source : " + actualSourceDataArray[j];
                                //actualTargetDataArray[j]="\n<td style=\"background-color: pink;\">"+actualTargetDataArray[j]+"</td>\n";
                                stringBuffer.append(actualSourceDataArray[j]);
                                stringBuffer.append("<br//> Target : ");
                                stringBuffer.append(actualTargetDataArray[j]);
                                stringBuffer.append("</td>\n");
                            } else {
                                if (actualSourceDataArray[j].equalsIgnoreCase("null")) {
                                    actualSourceDataArray[j] = "";
                                }

                                if (actualTargetDataArray[j].equalsIgnoreCase("null")) {
                                    actualTargetDataArray[j] = "";
                                }

                                actualSourceDataArray[j] = "\n<td>" + actualSourceDataArray[j] + "</td>\n";//  "\n<td style=\"background-color: pink;\">"+actualSourceDataArray[i]+"</td>\n";
                                actualTargetDataArray[j] = "\n<td>" + actualTargetDataArray[j] + "</td>\n";
                                stringBuffer.append(actualSourceDataArray[j]);
                            }
                        }
                    }
                }
            }
        }

        for (String str : list.get(0))//additional in source
        {
            if (str.equals(list.get(0).get(0))) {
                stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length).append("\"><h2>Additional In Source</h2></td>\n</tr>\n");
                stringBuffer.append(headerHavingColumnsNewFormat);
                //System.out.println("header content"+headerHavingColumnsNewFormat);
            }

            stringBuffer.append("<tr>\n");
            String[] arrayOfAdditionalInSource = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInSource) {
                stringBuffer.append("<td style=\"text-align: left; vertical-align: middle;\">\n");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("\n</td>\n");
            }
            stringBuffer.append("\n</tr>");
        }

        for (String str : list.get(1))//additional in target
        {
            if (str.equals(list.get(1).get(0))) {
                stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length).append("\"><h2>Additional In Target</h2></td>\n</tr>\n");
                stringBuffer.append(headerHavingColumnsNewFormat);
            }

            stringBuffer.append("<tr>\n");
            String[] arrayOfAdditionalInTarget = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInTarget) {
                stringBuffer.append("<td style=\"text-align: left; vertical-align: middle;\">\n");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("\n</td>\n");
            }
            stringBuffer.append("\n</tr>");
        }

        //it was here before ... 
        return stringBuffer.toString();
    }

    void createFooterForDetailedReportNewFormat(String summaryReportPath, String detailedReportPath, String outputHTMLPath, String table, String columnArray[]) throws FileNotFoundException, IOException {
        String content = getFooterContentForDetailedReportNewFormat(table, detailedReportPath, columnArray);
        new FileHandlingUtils().writeContentToFile(outputHTMLPath, content, true);//True because it is footer.
    }

    String getFooterContentForDetailedReportNewFormat(String tableNm, String fileToRead, String arrayCol[]) throws FileNotFoundException, IOException {
        //BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(ReportGenUtils.class.getResource("/files/footerDetailedReportNewFormat.txt").getPath());
        BufferedReader reader = new FileHandlingUtils().getBufferedReaderByFileName(filesPath + "footerDetailedReportNewFormat.txt");
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    void copyJSfile(String targetFileName) throws IOException {
        //URL url = ReportGenUtils.class.getResource("/files/tableToExcel.js");
        //File f = new File(url.getFile());
        File f = new File(filesPath + "tableToExcel.js");

        FileHandlingUtils.copyFile(f, new File(targetFileName));

    }

    void createBodyForDetailedReportNewFormatNonFlatData(String summaryReportPath, String detailedReportPath, String outputHTMLPathWithFileName, String tableName, String[] arrayOfColumns) throws FileNotFoundException, IOException {
        System.out.println("p# 3");
        String content = getBodyContentForDetailedReportNewFormatNonFlatData(tableName, detailedReportPath, arrayOfColumns, outputHTMLPathWithFileName);
        new FileHandlingUtils().writeContentToFile(outputHTMLPathWithFileName, content, true);//True as needs to be appended here.

    }

    String getBodyContentForDetailedReportNewFormatNonFlatData(String tableNm, String fileToRead, String arrayCol[], String outputHTMLPathWithFileName) throws FileNotFoundException, IOException {
        System.out.println("P# 2");

        String[] arrayHavingAllLinesInDetailedReport = new ParsingUtils().getAllLines(tableNm, fileToRead);
        for (int i = 0; i < arrayHavingAllLinesInDetailedReport.length; i++) {
            arrayHavingAllLinesInDetailedReport[i] = new String(arrayHavingAllLinesInDetailedReport[i].getBytes("ISO-8859-1"), "UTF-8");
        }
        String[] ArrayHavingAllLinesOfThisTable = new ParsingUtils().getAllLinesByTableName(tableNm, arrayHavingAllLinesInDetailedReport);
        ArrayList<ArrayList<String>> list = new ArrayProcessHelper().getMissInCount(ArrayHavingAllLinesOfThisTable, tableNm);
        StringBuilder stringBuffer = new StringBuilder();
        ArrayList<String> list1 = new ArrayProcessHelper().getMismatchListForNonFlatData(ArrayHavingAllLinesOfThisTable, tableNm);
        {
            if (list1.size() > 0) {
                String tmpFolderName = "";
                for (int i = 0; i < list1.size(); i++) {

                    if (list1.get(i) != null) {
                        if (i == 0) {
                            stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length + 1).append("\"><h2>Mismatches Between Source and Target</h2></td>\n</tr>\n");
                            //stringBuffer.append(headerHavingColumnsNewFormat);
                            stringBuffer.append(headerHavingColumnsNewFormat.replaceAll("\">" + arrayCol[arrayCol.length - 1] + "</td>\n</tr>", "\" colspan=\"2\">" + arrayCol[arrayCol.length - 1] + "</td>\n</tr>"));
                            //String newHeader=headerHavingColumnsNewFormat.replaceAll("\">"+arrayCol[arrayCol.length-1]+"</td>\n</tr>", "\" colspan=\"2\">"+arrayCol[arrayCol.length-1]+"</td>\n</tr>");
                            //System.out.println("headerHavingColumnsNewFormat "+ newHeader);
                            tmpFolderName = "C:\\" + new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date()).replaceAll(":", "").replaceAll("-", "").replaceAll(" ", "");
                            //System.out.println("folder name"+tmpFolderName+new File(tmpFolderName).mkdir());

                        }
                        stringBuffer.append("<tr>\n");
                        String[] arrayOfMismatchdata = list1.get(i).split(" \\| ");
                        for (String oneAddItem : arrayOfMismatchdata) {
                            stringBuffer.append("<td style=\"text-align: left; vertical-align: middle;\">\n");
                            if (oneAddItem.equalsIgnoreCase("null")) {
                                oneAddItem = "";
                            }
                            if (oneAddItem.contains("#@#")) {
                                //new FileHandlingUtils().createXMLDiffFile(oneAddItem, tableNm, i, outputHTMLPathWithFileName, tmpFolderName);
                                oneAddItem = new FileHandlingUtils().createBatFile(oneAddItem, tableNm, i, outputHTMLPathWithFileName, tmpFolderName);
                                //new FileHandlingUtils().createXMLDiffFile(oneAddItem, tableNm, i, outputHTMLPathWithFileName);
                                String a = outputHTMLPathWithFileName.replaceAll(".html", i + ".html");
                                System.out.println("a: " + a);
                                String s = "";
                                //String s = "<a href=\"" + a + "\">HTML view</a>";
                                oneAddItem = oneAddItem + "\n</td>\n<td>" + s;
                            }

                            stringBuffer.append(oneAddItem);
                            stringBuffer.append("\n</td>\n");
                        }
                        stringBuffer.append("\n</tr>");

                    }
                }
            }
        }

        for (String str : list.get(0))//additional in source
        {
            if (str.equals(list.get(0).get(0))) {
                stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length).append("\"><h2>Additional In Source</h2></td>\n</tr>\n");
                stringBuffer.append(headerHavingColumnsNewFormat);
                //System.out.println("header content"+headerHavingColumnsNewFormat);
            }

            stringBuffer.append("<tr>\n");
            String[] arrayOfAdditionalInSource = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInSource) {
                stringBuffer.append("<td style=\"text-align: left; vertical-align: middle;\">\n");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                } //else if(oneAddItem.contains("Files:"))
                /*else if (oneAddItem.contains("Files:")) {
                    //System.out.println("path : "+oneAddItem.substring(oneAddItem.indexOf("Files:")+7, oneAddItem.length()-1));
                    System.out.println("path : " + oneAddItem.substring(oneAddItem.indexOf("File:") + 6, oneAddItem.length()));
                }*/ else if (oneAddItem.contains("Files:")) {
                    String link = "<a href=\"" + oneAddItem.substring(oneAddItem.indexOf("Files:") + 6, oneAddItem.length()) + "\">View data</a>";
                    //String link="<a href=\""+oneAddItem.substring(oneAddItem.indexOf("File:")+6, oneAddItem.length())+"\">View data</a>";
                    System.out.println("Link : " + link);
                    oneAddItem = link;
                    //<a href="https://www.w3schools.com">Visit W3Schools.com!</a>
                    //System.out.println("path : "+oneAddItem.substring(oneAddItem.indexOf("File:")+6, oneAddItem.length())); 
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("\n</td>\n");
            }
            stringBuffer.append("\n</tr>");
        }

        for (String str : list.get(1))//additional in target
        {
            System.out.println("p# 1");
            if (str.equals(list.get(1).get(0))) {
                stringBuffer.append("<tr>\n<td style=\"vertical-align: middle;\" colspan=\"").append(arrayCol.length).append("\"><h2>Additional In Target</h2></td>\n</tr>\n");
                stringBuffer.append(headerHavingColumnsNewFormat);
            }

            stringBuffer.append("<tr>\n");
            String[] arrayOfAdditionalInTarget = str.split(" \\| ");
            for (String oneAddItem : arrayOfAdditionalInTarget) {
                stringBuffer.append("<td style=\"text-align: left; vertical-align: middle;\">\n");
                if (oneAddItem.equalsIgnoreCase("null")) {
                    oneAddItem = "";
                } //else if(oneAddItem.contains("File:"))
                else if (oneAddItem.contains("Files:")) {
                    String link = "<a href=\"" + oneAddItem.substring(oneAddItem.indexOf("Files:") + 6, oneAddItem.length()) + "\">View data</a>";
                    //String link="<a href=\""+oneAddItem.substring(oneAddItem.indexOf("File:")+6, oneAddItem.length())+"\">View data</a>";
                    System.out.println("Link : " + link);
                    oneAddItem = link;
                    //<a href="https://www.w3schools.com">Visit W3Schools.com!</a>
                    //System.out.println("path : "+oneAddItem.substring(oneAddItem.indexOf("File:")+6, oneAddItem.length()));
                }
                stringBuffer.append(oneAddItem);
                stringBuffer.append("\n</td>\n");
            }
            stringBuffer.append("\n</tr>");
        }

        //it was here before ...
        return stringBuffer.toString();
    }
}
