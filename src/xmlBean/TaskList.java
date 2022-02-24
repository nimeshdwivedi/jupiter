/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmlBean;

import componentBean.ComparisonTask;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nimeshd
 */
@XmlRootElement(name = "TaskList")
public class TaskList {
    
    private List<ComparisonTask> tasks = null;

    public void setTasks(List<ComparisonTask> tasks) {
        this.tasks = tasks;
    }

    @XmlElement(name = "Task")
    public List<ComparisonTask> getTasks() {
        return tasks;
    }
    
}
