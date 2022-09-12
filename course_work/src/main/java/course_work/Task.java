package course_work;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "task")
public class Task {
	@Id
	@Column(name = "task_id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")
	private Project project;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	@Column(name = "task")
	private String task;
	@Column(name = "deadline")
	private Date deadline;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return task;
	}
	
	public void setName(String someTask) {
		this.task = someTask;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee someEmployee) {
		this.employee = someEmployee;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public void setDeadline(Date someD) {
		this.deadline = someD;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void addProject(Project p) {
		this.project = p;
	}
	
	public void dataCheck() throws IllegalArgumentException,
	ParseException{
		if (task.equals("")) {
			throw new IllegalArgumentException("Данные проекта отсутствуют!");
		}
		
	}
	
	public boolean equalsName(String info) {
	       return this.getName().equals(info);
	}
	
	public Task(Project someProj) {
		this.project = someProj; 
		someProj.addTask(this);
	}
	
	public Task() {};
}
