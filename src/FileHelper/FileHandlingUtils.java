/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FileHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JOptionPane;

/**
 *
 * @author rahuldub
 */
public class FileHandlingUtils {

    BufferedReader getBufferedReaderByFileName(String inputTxtFilePath) throws FileNotFoundException {
        File file = new File(inputTxtFilePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return bufferedReader;
    }

    BufferedWriter getBufferedWriterByFileName(String outputFileName, boolean appendTrueFalse) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFileName, appendTrueFalse);
            bw = new BufferedWriter(fw);
            return bw;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bw;
    }

    boolean writeContentToFile(String filePathAndName, String contentToBeWritten, boolean appendOrNot) {
        BufferedWriter bw = new FileHandlingUtils().getBufferedWriterByFileName(filePathAndName, appendOrNot);
        try {
            bw.write(contentToBeWritten);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        System.out.println("destFile" + destFile.getAbsolutePath());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        FileChannel source = null;
        FileChannel destination = null;
        try {
            fIn = new FileInputStream(sourceFile);
            source = fIn.getChannel();
            fOut = new FileOutputStream(destFile);
            destination = fOut.getChannel();
            long transfered = 0;
            long bytes = source.size();
            while (transfered < bytes) {
                transfered += destination.transferFrom(source, 0, source.size());
                destination.position(transfered);
            }
        } finally {
            if (source != null) {
                source.close();
            } else if (fIn != null) {
                fIn.close();
            }
            if (destination != null) {
                destination.close();
            } else if (fOut != null) {
                fOut.close();
            }
        }
    }

    String createBatFile(String oneAddItem, String tName, int i, String outputFilePathWitheName, String atmpFolderName) {
        String[] arrayHavingSourceAndTargetLocation = oneAddItem.split("#@#");
        String beyondComparePath = returnComparerProgram();
        if (beyondComparePath == null) {
            JOptionPane.showMessageDialog(null, "No Comparer program found to create diff file.");
            System.out.println("Default comparer seems to be missing. Please check the zip package and restore the same in the same JAR parent folder.");
        } else {
            String contentToWrite = "\"" + beyondComparePath + "\" " + arrayHavingSourceAndTargetLocation[0].replaceAll("Source CLOB File - ", "") + " " + arrayHavingSourceAndTargetLocation[1].replaceAll("Target CLOB File - ", "");
            System.out.println("bat file command: " + contentToWrite);
            writeContentToFile(outputFilePathWitheName.replace("//", "/").replaceAll(".html", i + ".bat"), contentToWrite, false);
            //return "<a href=\""+outputFilePathWitheName.replaceAll(".html", i+".bat")+"\">Show in Beyond compare</a>";
        }
        outputFilePathWitheName=outputFilePathWitheName.replace("\\\\", "\\");
        System.out.println("<a href=\"" + outputFilePathWitheName.replaceAll(".html", i + ".bat") + "\" download>Show Differences</a>");
        return "<a href=\"" + outputFilePathWitheName.replaceAll(".html", i + ".bat") + "\" download>Show Differences</a>";
    }

    String createXMLDiffFile(String oneAddItem, String tName, int i, String outputFilePathWitheName, String atmpFolderName) {
        String[] arrayHavingSourceAndTargetLocation = oneAddItem.split("#@#");
        String beyondComparePath = returnComparerProgram();
        Runtime rt = Runtime.getRuntime();
        try {
            if (beyondComparePath == null) {
                JOptionPane.showMessageDialog(null, "No Comparer program found to create diff file.");
                System.out.println("Default comparer seems to be missing. Please check the zip package and restore the same in the same JAR parent folder.");
            } else {
                String command = "\"" + beyondComparePath + "\"  @\"" + outputFilePathWitheName.substring(0, outputFilePathWitheName.indexOf(tName)) + "My Script.txt\" \"" + arrayHavingSourceAndTargetLocation[0].replaceAll("Source CLOB File - ", "") + "\" \"" + arrayHavingSourceAndTargetLocation[1].replaceAll("Target CLOB File - ", "") + "\" \"" + atmpFolderName + "\\" + tName + i + ".html";
                System.out.println("Comparer command:" + command);
                Process p = rt.exec(command);
                Thread.sleep(1000);
                System.out.println("copy file called from there : source : " + atmpFolderName + "\\" + tName + i + ".html");
                System.out.println("copy file called from there : target : " + outputFilePathWitheName.replaceAll(".html", i + ".html"));
                copyFile(new File(atmpFolderName + "\\" + tName + i + ".html"), new File(outputFilePathWitheName.replaceAll(".html", i + ".html")));
                p.destroyForcibly();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error : " + e.getMessage());
        }
        return "";
    }

    private String returnComparerProgram() {
        String beyondComparePath = null;
        //Change 
        if (beyondComparePath == null) {
            File jarPath = new File(FileHandlingUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String deploymentPath = jarPath.getParentFile().getAbsolutePath().replace("%20", " ");
            //String deploymentPath = jarPath.getAbsolutePath();
            String pathToUserGuide = deploymentPath + "\\resource\\Diffinity\\Diffinity.exe";

            File comparerProgram = new File(pathToUserGuide);
            if (comparerProgram.exists()) {
                return comparerProgram.getAbsolutePath();
            } else {
                File f = new File("C:\\Program Files");
                if (f.exists()) {
                    File[] programFiles = f.listFiles();
                    for (File file : programFiles) {
                        if (file.getName().toUpperCase().startsWith("BEYOND COMPARE") && file.isDirectory()) {
                            File[] bCompareFiles = file.listFiles();
                            for (File bcFiles : bCompareFiles) {
                                if (bcFiles.getName().equalsIgnoreCase("BCompare.exe")) {
                                    return bcFiles.getAbsolutePath();
                                }
                            }
                            break;
                        }
                    }
                }
                f = new File("C:\\Program Files (x86)");
                if (f.exists()) {
                    File[] programFiles1 = f.listFiles();
                    for (File file : programFiles1) {
                        if (file.getName().toUpperCase().startsWith("BEYOND COMPARE") && file.isDirectory()) {
                            File[] bCompareFiles = file.listFiles();
                            for (File bcFiles : bCompareFiles) {
                                if (bcFiles.getName().equalsIgnoreCase("BCompare.exe")) {
                                    return bcFiles.getAbsolutePath();
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return beyondComparePath;
    }
}
