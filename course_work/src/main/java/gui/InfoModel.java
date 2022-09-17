package gui;

import java.text.*;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import course_work.*;

@SuppressWarnings("serial")
public class InfoModel extends DefaultTableModel{
	InfoModel(String[][] data, String[] columns){super(data, columns);}

	public void showTable(List <Client> someC)
	{
		for (Client cl : someC) {
			for (Project pr : cl.getProjects()) {
				String[] buf = new String[8];
				buf[0] = Integer.toString(pr.getId());
				buf[1] = cl.getName();
				buf[2] = cl.getLastName();
				buf[3] = pr.getName();
				for (Task t : pr.getTasks()) {
					buf[4] = t.getName();
					buf[5] = t.getEmployee().getName();
					buf[6] = t.getEmployee().getLastName();
					Date date = t.getDeadline();
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					buf[7] = dateFormat.format(date);
					addRow(buf);
				}
			}
		}
	}
	
	public void showTableEmps(List <Employee> ems) {
		for (Employee emp : ems) {
			String[] buf = new String[4];
			buf[0] = Integer.toString(emp.getId());
			buf[1] = emp.getName();
			buf[2] = emp.getLastName();
			addRow(buf);
		}
	}
	
	public void showTablePs(Client C) {
		for (Project p : C.getProjects()) {
			String[] buf = new String[2];
			buf[0] = Integer.toString(p.getId());
			buf[1] = p.getName();
			addRow(buf);
		}
	}
	
	public void showTableEmp(Employee emp) {
		for (Task t : emp.getTasks()) {
			String[] buf = new String[2];
			buf[0] = t.getProject().getName();
			buf[1] = t.getName();
			addRow(buf);
		}
	}
	
	public void showTablePro(List <Task> tasks) {
		for (Task t : tasks) {
			String[] buf = new String[4];
			buf[0] = Integer.toString(t.getProject().getId());
			buf[1] = t.getProject().getName();
			buf[2] = t.getName();
			Date date = t.getDeadline();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			buf[3] = dateFormat.format(date);
			addRow(buf);
		}
	}
}