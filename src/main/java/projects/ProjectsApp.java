package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	private Scanner in = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	// @formatter:off
	private List<String> operations = List.of(
			"1.) Add a Project.",
			"2.) List Projects.",
			"3.) Select a Project."
	);
	// @formatter:on

	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();
	}

	/*
	 * processUserSelection() method - No parameters. 
	 * - displays the menu selections, gets selection from user, and then acts on the selection
	 */
	private void processUserSelection() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}
	
	/*
	 * selectProject() method
	 *  - Lists the project IDs and names so user can select a project ID.
	 *     - Once ID entered, service is called to return project details. 
	 *     - If this step is successful, current project is set to the returned project.
	 *  - No return.
	 */
	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a Project ID to select a Project");
		
		curProject = null;
		
		curProject =  projectService.fetchProjectById(projectId);
	}

	/*
	 * listProjects() method
	 *  - Uses the fetchAllProjects() method from ProjectService.java.
	 *  - Prints out the current list of projects.
	 *  - Returns nothing.
	 */
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("   " 
				+ project.getProjectId() + ": " + project.getProjectName()));
		
	}

	/*
	 * createProject() method
	 *  - Private. 
	 *  - No parameters.
	 *  - Returns nothing.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter project name");
		BigDecimal estHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	/*
	 * getDecimalInput() method - One parameter (String prompt). -
	 */
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	/*
	 * exitMenu() method - No parameters. 
	 * - Used when the user wants to exit the application.
	 */
	private boolean exitMenu() {
		System.out.println("Exiting the menu...");
		return true;
	}

	/*
	 * getUserSelection() method - No parameters. 
	 * - prints the operations and then accepts user input as an Integer.
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	/*
	 * getIntInput() method - One parameter (String prompt) 
	 * - accepts user input and converts it to Integer (can be null).
	 */
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	/*
	 * getStringInput() method - One parameter (String prompt) 
	 * - Lowest level input method. 
	 * - Other input methods call this method and convert the input value to the appropriate type.
	 *   - Also be called by methods that need to collect String data from the user.
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = in.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	/*
	 * printOperations() method 
	 * - prints each available selection on a separate line in the console.
	 */
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println(" " + line)); // Prints each line of the operations list.
		
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
