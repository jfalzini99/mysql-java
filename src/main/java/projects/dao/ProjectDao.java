package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	/*
	 * insertProjecet() method
	 *  - One parameter (Project project).
	 *  - Insert a project row into the project table.
	 *  - Returns the Project object with the primary key.
	 */
	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
			} catch (Exception e ) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/*
	 * fetchAllProjects() method
	 *  - Handles an SQL statement to retrieve all projects. 
	 *  - If connection and SQL statement do not throw errors, all projects printed. 
	 *  - Returns nothing.
	 */
	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();	
					
					while (rs.next()) {
						/*
						 * What the extract() method does: 
						 * 
						 *	Project project = new Project();
						 *	project.setActualHours(rs.getBigDecimal("actual_hours"));
						 *	project.setDifficulty(rs.getObject("difficulty", Integer.class));
						 * 	project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
						 *  project.setNotes(rs.getString("notes"));
						 *	project.setProjectId(rs.getObject("project_id", Integer.class));
						 *	project.setProjectName(rs.getString("project_name"));
						 */
						
						projects.add(extract(rs, Project.class));
					}
					
					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	/*
	 * fetchProjectById(Integer x) method
	 *  - One parameter(Integer projectId).
	 *  - Retrieves a Project row and all associated child rows.
	 *  - Returns an Optional of type Project.
	 */
	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);
					
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				
				return Optional.ofNullable(project);
				
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/*
	 * fetchMaterialForProject(Connection x, Integer y) method
	 *  - Two parameters; a Connection conn & and an Integer projectId).
	 *  - Returns the Materials for a given Project.
	 *  - Returns a List of type Material.
	 */
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) 
		throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();
				
				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				
				return materials;
			}
		}
	}
	
	/*
	 * fetchStepsForProject(Connection x, Integer y) method
	 *  - Two parameters; a Connection conn & and an Integer projectId).
	 *  - Returns the Steps for a given Project.
	 *  - Returns a List of type Step.
	 */
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) 
		throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();
				
				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				
				return steps;
			}
		}
	}
	
	/*
	 * fetchCategoriesForProject(Connection x, Integer y) method
	 *  - Two parameters; a Connection conn & and an Integer projectId).
	 *  - Returns the Categories for a given Project.
	 *  - Returns a List of type Category.
	 */
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) 
		throws SQLException {
		// @formatter:off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
				+ "WHERE project_id = ?";
		// @formatter:on
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();
				
				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				
				return categories;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
				boolean updated = stmt.executeUpdate() == 1;
				
				commitTransaction(conn);
				return updated;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				
				commitTransaction(conn);
				return deleted;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
}



















