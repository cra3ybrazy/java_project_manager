package course_work;
import javax.persistence.*;

import exceptions.WrongLastNameException;
import exceptions.WrongNameException;

@MappedSuperclass //базовый класс - не сущность
public class Human {
	@Id
	@Column(name = "id")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "last_name")
	private String lastName;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public boolean equalsName(String info) {
	       return this.getName().equals(info);
	}
	
	public boolean equalsLastName(String info) {
	       return this.getLastName().equals(info);
	}
	
	public void dataCheck() throws IllegalArgumentException, 
	WrongNameException, WrongLastNameException{
		if (name.equals("")||lastName.equals("")) {
			throw new IllegalArgumentException("Некоторые данные не заполнены!");
		}
		for (int i = 0; i<name.length(); ++i)
			//проверка на ввод данных кириллицей и без цифр
            if ((int)name.charAt(i)<192){ 
                throw new WrongNameException("Некорректно введенное имя");
            }
		for (int i = 0; i<lastName.length(); ++i)
			//проверка на ввод данных кириллицей и без цифр
            if ((int)lastName.charAt(i)<192){
                throw new WrongLastNameException("Некорректно введенная фамилия");
            }
	}
}

