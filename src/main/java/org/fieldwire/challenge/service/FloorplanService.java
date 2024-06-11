package org.fieldwire.challenge.service;

import lombok.RequiredArgsConstructor;
import org.fieldwire.challenge.exception.ErrorType;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Floorplan;
import org.fieldwire.challenge.model.Project;
import org.fieldwire.challenge.model.dto.FloorplanDto;
import org.fieldwire.challenge.repository.FloorplanRepository;
import org.fieldwire.challenge.repository.ProjectRepository;
import org.fieldwire.challenge.util.ImageUtil;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FloorplanService {

	private final FloorplanRepository floorplanRepository;
	private final ProjectRepository projectRepository;

	public Resource download(String name, String projectName) {
		Floorplan request = new Floorplan();
		request.setName(name);
		request.setProjectName(projectName);

		return ImageUtil.createZip(
				floorplanRepository
					.findByNameAndProjectName(request)
					.map(dto -> {
						dto.setId(null);
						return dto;
					})
					.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "floorplan is not found")));
	}

	@Transactional
	public void create(FloorplanDto floorplanDto, MultipartFile original) {
		Floorplan floorplan = new Floorplan();

		floorplan.setName(floorplanDto.getName());

		Project project = new Project();
		project.setName(floorplanDto.getProjectName());
		if (projectRepository.findByName(project).isEmpty()) {
			throw new ServiceException(ErrorType.NOT_FOUND, "the project does not exist");
		}
		floorplan.setProjectName(Objects.isNull(floorplanDto.getProjectName()) ? original.getOriginalFilename() : floorplanDto.getProjectName());

		if (floorplanRepository.findByNameAndProjectName(floorplan).isPresent()) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "the floorplan already exist");
		}

		try {
			floorplan.setOriginal(original.getBytes());
			floorplan.setThumb(ImageUtil.resizeOriginal(original.getBytes(), 100, 100));
			floorplan.setLarge(ImageUtil.resizeOriginal(original.getBytes(), 2000, 2000));
		} catch (IOException e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "cannot upload floorplan");
		}

		floorplanRepository.create(floorplan);
	}

	@Transactional
	public FloorplanDto updateName(FloorplanDto floorplanDto, String newName) {
		Floorplan request = new Floorplan();
		request.setName(floorplanDto.getName());
		request.setProjectName(floorplanDto.getProjectName());

		Floorplan found = floorplanRepository.findByNameAndProjectName(request)
				.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "the floorplan entity does not exists"));
		request.setName(newName);
		if (floorplanRepository.findByNameAndProjectName(request).isPresent()) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "the name already exists");
		}
		if (found.getName().equals(newName)) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "floorplan names are the same");
		}

		found.setName(newName);
		floorplanDto.setName(newName);

		floorplanRepository.updateName(found);

		return floorplanDto;
	}

	@Transactional
	public FloorplanDto updateProjectName(FloorplanDto floorplanDto, String newProjectName) {
		Floorplan request = new Floorplan();
		request.setName(floorplanDto.getName());
		request.setProjectName(floorplanDto.getProjectName());

		Floorplan found = floorplanRepository.findByNameAndProjectName(request)
				.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "the floorplan entity does not exists"));

		Project project = new Project();
		project.setName(newProjectName);
		if (projectRepository.findByName(project).isEmpty()) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "the project does not exist");
		}
		if (found.getProjectName().equals(newProjectName)) {
			throw new ServiceException(ErrorType.BAD_REQUEST, "project names are the same");
		}

		found.setProjectName(newProjectName);
		floorplanDto.setProjectName(newProjectName);

		floorplanRepository.updateProjectName(found);

		return floorplanDto;
	}

	@Transactional
	public FloorplanDto updateOriginalThumbLarge(FloorplanDto floorplanDto, MultipartFile newOriginal) {
		Floorplan request = new Floorplan();
		request.setName(floorplanDto.getName());
		request.setProjectName(floorplanDto.getProjectName());

		Floorplan found = floorplanRepository.findByNameAndProjectName(request)
				.orElseThrow(() -> new ServiceException(ErrorType.NOT_FOUND, "the floorplan entity does not exists"));

		try {
			if (Arrays.equals(found.getOriginal(), newOriginal.getBytes())) {
				throw new ServiceException(ErrorType.BAD_REQUEST, "images are the same");
			}

			found.setOriginal(newOriginal.getBytes());
			found.setThumb(ImageUtil.resizeOriginal(newOriginal.getBytes(), 100, 100));
			found.setLarge(ImageUtil.resizeOriginal(newOriginal.getBytes(), 2000, 2000));
		} catch (IOException e) {
			throw new ServiceException(ErrorType.INTERNAL_SERVER_ERROR, "cannot update floorplan (original)");
		}

		floorplanRepository.updateOriginalThumbLarge(found);

		return floorplanDto;
	}

	public String delete(String name, String projectName) {
		Floorplan floorplan = new Floorplan();
		floorplan.setName(name);
		floorplan.setProjectName(projectName);

		return floorplanRepository
				.delete(floorplan)
				.getName();
	}
}
