package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();

	/*
	 * addProject() method
	 *  - One parameter (Project project).
	 *  - calls the DAO class to insert a project row.
	 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	/*
	 * fetchAllProjects() method
	 *  - Uses the fetchAllProjects() method in ProjectDao.java
	 * 	- Returns nothing. 
	 */
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> 
			new NoSuchElementException("Project with project ID = " + 
				projectId + " does not exist."));
	}

}
