package application;

import java.util.List;
import course_work.*;
import gui.*;
import hibernate.*;

public class Main {
	public static void main(String[] args) {
		//����� ������������ ��� ������ ��������������� ����� �� � ���������
		NewClientDao Dao = new NewClientDao();
		List<Client> cs = Dao.findClients();
		ManagerInterface GUI = new ManagerInterface();
		GUI.show(cs, Dao);
	}
}
