package org.fieldwire.challenge.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.fieldwire.challenge.exception.ErrorMessage;
import org.fieldwire.challenge.model.dto.ProjectRequestDto;
import org.fieldwire.challenge.model.dto.ProjectResponseDto;
import org.fieldwire.challenge.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Api(value = "Project Controller")
public class ProjectController {

	private final ProjectService projectService;

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Get all projects")
	@GetMapping("/list")
	public ResponseEntity<List<ProjectResponseDto>> getAll() {
		return ResponseEntity
				.ok(projectService.getAll());
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 404, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Get the project")
	@GetMapping("/{name}")
	public ResponseEntity<ProjectResponseDto> get(@PathVariable String name) {
		ProjectRequestDto dto = new ProjectRequestDto();
		dto.setName(name);
		return ResponseEntity
				.ok(projectService.get(dto));
	}

	@ApiResponses(value = {
			@ApiResponse(code = 201, message = ""),
			@ApiResponse(code = 400, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Create the project")
	@PostMapping("/create")
	public ResponseEntity<Void> create(@RequestBody ProjectRequestDto dto) {
		projectService.create(dto);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 400, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 404, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Update the project's name")
	@PatchMapping("/{name}/name")
	public ResponseEntity<ProjectResponseDto> editName(@PathVariable String name,
												 @RequestParam String newName) {
		ProjectRequestDto dto = new ProjectRequestDto();
		dto.setName(name);
		return ResponseEntity
				.ok(projectService.updateName(dto, newName));
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Delete the project")
	@DeleteMapping("/{name}/delete")
	public ResponseEntity<String> delete(@PathVariable String name) {
		return ResponseEntity
				.ok(projectService.delete(name));
	}
}
