package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

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
	
	/*
	 * fetchProjectById(Integer x) method
	 *  - One parameter: Integer projectId.
	 *  - Returns the result of the fetchProjectById(Integer projectId) method 
	 *    in the ProjectDao.
	 *  - Returns a NoSuchElementException is no project in the DB has the given project ID.
	 */
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> 
			new NoSuchElementException("Project with project ID = " + 
				projectId + " does not exist."));
	}

	/*
	 * modifyProjectDetails(Project x) method
	 *  - One parameter: Project project.
	 *  - Used to access the modifyProjectDetails() method in the ProjectDao.
	 *  - If no project is returned from the ProjectDao method, a DbException with the message
	 *    "Project with project ID ? does not exist." 
	 */
	public void modifyProjectDetails(Project project) {
		if (!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() 
				+ "does not exist."); 
		}
	}

	/*
	 * deleteProject(Integer x) method
	 *  - Calls the deleteProject() method from ProjectDao.
	 *  - Checks the returned boolean.
	 *     - If false, message returned saying Project ID doesn't exist.
	 */
	public void deleteProject(Integer projectId) {
		if (!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	}

}
