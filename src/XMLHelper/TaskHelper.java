/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHelper;

import componentBean.ComparisonTask;
import componentBean.DBReferences;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import xmlBean.Reference;
import xmlBean.TaskList;

/**
 *
 * @author nimeshd
 */
public class TaskHelper {

    String taskFile = "Tasks.xml";
    String taskFilePath = System.getProperty("user.home") + "//Documents//Jupiter-config";

    public TaskHelper() {
        File dir = new File(taskFilePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(taskFilePath + "//" + taskFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String[] getTaskNames() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(taskFilePath + "//" + taskFile);
        if (file.length() != 0) {
            TaskList taskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
            String[] taskNames = new String[taskList.getTasks().size()];
            int i = 0;
            for (ComparisonTask r : taskList.getTasks()) {
                taskNames[i] = r.getName();
                i++;
            }
            Arrays.sort(taskNames);
            return taskNames;
        }
        return null;
    }

    public boolean saveNewTask(ComparisonTask task) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(taskFilePath + "//" + taskFile);
        if (file.length() == 0) {
            TaskList taskList = new TaskList();
            List<ComparisonTask> tasks = new ArrayList<>();
            tasks.add(task);
            taskList.setTasks(tasks);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(taskList, new File(taskFilePath + "//" + taskFile));
            System.out.println("Saved the task");
            return true;
        } else {
            TaskList taskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
            List<ComparisonTask> tasks = taskList.getTasks();
            if (checkIfExistingTask(task, tasks)) {
                JOptionPane.showMessageDialog(null, "A task already exists with this name", "Please provide another name", JOptionPane.ERROR_MESSAGE);
            } else {
                taskList.getTasks().add(task);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(taskList, new File(taskFilePath + "//" + taskFile));
                System.out.println("Saved the task");
                return true;
            }
        }
        return false;
    }

    private boolean checkIfExistingTask(ComparisonTask task, List<ComparisonTask> tasks) {
        for (ComparisonTask t : tasks) {
            if (t.getName().equalsIgnoreCase(task.getName())) {
                return true;
            }
        }
        return false;
    }

    public ComparisonTask getComparisonTask(String taskName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TaskList taskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
        for (ComparisonTask t : taskList.getTasks()) {
            if (t.getName().equalsIgnoreCase(taskName)) {
                return t;
            }
        }
        return null;
    }

    public boolean deleteTask(String taskName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TaskList taskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
        for (ComparisonTask t : taskList.getTasks()) {
            if (t.getName().equalsIgnoreCase(taskName)) {
                taskList.getTasks().remove(t);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(taskList, new File(taskFilePath + "//" + taskFile));
                return true;
            }
        }
        return false;
    }

    public boolean replaceOldTask(ComparisonTask task) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TaskList taskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
        for (ComparisonTask t : taskList.getTasks()) {
            if (t.getName().equalsIgnoreCase(task.getName())) {
                taskList.getTasks().remove(t);
                taskList.getTasks().add(task);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(taskList, new File(taskFilePath + "//" + taskFile));
                return true;
            }
        }
        return false;
    }

    public boolean importTasks(String pathToFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        TaskList existingTaskList = (TaskList) unmarshaller.unmarshal(new File(taskFilePath + "//" + taskFile));
        TaskList importedTaskList = (TaskList) unmarshaller.unmarshal(new File(pathToFile));
        int existingTaskSize = existingTaskList.getTasks().size();
        for (ComparisonTask task : importedTaskList.getTasks()) {
            if (!checkIfExistingTask(task, existingTaskList.getTasks())) {
                System.out.println("Adding task");
                existingTaskList.getTasks().add(task);
            }
        }
        if ((existingTaskList.getTasks().size() - existingTaskSize) > 0) {
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(existingTaskList, new File(taskFilePath + "//" + taskFile));
            return true;
        }
        return false;
    }

}
