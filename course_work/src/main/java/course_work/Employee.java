package course_work;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "employee")
public class Employee extends Human {
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	private List<Task> tasks = new ArrayList<Task>();
	
	public List<Task> getTasks() {return tasks; }
	
	public void addTask(Task someTask) {
		tasks.add(someTask);
	}
}
