package XMLHelper;

import componentBean.DBConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

/**
 *
 * @author iDEApAD
 */
public class ImportExportConnection {

    String connectionFile = "Connections.xml";
    String connectionFilePath = System.getProperty("user.home") + "//Documents//Jupiter-config";

    public ImportExportConnection() {
        File dir = new File(connectionFilePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                JOptionPane.showMessageDialog(null, "Failed to create directory structure due to system constraints. Please Create a directory: " + connectionFilePath + " and try again...");
            }
        }
        File file = new File(connectionFilePath+"//"+connectionFile);
    }

    public boolean importDBConnection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\"));
        int state = chooser.showDialog(chooser, "Import");
        if (state == JFileChooser.APPROVE_OPTION) {
            String pathToXML = chooser.getSelectedFile().getAbsolutePath();
            return (new XMLHelper.ConnectionHelper().importDBConnection(pathToXML));
        }
        return false;
    }

    public boolean exportDBConnection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int state = chooser.showDialog(chooser, "Export here...");
        if (state == JFileChooser.APPROVE_OPTION) {
            try {
                String sourcePathToXML = connectionFilePath+"//"+connectionFile;
                String targetPathToXML = chooser.getSelectedFile().getAbsolutePath() + "//Jupitor-Connections.xml";
                return performCopy(sourcePathToXML, targetPathToXML);
            } catch (IOException ex) {
                return false;
            }
        }
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
            System.out.println("Error" + ex.getMessage());
        } finally {
            is.close();
            os.close();
        }
        return false;
    }

}
