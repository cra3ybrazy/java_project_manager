package course_work;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
public class Client extends Human {
	//mappedBy позволяет определить заказчика проекта
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "client", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Project> projects;
	
	public List<Project> getProjects() {return projects; }
	
	public void addProj(Project pr) {
		if (projects == null) projects = new ArrayList<Project>();
		projects.add(pr);
	}
	
	public Project find(int id) throws NullPointerException {
		for (Project pr : projects){
			if (pr.getId() == id) return pr;
		}
        throw new NullPointerException();
    }
	
	//получение последнего Id проекта
	public int lastPrId() {
		return projects.get(projects.size() - 1).getId();
	}
	
	public void delete(int id) {
		for (Project pr : projects) {
			if (pr.getId() == id) {
				projects.remove(pr);
				break;
			}
		}
	}
	
	public Client() {};
}