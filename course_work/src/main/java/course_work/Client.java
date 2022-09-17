package course_work;
import javax.persistence.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

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
	
	public void addCells(PdfPTable table, Font font) {
		for (Project pr : this.getProjects()) {
            for (Task t : pr.getTasks()) {
            	PdfPCell pfpc;
                table.addCell(""+pr.getId());
                pfpc = new PdfPCell(new Phrase(this.getName(), font));
                table.addCell(pfpc);
                pfpc = new PdfPCell(new Phrase(this.getLastName(), font));
                table.addCell(pfpc);
                pfpc = new PdfPCell(new Phrase(pr.getName(), font));
                table.addCell(pfpc);
				pfpc = new PdfPCell(new Phrase(t.getName(), font));
	            table.addCell(pfpc);
	            pfpc = new PdfPCell(new Phrase(t.getEmployee().getLastName(), font));
	            table.addCell(pfpc);
				Date date = t.getDeadline();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String d = dateFormat.format(date);
				pfpc = new PdfPCell(new Phrase(d, font));
	            table.addCell(pfpc);
			}
		}
	}
	
	public Client() {};
}