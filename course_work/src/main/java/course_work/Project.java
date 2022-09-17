package course_work;
import javax.persistence.*;

import exceptions.WrongIdException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {
	@Id
	@Column(name = "project_id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
	private List<Task> tasks;
	//cascade указывает тип отношения связанных полей объектов (удалится проект - удалится клиент например)
	//fetch указывает как "вытаскивать" объект (по запросу или всегда)
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
	@JoinColumn(name = "client_id")
	private Client client;
	@Column(name = "project_name")
	private String project;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return project;
	}
	
	public void setName(String someProject) {
		project = someProject;
	}
	
	public String getClientName() {
		return client.getName();
	}
	
	public String getClientLastName() {
		return client.getLastName();
	}
	
	public void addTask(Task someTask) {
		if (tasks == null) tasks = new ArrayList<Task>();
		tasks.add(someTask);
	}
	
	public Client getC() {
		return this.client;
	}
	
	public List<Task> getTasks() {return tasks; }
	
	public boolean equalsProjects(String info) {
	       return this.getName().equals(info);
	}
	
	public void dataCheck() throws IllegalArgumentException, 
	WrongIdException{
		if (project.equals("")) {
			throw new IllegalArgumentException("Данные проекта отсутствуют!");
		}
		/*if (getId() % 10 == 0) {
			throw new WrongIdException("Максимальное число проектов превышено!");
		}*/
	}
	
	public boolean equalsName(String info) {
	       return this.getName().equals(info);
	}
	
	public void addClient(Client someC) {
		this.client = someC;
	}
	
	public Project() {};
	
	public Project(Client someC) 
	{
		this.client = someC;
	};
}
