package gui;

import java.awt.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import course_work.*;
import exceptions.WrongIdException;
import exceptions.WrongLastNameException;
import exceptions.WrongNameException;
import hibernate.NewClientDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ManagerInterface {
	//логгер для окна со всеми данными
	private static final Logger PLlog = LogManager.getLogger(ManagerInterface.class);
	//окно с интерфейсом
	private JFrame clientList;
	//окно с сотрудниками определенного проекта
	private JFrame empList;
	//окно с проектами определенного клиента
	private JFrame clProjs;
	//окно с загрузкой сотрудника
	private JFrame empLoad;
	//окно с проектами с нарушенным сроком заданий
	private JFrame cal;
	//панель инструментов
	private JToolBar toolBar;
	//скроллинг
	private JScrollPane scroll;
	//таблица
	private JTable clients;
	//модель таблицы
	private InfoModel model;
	//кнопки
	//private JButton search;
	private JButton save;
	private JButton edit;
	private JButton open;
	private JButton add;
	private JButton del;
	private JButton print;
	private JButton customer;
	private JButton employee;
	private JButton calendar;
	
    private PDFGenerator report;
	@SuppressWarnings("serial")
	public void show(final List <Client> someC, final NewClientDao someDao) {
		// Создание окна
		clientList = new JFrame("Список клиентов");
		clientList.setSize(1700, 480);
		clientList.setLocationRelativeTo(null);
		clientList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Создание кнопок и прикрепление иконок
	    save = new JButton(new ImageIcon("src/main/resources/icons/save.png"));
	    edit = new JButton(new ImageIcon("src/main/resources/icons/edit.png"));
	    open = new JButton(new ImageIcon("src/main/resources/icons/open.png"));
	    add = new JButton(new ImageIcon("src/main/resources/icons/add.png"));
	    del = new JButton(new ImageIcon("src/main/resources/icons/delete.png"));
	    print = new JButton(new ImageIcon("src/main/resources/icons/print.png"));
	    customer = new JButton(new ImageIcon("src/main/resources/icons/customers.png"));
	    employee = new JButton(new ImageIcon("src/main/resources/icons/employee.png"));
	    calendar = new JButton(new ImageIcon("src/main/resources/icons/calendar.png"));
	    
	    // Настройка подсказок для кнопок
	    save.setToolTipText("Сохранить");
	    edit.setToolTipText("Редактировать");
	    open.setToolTipText("Список сотрудников проекта");
	    add.setToolTipText("Добавить");
	    del.setToolTipText("Удалить");
	    print.setToolTipText("Печать");
	    customer.setToolTipText("Проекты клиента");
	    employee.setToolTipText("Загрузка сотрудника");
	    calendar.setToolTipText("Проекты с нарушенным сроком заданий");
	    
	    //функционал для формирования PDF файла
	    print.setPreferredSize(new Dimension(36,36));
	    print.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Manager is going to print table");
                Thread pdfThread = new Thread(new Runnable(){
                    public void run(){
                    	report = new PDFGenerator("manager_report.pdf");
                        report.addProjects(someC);
                        report.doClose();
                        JOptionPane.showMessageDialog(clientList, "Отчет сгенерирован!", 
                        "Отчёт", JOptionPane.INFORMATION_MESSAGE);
                        PLlog.info("Table printed successfully!");
                    } 
                });
                pdfThread.start();
                
            }
        });
	    
	    //функционал для проектов с нарушенным сроком заданий
	    calendar.setPreferredSize(new Dimension(36,36));
	    calendar.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Preparing to open window with overdue projects");
	    		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
	    	    Date date = new Date();  
	    		JLabel curDate= new JLabel("Сегодняшняя дата: " + formatter.format(date));
    			JPanel content = new JPanel();
    			List<Task> tasks = new ArrayList<Task>();
    			
    			content.add(curDate);
		        for (Client cl : someC) {
		        	for (Project p : cl.getProjects()) {
		        		for (Task t : p.getTasks()) {
		        			if (t.getDeadline().compareTo(date) < 0) tasks.add(t);
		        		}
		        	}
		        }
	            
	            // Создание окна
				cal = new JFrame("Проекты с нарушенным сроком заданий");
				cal.setSize(1000, 400);
				cal.setLocationRelativeTo(null);
    			
    			JTable projs;
    			InfoModel newMod;
    			JScrollPane newScroll;
    			
    			// Создание таблицы с данными
    			String[] cols = {"ID Проекта", "Проект", "Задание", "Срок задания"};
    			String[][] innerdata = new String[0][];
    	 		newMod =  new InfoModel(innerdata, cols);
    	 		projs = new JTable(newMod);
    	 		//прокрутка таблицы
    			newScroll = new JScrollPane(projs);
    			newMod.showTablePro(tasks);
    			
    			// Размещение таблицы с данными
    	        cal.add(newScroll, BorderLayout.CENTER);
    	        cal.add(content, BorderLayout.NORTH);
    			// Визуализация экранной формы
    	        cal.setVisible(true);
    	        PLlog.info("Window with overdue projects created.");
	    	}
	    });
	    
	    //функционал для кнопки загрузки сотрудника
	    employee.setPreferredSize(new Dimension(36,36));
	    employee.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Preparing to open window with employee workload");
	    		final JDialog eBox = new JDialog(clientList, "Поиск сотрудника", true);
	    		List<String> ls = new ArrayList<String>(); 
	    		final List<Employee> es = new ArrayList<Employee>();
	    		boolean flag = true;
	    		
	    		for (Client cl : someC) {
	    			for (Project pr : cl.getProjects()) {
	    				for (Task t : pr.getTasks()) {
	    					for (Employee em : es) {
	    						if (em.getId() == t.getEmployee().getId()) flag = false; 
	    					}
	    					if (flag) {
	    						es.add(t.getEmployee());
	    					}
	    					flag = true;
	    				}
	    			}
	    		}
	    		
	    		for (Employee empl : es) {
	    			ls.add("ID " + empl.getId() + ": " + empl.getName() + " " + empl.getLastName());
	    		}
	    		String[] array = ls.toArray(new String[ls.size()]);
	    		@SuppressWarnings({ "rawtypes", "unchecked" })
				final JComboBox comboBox = new JComboBox(array);
		        JPanel content = new JPanel();
		        JLabel text = new JLabel("Выберите сотрудника: ");
		        JButton enterData = new JButton("ОК");
		        
		        enterData.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent e) {
	    				eBox.dispose();
	    				String selected = comboBox.getSelectedItem().toString();
	    				//удаление из текста символов
	    				selected = selected.replaceAll("\\D+","");
	    				int eid = Integer.parseInt(selected);
	    				Employee searched = new Employee();
	    				for (Employee empl : es) {
	    					if (eid == empl.getId()) searched = empl;
	    				}
	    				
	    				// Создание окна
	    				empLoad = new JFrame("Загрузка сотрудника");
	    				empLoad.setSize(600, 200);
	    				empLoad.setLocationRelativeTo(null);
	    				JLabel j = new JLabel("Cотрудник c ID: " + searched.getId() + " " +
	    				 searched.getName() + " " + searched.getLastName());
            			JPanel emp = new JPanel();
	    				emp.add(j);
	    				
            			JTable projs;
            			InfoModel newMod;
            			JScrollPane newScroll;
            			
            			// Создание таблицы с данными
            			String[] cols = {"Проект", "Задание"};
            			String[][] innerdata = new String[0][];
            	 		newMod =  new InfoModel(innerdata, cols);
            	 		projs = new JTable(newMod);
            	 		//прокрутка таблицы
            			newScroll = new JScrollPane(projs);
            	        newMod.showTableEmp(searched);
            			
            			// Размещение таблицы с данными
            	        empLoad.add(newScroll, BorderLayout.CENTER);
            			empLoad.add(emp, BorderLayout.NORTH);
            			// Визуализация экранной формы
            	        empLoad.setVisible(true);
	    			}
		        });
		        
		        content.add(text);
		        content.add(comboBox);
		        content.add(enterData);
		        
		        content.add(text, BorderLayout.NORTH);
		        content.add(comboBox, BorderLayout.CENTER);
		        content.add(enterData, BorderLayout.SOUTH);
		        eBox.setContentPane(content);
	            enterData.setSize(50, 50);
	            eBox.setSize(300, 130);
	            eBox.setResizable(false);
	            eBox.setLocationRelativeTo(null);;
	            eBox.setVisible(true);
	            PLlog.info("Window with employee workload created.");
	    	}
	    });
	    
	    //функционал для кнопки поисков проектов клиента
	    customer.setPreferredSize(new Dimension(36,36));
	    customer.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Preparing to open window with specific client's projects.");
	    		final JDialog clBox = new JDialog(clientList, "Поиск клиента", true);
	    		List<String> ls = new ArrayList<String>(); 
	    		for (Client c : someC) {
	    			ls.add(c.getId() + " " + c.getName() + " " + c.getLastName());
	    		}
	    		String[] array = ls.toArray(new String[ls.size()]);
	    		@SuppressWarnings({ "rawtypes", "unchecked" })
				final JComboBox comboBox = new JComboBox(array);
		        JPanel content = new JPanel();
		        JLabel text = new JLabel("Выберите клиента: ");
		        JButton enterData = new JButton("ОК");
		        
		        enterData.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent e) {
	    				clBox.dispose();
	    				String selected = comboBox.getSelectedItem().toString();
	    				selected = selected.replaceAll("\\D+","");
	    				int cid = Integer.parseInt(selected);
	    				Client searched = new Client();
	    				for (Client c : someC) {
	    					if (cid == c.getId()) searched = c;
	    				}
	    				
	    				// Создание окна
	    				clProjs = new JFrame("Список проектов клиента");
	    				clProjs.setSize(600, 200);
	    				clProjs.setLocationRelativeTo(null);
	    				JLabel j = new JLabel("Выбран клиент с ID: " + searched.getId() + " " +
	   	    				 searched.getName() + " " + searched.getLastName());
	               			JPanel emp = new JPanel();
	   	    				emp.add(j);
	    				
            			JTable projs;
            			InfoModel newMod;
            			JScrollPane newScroll;
            			
            			// Создание таблицы с данными
            			String[] cols = {"ID Проекта", "Название"};
            			String[][] innerdata = new String[0][];
            	 		newMod =  new InfoModel(innerdata, cols);
            	 		projs = new JTable(newMod);
            	 		//прокрутка таблицы
            			newScroll = new JScrollPane(projs);
            	        newMod.showTablePs(searched);
            			
            			// Размещение таблицы с данными
            			clProjs.add(newScroll, BorderLayout.CENTER);
            			clProjs.add(emp, BorderLayout.NORTH);
            			// Визуализация экранной формы
            			clProjs.setVisible(true);
            			PLlog.info("Window with client's projects opened.");
	    			}
		        });
		        
		        content.add(text);
		        content.add(comboBox);
		        content.add(enterData);
		        
		        content.add(text, BorderLayout.NORTH);
		        content.add(comboBox, BorderLayout.CENTER);
		        content.add(enterData, BorderLayout.SOUTH);
	            clBox.setContentPane(content);
	            enterData.setSize(50, 50);
	            clBox.setSize(300, 130);
	            clBox.setResizable(false);
	            clBox.setLocationRelativeTo(null);;
	            clBox.setVisible(true);

	    	}
	    });
	    
	    //функционал для переноса данных в БД
	    save.setPreferredSize(new Dimension(36,36));
	    save.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
                PLlog.info("Changes in tables are going to be saved in DB");
	    		//someDao.deleteTable(someC);
	    		someDao.insertData(someC);
	    		JOptionPane.showMessageDialog(clientList, "Изменения сохранены в Базе Данных", "", 
	                    JOptionPane.INFORMATION_MESSAGE);
	                    PLlog.info("Data saved successfully!");

	    	}
	    });
	    
	    //функционал для кнопки изменения данных
	    edit.setPreferredSize(new Dimension(36,36));
	    edit.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Edit button was pressed.");
	    		final JDialog errorBox =  new JDialog(clientList, "Выбор поля", true);
	    	try {
	    		int row = clients.getSelectedRow();
	    		final ArrayList<String> data = new ArrayList<String>();
	    		for (int i = 0; i < 8; i++) data.add(clients.getModel().getValueAt(row,i).toString());
	    		
	    		String[] options = {"Имя и фамилия клиента", "Название проекта", 
	    				"Данные задания", "Имя и фамилия сотрудника"
	    		};
	    		int choice = JOptionPane.showOptionDialog(null, "Что вы хотите отредактировать?",
	                    "Редактирование данных таблицы",
	                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);	
	    		if (choice == 0) {
	    			PLlog.info("Manager is going to edit client.");
	    			final JDialog editClient = new JDialog(clientList, "Редактирование клиента", true);
	    			JPanel commonPanel = new JPanel();
	    			
	    			JLabel info = new JLabel("Выбран клиент");
	    			JLabel info1 = new JLabel(data.get(1));
	    			JLabel info2 = new JLabel(data.get(2));
	    			
	    			JLabel clName = new JLabel("Новое имя клиента: ");
	    			JLabel clLastName = new JLabel("Новая фамилия клиента: ");
	    			
	    			final JTextField newClName = new JTextField(15);
		    		final JTextField newClLastName = new JTextField(15);
	    			
	    			JPanel infoPanel = new JPanel();
	    			JPanel clNamePanel = new JPanel();
	    			JPanel clLastNamePanel = new JPanel();
	    			
	    			JButton enterData = new JButton("ОК");
	    			infoPanel.add(info);
	    			infoPanel.add(info1);
	    			infoPanel.add(info2);
	    			clNamePanel.add(clName);
	    			clNamePanel.add(newClName);
	    			clLastNamePanel.add(clLastName);
	    			clLastNamePanel.add(newClLastName);
	    			commonPanel.add(infoPanel);
		    		commonPanel.add(clNamePanel);
		    		commonPanel.add(clLastNamePanel);
		    		commonPanel.add(enterData);
		    		
		    		enterData.addActionListener(new ActionListener() {
		    			public void actionPerformed(ActionEvent e) {
		    				try {
		    					PLlog.info("Editing client.");
		    					Client temp = new Client();
		    					temp.setName(newClName.getText().trim());
		    					temp.setLastName(newClLastName.getText().trim());
		    					temp.dataCheck();
		    					
		    					for (Client cl : someC) {
		    						if (cl.equalsName(data.get(1))&&cl.equalsLastName(data.get(2))) {
		    							cl.setName(temp.getName());
		    							cl.setLastName(temp.getLastName());
		    							break;
		    						}
		    					}
		    					while (model.getRowCount()>0) model.removeRow(0);
		            			model.showTable(someC);
		            			
		            			PLlog.info("Client edited.");
		            			JOptionPane.showMessageDialog(editClient, "Клиент отредактирован!", "", 
		    		                    JOptionPane.INFORMATION_MESSAGE);
		    		                    editClient.dispose();
		    				}
		    				catch(WrongNameException exName){
		    					PLlog.error("Manager entered incorrect client's name.");
		                        JOptionPane.showMessageDialog(editClient, exName.getMessage(), "", 
		                        JOptionPane.ERROR_MESSAGE);
		                    }
		                    catch(WrongLastNameException exLName){
		                    	PLlog.error("Manager entered incorrect client's new last name.");
		                        JOptionPane.showMessageDialog(editClient, exLName.getMessage(), "", 
		                        JOptionPane.ERROR_MESSAGE);
		                    }
		    				catch(IllegalArgumentException someEX) {
		    					PLlog.error("Manager entered incorrect client's new name or new last name.");
		    					JOptionPane.showMessageDialog(editClient, someEX.getMessage(), "", 
		   	    	                 JOptionPane.ERROR_MESSAGE);
		    				}
		    			}
		    		});
		    		editClient.setContentPane(commonPanel);
		    		editClient.setSize(360, 180);
		    		editClient.setResizable(false);
		    		editClient.setLocationRelativeTo(null);
		    		editClient.setVisible(true);
	    		}
	    		if (choice == 1) {
	    			PLlog.info("Editing project.");
	    			final JDialog editProject = new JDialog(clientList, "Редактирование проекта", true);
	    			JPanel commonPanel = new JPanel();
	    			
	    			JLabel info = new JLabel("Выбран проект с ID");
	    			JLabel info1 = new JLabel(data.get(0));
	    			JLabel info2 = new JLabel(data.get(3));
	    			
	    			JLabel prName = new JLabel("Новое название проекта: ");
	    			
	    			final JTextField newPrName = new JTextField(15);
	    			
	    			JPanel infoPanel = new JPanel();
	    			JPanel prNamePanel = new JPanel();
	    			
	    			JButton enterData = new JButton("ОК");
	    			infoPanel.add(info);
	    			infoPanel.add(info1);
	    			infoPanel.add(info2);
	    			prNamePanel.add(prName);
	    			prNamePanel.add(newPrName);
	    			
	    			commonPanel.add(infoPanel);
	    			commonPanel.add(prNamePanel);
		    		commonPanel.add(enterData);
		    		
		    		enterData.addActionListener(new ActionListener() {
		    			public void actionPerformed(ActionEvent e) {
		    				try {
		    					PLlog.info("Editing project's data.");
		    					Project temp = new Project();
		    					temp.setName(newPrName.getText().trim());
		    					temp.setId(1);
		    					temp.dataCheck();
		    					
		    					for (Client cl : someC) {
		    						for (Project pr : cl.getProjects()) {
		    							if (pr.equalsName(data.get(3))&&(pr.getId() == Integer.parseInt(data.get(0)))) {
		    								pr.setName(temp.getName());
		    							}
		    						}
		    					}		    					
		    					while (model.getRowCount()>0) model.removeRow(0);
		            			model.showTable(someC);
		            			
		            			JOptionPane.showMessageDialog(editProject, "Проект отредактирован!", "", 
		    		                    JOptionPane.INFORMATION_MESSAGE);
		    		                    editProject.dispose();
		    		                    PLlog.info("Project editedt.");
		    				}
		    				catch(WrongIdException exId) {
		    					PLlog.error("Wrong project's id.");
		    					JOptionPane.showMessageDialog(editProject, exId.getMessage(), "", 
		    	                 JOptionPane.ERROR_MESSAGE);
		    				}
		    				catch(IllegalArgumentException newEx) {
		    					PLlog.error("Wrong new project data.");
		    					JOptionPane.showMessageDialog(editProject, newEx.getMessage(), "", 
				    	                 JOptionPane.ERROR_MESSAGE);
		    				}
		    			}
		    		});
		    		
		    		editProject.setContentPane(commonPanel);
		    		editProject.setSize(360, 180);
		    		editProject.setResizable(false);
		    		editProject.setLocationRelativeTo(null);
		    		editProject.setVisible(true);
	    		}
	    		if (choice == 2) {
	    			PLlog.info("Editing task.");
	    			final JDialog editTask = new JDialog(clientList, "Редактирование задания", true);
	    			JPanel commonPanel = new JPanel();
	    			JLabel info = new JLabel("Выбрано задание проекта:");
	    			JLabel info1 = new JLabel(data.get(3));
	    			JLabel infoT = new JLabel("Задание:");
	    			JLabel info2 = new JLabel(data.get(4));
	    			JLabel tName = new JLabel("Новое название задания: ");
	    			JLabel tDeadline = new JLabel("Выполнить до (dd/mm/yyy): ");
	    			
	    			final JTextField newTName = new JTextField(15);
	    			final JTextField newTD = new JTextField(10);
	    			
	    			JPanel infoPanel = new JPanel();
	    			JPanel projectPanel = new JPanel();
	    			JPanel taskPanel = new JPanel();
	    			JPanel tNamePanel = new JPanel();
	    			JPanel tDeadlinePanel = new JPanel();
	    			
	    			JButton enterData = new JButton("ОК");
	    			infoPanel.add(info);
	    			projectPanel.add(info1);
	    			taskPanel.add(infoT);
	    			taskPanel.add(info2);
	    			tNamePanel.add(tName);
	    			tNamePanel.add(newTName);
	    			tDeadlinePanel.add(tDeadline);
	    			tDeadlinePanel.add(newTD);
	    			
	    			commonPanel.add(infoPanel);
	    			commonPanel.add(projectPanel);
	    			commonPanel.add(taskPanel);
	    			commonPanel.add(tNamePanel);
	    			commonPanel.add(tDeadlinePanel);
		    		commonPanel.add(enterData);
		    		
		    		enterData.addActionListener(new ActionListener() {
		    			public void actionPerformed(ActionEvent e) {
		    				try {
		    					Task t = new Task();
		    					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		    					formatter.setLenient(false);
		    					String date = newTD.getText().trim();
		    					Date someD = formatter.parse(date);
		    					t.setName(newTName.getText().trim());
		    					t.setDeadline(someD);
		    					t.dataCheck();
		    					
		    					for (Client cl : someC) {
		    						for (Project pr : cl.getProjects()) {
		    							for (Task ta : pr.getTasks()) {
		    								if (pr.equalsName(data.get(3))&&(ta.equalsName(data.get(4)))) {
		    									ta.setName(t.getName());
		    									ta.setDeadline(t.getDeadline());
		    								}
		    							}
		    						}
		    					}		    					
		    					while (model.getRowCount()>0) model.removeRow(0);
		            			model.showTable(someC);
		            			
		            			PLlog.info("Task edited.");
		            			JOptionPane.showMessageDialog(editTask, "Задание отредактировано!", "", 
		    		                    JOptionPane.INFORMATION_MESSAGE);
		    		                    editTask.dispose();
		    				}
		    				catch(ParseException ex) {
		    					PLlog.error("Entered wrong date!");
		                    	JOptionPane.showMessageDialog(editTask, "Некорректная дата!", "", 
		                        JOptionPane.ERROR_MESSAGE);
		                    }
		    				catch(IllegalArgumentException someEX) {
		    					PLlog.error("Entered wrong task data!");
		    					JOptionPane.showMessageDialog(editTask, someEX.getMessage(), "", 
		   	    	                 JOptionPane.ERROR_MESSAGE);
		    				}
		    			}
		    		});
		    		editTask.setContentPane(commonPanel);
		    		editTask.setSize(460, 200);
		    		editTask.setResizable(false);
		    		editTask.setLocationRelativeTo(null);
		    		editTask.setVisible(true);
	    		}
	    		if (choice == 3) {
	    			PLlog.info("Editing employee.");
	    			final JDialog editEmp = new JDialog(clientList, "Редактирование сотрудника", true);
	    			JPanel commonPanel = new JPanel();
	    			
	    			JLabel info = new JLabel("Выбран сотрудник");
	    			JLabel info1 = new JLabel(data.get(5));
	    			JLabel info2 = new JLabel(data.get(6));
	    			
	    			JLabel empName = new JLabel("Новое имя сотрудника: ");
	    			JLabel empLastName = new JLabel("Новая фамилия сотрудника: ");
	    			
	    			final JTextField newEmName = new JTextField(15);
		    		final JTextField newEmLastName = new JTextField(15);
	    			
	    			JPanel infoPanel = new JPanel();
	    			JPanel emNamePanel = new JPanel();
	    			JPanel emLastNamePanel = new JPanel();
	    			
	    			JButton enterData = new JButton("ОК");
	    			infoPanel.add(info);
	    			infoPanel.add(info1);
	    			infoPanel.add(info2);
	    			emNamePanel.add(empName);
	    			emNamePanel.add(newEmName);
	    			emLastNamePanel.add(empLastName);
	    			emLastNamePanel.add(newEmLastName);
	    			commonPanel.add(infoPanel);
		    		commonPanel.add(emNamePanel);
		    		commonPanel.add(emLastNamePanel);
		    		commonPanel.add(enterData);
		    		
		    		enterData.addActionListener(new ActionListener() {
		    			public void actionPerformed(ActionEvent e) {
		    				try {
		    					Employee temp = new Employee();
		    					temp.setName(newEmName.getText().trim());
		    					temp.setLastName(newEmLastName.getText().trim());
		    					temp.dataCheck();
		    					
		    					for (Client cl : someC) {
		    						for (Project pr : cl.getProjects()) {
		    							for (Task ta : pr.getTasks()) {
		    								if (ta.getEmployee().equalsName(data.get(5))&&
		    										ta.getEmployee().equalsLastName(data.get(6))) {
		    									ta.getEmployee().setName(temp.getName());
		    									ta.getEmployee().setLastName(temp.getLastName());
		    								}
		    							}
		    						}
		    					}
		    					while (model.getRowCount()>0) model.removeRow(0);
		            			model.showTable(someC);
		            			PLlog.info("Employee edited.");
		            			JOptionPane.showMessageDialog(editEmp, "Сотрудник отредактирован!", "", 
		    		                    JOptionPane.INFORMATION_MESSAGE);
		    		                    editEmp.dispose();
		    				}
		    				catch(IllegalArgumentException someEX) {
		    					PLlog.error("Wrong employee data.");
		    					JOptionPane.showMessageDialog(editEmp, someEX.getMessage(), "", 
			   	    	                 JOptionPane.ERROR_MESSAGE);
			    			} 
		    				catch(WrongNameException exName){
		    					PLlog.error("Wrong employee name.");
		                        JOptionPane.showMessageDialog(editEmp, exName.getMessage(), "", 
		                        JOptionPane.ERROR_MESSAGE);
		                    }
		                    catch(WrongLastNameException exLName){
		                    	PLlog.error("Wrong employee last name.");
		                        JOptionPane.showMessageDialog(editEmp, exLName.getMessage(), "", 
		                        JOptionPane.ERROR_MESSAGE);
		                    }
		    			}
		    		});
		    		
		    		editEmp.setContentPane(commonPanel);
		    		editEmp.setSize(360, 180);
		    		editEmp.setResizable(false);
		    		editEmp.setLocationRelativeTo(null);
		    		editEmp.setVisible(true);
	    		}
	    	}
	    	catch (IndexOutOfBoundsException newEx) {
	    		PLlog.error("Manager didn't choose line for editing.");
				JOptionPane.showMessageDialog(errorBox, "Выберите строку для редактирования.", "", 
   	               JOptionPane.ERROR_MESSAGE);
			}
	    	}
	    });
	    
	    //список сотрудников занятых на определенном проекте
	    open.setPreferredSize(new Dimension(36,36));
	    open.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Manager is going to check project's list of employee.");
		    	final JDialog projBox = new JDialog(clientList, "Список сотрудников проекта", true);
		    	final JTextField smallField = new JTextField(4);
		        smallField.setHorizontalAlignment(JTextField.RIGHT);
		        JPanel content = new JPanel();
		        JLabel text = new JLabel("Введите ID проекта и нажмите ENTER: ");
		        
		        smallField.addActionListener(new ActionListener(){
	            	public void actionPerformed(ActionEvent e) {
	            		try {
	            			int id = Integer.parseInt(smallField.getText());
	            			List<Employee> ems = new ArrayList<Employee>();
	            			String name = "Несуществующий проект";
	            			for (Client cl : someC) {
	            				for (Project pr : cl.getProjects()) {
	            					if (pr.getId() == id) {
	            						for (Task t : pr.getTasks()) {
	            							ems.add(t.getEmployee());
	            						}
	            						name = pr.getName();
	            					}
	            				}
	            			}
	            			projBox.dispose();
	            			// Создание окна
	            			empList = new JFrame("Список сотрудников проекта");
	            			empList.setSize(600, 200);
	            			empList.setLocationRelativeTo(null);
	            			
	            			JTable emps;
	            			InfoModel newMod;
	            			JScrollPane newScroll;
	            			JLabel j = new JLabel("Проект: " + name);
	                   			JPanel emp = new JPanel();
	       	    				emp.add(j);
	            			
	            			// Создание таблицы с данными
	            			String[] cols = {"ID Сотрудника", "Имя", "Фамилия"};
	            			String[][] innerdata = new String[0][];
	            	 		newMod =  new InfoModel(innerdata, cols);
	            	 		emps = new JTable(newMod);
	            	 		//прокрутка таблицы
	            			newScroll = new JScrollPane(emps);
	            	        newMod.showTableEmps(ems);
	            			
	            			// Размещение таблицы с данными
	            			empList.add(newScroll, BorderLayout.CENTER);
	            			empList.add(emp, BorderLayout.NORTH);
	            			
	            			// Визуализация экранной формы
	            			empList.setVisible(true);
	            		}
	            		catch(NullPointerException exNull){
	                        JOptionPane.showMessageDialog(projBox, "Такого проекта нет!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                    catch(NumberFormatException exNum){
	                        JOptionPane.showMessageDialog(projBox, "Некорректные данные!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	            		catch(IndexOutOfBoundsException exNum){
	                        JOptionPane.showMessageDialog(projBox, "Такого проекта нет!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            
	            content.add(text, BorderLayout.NORTH);
	            content.add(smallField);
	            projBox.setContentPane(content);
	            projBox.setSize(300, 130);
	            projBox.setResizable(false);
	            projBox.setLocationRelativeTo(null);;
	            projBox.setVisible(true);
	    	}
	    });
	    
	    //функционал для кнопки добавления данных
	    add.setPreferredSize(new Dimension(36,36));
	    add.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Manager is going to add data.");
	    		JPanel commonPanel = new JPanel();
	    		
	    		final JDialog addBox = new JDialog(clientList, "Добавление проекта", true);
	    		//вывод текста в иконке
	    		JLabel clName = new JLabel("Имя клиента: ");
	    		JLabel clLastName = new JLabel("Фамилия клиента: ");
	    		JLabel prName = new JLabel("Название проекта: ");
	    		JLabel taskName = new JLabel("Задание: ");
	    		JLabel deadlineName = new JLabel("Дедлайн (\"dd-MM-yyyy\"): ");
	    		JLabel emName = new JLabel("Имя сотрудника: ");
	    		JLabel emLastName = new JLabel("Фамилия сотрудника: ");
	            
	    		final JTextField newClName = new JTextField(15);
	    		final JTextField newClLastName = new JTextField(15);
	    		final JTextField newPrName = new JTextField(15);
	    		final JTextField newTaskName = new JTextField(15);
	    		final JTextField newDeadline = new JTextField(10);
	    		final JTextField newEmName = new JTextField(15);
	    		final JTextField newEmLastName = new JTextField(15);
	    		
	    		JPanel clNamePanel = new JPanel();
	    		JPanel clLastNamePanel = new JPanel();
	    		JPanel prPanel = new JPanel();
	    		JPanel taskPanel = new JPanel();
	    		JPanel deadlinePanel = new JPanel();
	    		JPanel emNamePanel = new JPanel();
	    		JPanel emLastNamePanel = new JPanel();
	    		
	    		JButton addProject = new JButton("Добавить");
	    		
	    		clNamePanel.add(clName);
	    		clNamePanel.add(newClName);
	    		clLastNamePanel.add(clLastName);
	    		clLastNamePanel.add(newClLastName);
	    		prPanel.add(prName);
	    		prPanel.add(newPrName);
	    		taskPanel.add(taskName);
	    		taskPanel.add(newTaskName);
	    		deadlinePanel.add(deadlineName);
	    		deadlinePanel.add(newDeadline);
	    		emNamePanel.add(emName);
	    		emNamePanel.add(newEmName);
	    		emLastNamePanel.add(emLastName);
	    		emLastNamePanel.add(newEmLastName);
	    		
	    		commonPanel.add(clNamePanel);
	    		commonPanel.add(clLastNamePanel);
	    		commonPanel.add(prPanel);
	    		commonPanel.add(taskPanel);
	    		commonPanel.add(deadlinePanel);
	    		commonPanel.add(emNamePanel);
	    		commonPanel.add(emLastNamePanel);
	    		commonPanel.add(addProject, BorderLayout.NORTH);
	    		
	    		addProject.addActionListener(new ActionListener() {
	    			public void actionPerformed(ActionEvent e) {
	    				PLlog.info("Adding data.");
	    				//добавление данных в классы
	    				//с учетом возможности добавления к клиенту проекта
	    				try {
	    					boolean mark = true;
	    					boolean mark_2 = true;
	    					boolean mark_3 = true;
	    					int id = 0;
	    					int t_id = 0;
	    					int cl_id = 0;
	    					
	    					Employee em = new Employee();
	    					//проверка если работник уже существует 
	    					//очень некрасиво но работает :(
	    					for (Client cli : someC) {
	    						for (Project pro : cli.getProjects()) {
	    							for (Task ta : pro.getTasks()) {
	    								if ((ta.getEmployee().equalsName(newEmName.getText().trim()))&&
	    										(ta.getEmployee().equalsLastName(newEmLastName.getText().trim()))) {
	    									em = ta.getEmployee();
	    									//добавление к работнику задания
	    									ta.getEmployee().addTask(ta);
	    									mark_2 = false;
	    								}
	    								//присвоение значения полю id сотрудника
	    								if (ta.getEmployee().getId() > id) id = ta.getEmployee().getId();
	    								//присвоение значения полю id задания
	    								if (ta.getId() > t_id) t_id = ta.getId();
	    							}
	    						}
	    					}
	    					if (mark_2) {
	    						//если новый сотрудник
	    						em.setId(id + 1);
	    						em.setName(newEmName.getText().trim());
	    						em.setLastName(newEmLastName.getText().trim());
	    						em.dataCheck();
	    					}

	    					Project pr = new Project();
	    					for (Client cle : someC) {
	    						//если проект добавляется
	    						//к существующему клиенту то проверяются 
	    						//введенные имя и фамилия клиента
	    						if ((cle.equalsName(newClName.getText().trim())&&
	    								(cle.equalsLastName(newClLastName.getText().trim())))) {
	    							for (Project pro : cle.getProjects()) {
	    								if (pro.equalsProjects(newPrName.getText())) {
	    									pr = pro;
	    									mark_3 = false;
	    									mark = false;
	    								}
	    							}
	    							if (mark_3) {
	    							//создание нового проекта
	    							 pr.setName(newPrName.getText().trim());
	    							 pr.setName(newPrName.getText().trim());
	    							 pr.setId(cle.lastPrId() + 1);
	    							 pr.dataCheck();
	    							 pr.addClient(cle);
	    							 cle.addProj(pr);
	    							 mark = false;
	    							}
	    						}
	    						cl_id++;
	    					}
	    					if (mark) {
	    						Client newC = new Client();
	    						newC.setId(cl_id + 1);
	    						newC.setName(newClName.getText().trim());
	    						newC.setLastName(newClLastName.getText().trim());
	    						newC.dataCheck();
	    						pr.setName(newPrName.getText().trim());
	    						pr.setId(someC.get(someC.size() - 1).getProjects().get(0).getId() + 10);
	    						pr.dataCheck();
	    						pr.addClient(newC);
	    						someC.add(newC);
	    						newC.addProj(pr);
	    					}
	    					
	    					Task t = new Task();
	    					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	    					formatter.setLenient(false);
	    					String date = newDeadline.getText().trim();
	    					Date someD = formatter.parse(date);
	    					t.setName(newTaskName.getText().trim());
	    					t.setId(t_id + 1); 
	    					t.dataCheck();
	    					t.setDeadline(someD);
	    					t.setEmployee(em);
	    					t.addProject(pr);
	    					pr.addTask(t);
	    					
	    					someDao.addTask(t);
	    					
	    					
	    					model.addRow(new String[] {
	    							Integer.toString(pr.getId()),
	    							pr.getClientName(),
	    							pr.getClientLastName(),
	    							pr.getName(),
	    							t.getName(),
	    							em.getName(),
	    							em.getLastName(),
	    							date,
	    							Integer.toString(em.getId()),
	    					});
	    					PLlog.info("Data succesfully added.");
	    					JOptionPane.showMessageDialog(addBox, "Данные внесены!", "", 
	    		                    JOptionPane.INFORMATION_MESSAGE);
	    		                    addBox.dispose();
	    				}
	    				catch(NumberFormatException exNum){
	    					PLlog.error("Wrong data.");
	                        JOptionPane.showMessageDialog(addBox, "Введены некорректные данные!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                    catch(ParseException ex) {
	                    	PLlog.error("Wrong date.");
	                    	JOptionPane.showMessageDialog(addBox, "Некорректная дата!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	    				catch(WrongNameException exName){
	    					PLlog.error("Wrong name.");
	                        JOptionPane.showMessageDialog(addBox, exName.getMessage(), "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                    catch(WrongLastNameException exLName){
	                    	PLlog.error("Wrong last name.");
	                        JOptionPane.showMessageDialog(addBox, exLName.getMessage(), "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	    				catch(WrongIdException exId) {
	    					PLlog.error("Wrong id.");
	    					JOptionPane.showMessageDialog(addBox, exId.getMessage(), "", 
	    	                 JOptionPane.ERROR_MESSAGE);
	    				}
	    				catch(IllegalArgumentException someEX) {
	    					PLlog.error("Wrong.");
	    					JOptionPane.showMessageDialog(addBox, someEX.getMessage(), "", 
	   	    	                 JOptionPane.ERROR_MESSAGE);
	    				}
	                }
	    		});
	    		
	    		addBox.setContentPane(commonPanel);
	            addBox.setSize(360, 360);
	            addBox.setResizable(false);
	            addBox.setLocationRelativeTo(null);
	            addBox.setVisible(true);
	    	}
	    });
	    
	    //создание интерфейса и функционала для кнопки удаления данных из таблицы
	    del.setPreferredSize(new Dimension(36,36));
	    del.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		PLlog.info("Manager is going to delete project.");
	    		final JDialog deleteBox = new JDialog(clientList, "Удалить проект", true);
	    		final JTextField smallField = new JTextField(4);
	            smallField.setHorizontalAlignment(JTextField.RIGHT);
	            JPanel content = new JPanel();
	            JLabel text = new JLabel("Введите ID проекта и нажмите ENTER: ");
	            
	            smallField.addActionListener(new ActionListener(){
	            	public void actionPerformed(ActionEvent e) {
	            		try {
	            			int id = Integer.parseInt(smallField.getText());
	            			int cl_id = 0;
	            			//поиск id клиента
	            			for (Client cl : someC) {
	            				for (Project pr : cl.getProjects()) {
	            					if (pr.getId() == id) {
	            						cl_id = someC.indexOf(cl);
	            						break;
	            					}
	            				}
	            			}
	            			someC.get(cl_id).find(id);
	            			//если у клиента удаляется единственный проект
	            			if (someC.get(cl_id).getProjects().size() == 1) {
	            				//someDao.removeC(someC.get(cl_id)); 
	            				someC.remove(cl_id);
	            			}
	            			//если проектов несколько
	            			else someC.get(cl_id ).delete(id);
	            			PLlog.info("Project deleted.");
	            			JOptionPane.showMessageDialog(deleteBox, "Проект удален", "", 
	                                JOptionPane.INFORMATION_MESSAGE);
	            			deleteBox.dispose();
	            			while (model.getRowCount()>0) model.removeRow(0);
	            			model.showTable(someC);
	            		}
	            		catch(NullPointerException exNull){
	                        JOptionPane.showMessageDialog(deleteBox, "Такого проекта нет!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                    catch(NumberFormatException exNum){
	                        JOptionPane.showMessageDialog(deleteBox, "Некорректные данные!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	            		catch(IndexOutOfBoundsException exNum){
	                        JOptionPane.showMessageDialog(deleteBox, "Такого проекта нет!", "", 
	                        JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	            });
	            
	            content.add(text, BorderLayout.NORTH);
	            content.add(smallField);
	            deleteBox.setContentPane(content);
	            deleteBox.setSize(300, 130);
	            deleteBox.setResizable(false);
	            deleteBox.setLocationRelativeTo(null);;
	            deleteBox.setVisible(true);
	    	}
	    });
	    
	    //Добавление кнопок на панель инструментов
	    toolBar = new JToolBar("Панель инструментов");
	    toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
	    toolBar.add(save);
	    toolBar.add(edit);
	    toolBar.add(add);
	    toolBar.add(del);
	    toolBar.add(print);
	    toolBar.add(open);
	    toolBar.add(customer);
	    toolBar.add(employee);
	    toolBar.add(calendar);
	    // Размещение панели инструментов
	    clientList.setLayout(new BorderLayout());
	    clientList.add(toolBar, BorderLayout.NORTH);
	    
	    // Создание таблицы с данными
	 		String [] columns = {"ID Проекта", "Имя клиента", "Фамилия клиента", "Проект", "Задание", "Имя сотрудника", "Фамилия Сотрудника", "Срок задания"};
	 		String [][] data = new String[0][];
	 		model =  new InfoModel(data, columns);
	 		//отключение возможности редактирования данных в самой таблице
	 		clients = new JTable(model){
	            @Override
	            public boolean isCellEditable(int row, int column){ return false; }
	        };
	 		//прокрутка таблицы
			scroll = new JScrollPane(clients);
			Thread pLThread = new Thread(new Runnable(){
				public void run(){
					model.showTable(someC);
				}
			});
			pLThread.start();
			// Размещение таблицы с данными
			clientList.add(scroll, BorderLayout.CENTER);
			
			// Визуализация экранной формы
			clientList.setVisible(true);
	}
}