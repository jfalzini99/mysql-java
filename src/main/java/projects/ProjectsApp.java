package projects;

import java.sql.Connection;

import projects.dao.DbConnection;

public class ProjectsApp {

	public static void main(String[] args) {
		
		@SuppressWarnings("unused")
		Connection conn = DbConnection.getConnection();

	}

}
