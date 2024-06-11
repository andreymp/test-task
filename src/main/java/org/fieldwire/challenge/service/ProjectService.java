package org.fieldwire.challenge.service;

import lombok.RequiredArgsConstructor;
import org.fieldwire.challenge.exception.ErrorType;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Project;
import org.fieldwire.challenge.model.dto.ProjectRequestDto;
import org.fieldwire.challenge.model.dto.ProjectResponseDto;
import org.fieldwire.challenge.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;

	public List<ProjectResponseDto> getAll() {
		return projectRepository
				.findAll()
				.stream()
				.map(project -> {
					ProjectResponseDto responseDto = new ProjectResponseDto();
					responseDto.setName(project.getName());

					return responseDto;
				})
				.toList();
	}

	public ProjectResponseDto get(ProjectRequestDto requestDto) {
		Project request = new Project();
		request.setName(requestDto.getName());
		return projectRepository
				.findByName(request)
				.map(project -> {
					ProjectResponseDto projectResponseDto = new ProjectResponseDto();
					projectResponseDto.setName(project.getName());

					if (Objects.nonNull(project.getFloorplans())) {
						projectResponseDto.setFloorplans(project
								.getFloorplans()
								.stream()
								.map(floorplan -> {
									ProjectResponseDto.FloorplanResponseDto floorplanResponseDto = new ProjectResponseDto.FloorplanResponseDto();
									floorplanResponseDto.setName(floorplan.getName());

									return floorplanResponseDto;
								})
								.toList()
						);
					}

					return projectResponseDto;
				})
				.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "project is not found"));
	}

	public void create(ProjectRequestDto projectRequestDto) {
		Project request = new Project();
		request.setName(projectRequestDto.getName());

		Project project = new Project();
		project.setName(projectRequestDto.getName());
		if (projectRepository.findByName(project).isPresent()) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "the project already exists");
		}
		projectRepository.create(request);
	}

	@Transactional
	public ProjectResponseDto updateName(ProjectRequestDto projectRequestDto, String newName) {
		Project request = new Project();
		request.setName(projectRequestDto.getName());

		Project found = projectRepository.findByName(request)
				.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "the project entity does not exists"));
		request.setName(newName);
		if (projectRepository.findByName(request).isPresent()) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "the name already exists");
		}
		if (found.getName().equals(newName)) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "project names are the same");
		}
		found.setName(newName);
		projectRepository.updateName(found);

		ProjectResponseDto response = new ProjectResponseDto();
		response.setName(newName);
		return response;
	}

	@Transactional
	public String delete(String name) {
		Project project = new Project();
		project.setName(name);

		return projectRepository
				.deleteByName(project)
				.getName();

	}
}
