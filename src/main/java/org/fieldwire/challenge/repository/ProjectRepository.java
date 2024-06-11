package org.fieldwire.challenge.repository;

import liquibase.pro.packaged.O;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fieldwire.challenge.exception.ErrorType;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Floorplan;
import org.fieldwire.challenge.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class ProjectRepository {

	private final JdbcTemplate jdbcTemplate;
	private final FloorplanRepository floorplanRepository;
	private final RowMapper<Project> projectRowMapper;

	@Autowired
	public ProjectRepository(JdbcTemplate jdbcTemplate, FloorplanRepository floorplanRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.floorplanRepository = floorplanRepository;
		this.projectRowMapper = (rs, rowNum) -> {
			Project project = new Project();
			project.setId(rs.getLong("id"));
			project.setName(rs.getString("name"));

			Floorplan floorplan = new Floorplan();
			floorplan.setProjectName(project.getName());
			project.setFloorplans(this.floorplanRepository.findByProjectName(floorplan));
			return project;
		};
	}

	public Optional<Project> findByName(Project project) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM project WHERE name = ?", projectRowMapper, project.getName()));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to find project entity by name");
		}
	}

	public List<Project> findAll() {
		try {
			return jdbcTemplate.query("SELECT * FROM project", projectRowMapper);
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to find project entity by name");
		}
	}
	public Project create(Project project) {
		try {
			jdbcTemplate.update("INSERT INTO project (name) VALUES (?)", project.getName());
			return project;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to create project entity");
		}
	}

	public Project updateName(Project project) {
		try	{
			jdbcTemplate.update("UPDATE project SET name = ? WHERE id = ?", project.getName(), project.getId());
			floorplanRepository.updateProjectNamesAll(project.getFloorplans());
			return project;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to update the project entity (name)");
		}
	}

	public Project deleteByName(Project project) {
		try {
			jdbcTemplate.update("DELETE FROM project WHERE name = ?", project.getName());
			floorplanRepository.deleteAll(Objects.isNull(project.getFloorplans()) ? Collections.emptyList() : project.getFloorplans());
			return project;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to delete project entity");
		}
	}
}
