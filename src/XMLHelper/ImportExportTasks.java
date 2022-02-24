package XMLHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

/**
 *
 * @author iDEApAD
 */
public class ImportExportTasks {

    String taskFile = "Tasks.xml";
    String taskFilePath = System.getProperty("user.home") + "//Documents//Jupiter-config";

    public ImportExportTasks() {
        File dir = new File(taskFilePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(taskFilePath+"//"+taskFile);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE.SEVERE, null, ex);
            }
        }
    }
    
    

    public boolean importTasks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\"));
        int state = chooser.showDialog(chooser, "Import");
        if (state == JFileChooser.APPROVE_OPTION) {
            try {
                String pathToXML = chooser.getSelectedFile().getAbsolutePath();
                System.out.println("Path To XML"+ pathToXML);
                return (new XMLHelper.TaskHelper().importTasks(pathToXML));
            } catch (JAXBException ex) {
                System.out.println("");
                return false;
            }
        }
        JOptionPane.showMessageDialog(null, "Import action cancelled");
        return false;
    }

    public boolean exportTasks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int state = chooser.showDialog(chooser, "Export here...");
        if (state == JFileChooser.APPROVE_OPTION) {
            try {
                String sourcePathToXML = taskFilePath+"//"+taskFile;
                String targetPathToXML = chooser.getSelectedFile().getAbsolutePath() + "\\Jupitor - ExportedTasks.xml";
                System.out.println("Source"+sourcePathToXML);
                System.out.println("Target"+targetPathToXML);
                return performCopy(sourcePathToXML, targetPathToXML);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "IO Error occurred while exporting the task");
                return false;
            }
        }
        JOptionPane.showMessageDialog(null, "Import action cancelled");
        return false;
    }

    private boolean performCopy(String sourcePathToXML, String targetPathToXML) throws FileNotFoundException, IOException {
        File sourceFile = new File(sourcePathToXML);
        File targetFile = new File(targetPathToXML);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            is.close();
            os.close();
        }
    }

}
