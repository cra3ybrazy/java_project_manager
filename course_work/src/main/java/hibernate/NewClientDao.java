package hibernate;
import java.util.List;
import javax.persistence.*;
import course_work.*;

public class NewClientDao {
	private EntityManager em;
	
	public NewClientDao() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
		em = factory.createEntityManager();
	}
	
	@SuppressWarnings("unchecked")
	public List<Client> findClients() {
		//делаем запрос и достаем данные из таблиц
		return em.createQuery(
				"Select DISTINCT cl from Client cl " + "JOIN FETCH cl.projects ps")
				.getResultList();
	}
	
	//удаление данных из БД
	public void deleteTable(List <Client> clients) {
		em.getTransaction().begin();
		for (Client cl : clients) {
			em.remove(cl);
		}
		em.getTransaction().commit();
	}
	
	//внесение данных из классов в БД
	public void insertData(List <Client> clients) {
		em.getTransaction().begin();
		for (Client cl : clients) {
			em.persist(cl);
			em.flush();
		}
		em.getTransaction().commit();
	}
	
	//добавление данных в БД (минимум задания)
	//остальные данные тянутся за классом Task
	public void addTask(Task t) {
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
	}
	
	/*public void removeC(Client cl) {
		em.getTransaction().begin();
		
		em.find(Client.class, cl);
		em.remove(cl);
		
		em.remove(cl.getProjects().get(0));
		
		em.getTransaction().commit();
	}*/
}