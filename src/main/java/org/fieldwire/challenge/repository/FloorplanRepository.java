package org.fieldwire.challenge.repository;

import lombok.RequiredArgsConstructor;
import org.fieldwire.challenge.exception.ErrorType;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Floorplan;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FloorplanRepository {

	private static final String CREATE_SQL = "INSERT INTO floorplan (project_name, name, original, thumb, large) VALUES (?, ?, ?, ?, ?)";
	private static final String DELETE_SQL = "DELETE FROM floorplan WHERE name  = ? AND project_name = ?";
	private static final String UPDATE_PROJECT_NAME_SQL = "UPDATE floorplan SET project_name = ? WHERE id = ?";

	private final JdbcTemplate jdbcTemplate;

	private final RowMapper<Floorplan> floorplanRowMapper = (rs, rowNum) -> {
		Floorplan floorplan = new Floorplan();
		floorplan.setId(rs.getLong("id"));
		floorplan.setProjectName(rs.getString("project_name"));
		floorplan.setName(rs.getString("name"));
		floorplan.setOriginal(rs.getBytes("original"));
		floorplan.setThumb(rs.getBytes("thumb"));
		floorplan.setLarge(rs.getBytes("large"));
		return floorplan;
	};

	public Optional<Floorplan> findByNameAndProjectName(Floorplan floorplan) {
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM floorplan WHERE project_name = ? AND name = ?",
							floorplanRowMapper, floorplan.getProjectName(), floorplan.getName()));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to get floorplan entity by \"project_name\"");
		}
	}

	public List<Floorplan> findByProjectName(Floorplan floorplan) {
		try {
			return jdbcTemplate.query("SELECT * FROM floorplan WHERE project_name = ?", floorplanRowMapper, floorplan.getProjectName());
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to get floorplan entity by \"project_name\"");
		}
	}

	public Floorplan create(Floorplan floorplan) {
		try {
			jdbcTemplate.update(CREATE_SQL, floorplan.getProjectName(), floorplan.getName(), floorplan.getOriginal(), floorplan.getThumb(), floorplan.getLarge());
			return floorplan;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to create floorplan entity");
		}
	}

	public Floorplan updateName(Floorplan floorplan) {
		try	{
			jdbcTemplate.update("UPDATE floorplan SET name = ? WHERE id = ?", floorplan.getName(), floorplan.getId());
			return floorplan;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to update the floorplan entity (project_name)");
		}
	}

	public Floorplan updateProjectName(Floorplan floorplan) {
		try	{
			jdbcTemplate.update(UPDATE_PROJECT_NAME_SQL, floorplan.getProjectName(), floorplan.getId());
			return floorplan;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to update the floorplan entity (project_name)");
		}
	}

	public void updateProjectNamesAll(List<Floorplan> floorplans) {
		try	{
			jdbcTemplate.batchUpdate(UPDATE_PROJECT_NAME_SQL, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Floorplan floorplan = floorplans.get(i);
					ps.setString(1, floorplan.getProjectName());
					ps.setLong(2, floorplan.getId());
				}

				@Override
				public int getBatchSize() {
					return floorplans.size();
				}
			});
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to update the floorplans entities (name) in a batch");
		}
	}

	public Floorplan updateOriginalThumbLarge(Floorplan floorplan) {
		try	{
			jdbcTemplate.update("UPDATE floorplan SET original = ?, thumb = ?, large = ? WHERE id = ?",
					floorplan.getOriginal(), floorplan.getThumb(), floorplan.getLarge(), floorplan.getId());
			return floorplan;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to update the floorplan entity (original, thumb, large)");
		}
	}

	public Floorplan delete(Floorplan floorplan) {
		try {
			jdbcTemplate.update(DELETE_SQL, floorplan.getName(), floorplan.getProjectName());
			return floorplan;
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to delete floorplan entity");
		}
	}

	public void deleteAll(List<Floorplan> floorplans) {
		try {
			jdbcTemplate.batchUpdate(DELETE_SQL, new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, floorplans.get(i).getName());
						ps.setString(2, floorplans.get(i).getProjectName());
					}

					@Override
					public int getBatchSize() {
						return floorplans.size();
					}
				}
			);
		} catch (Exception e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "unable to delete floorplan entities in a batch");
		}
	}
}
