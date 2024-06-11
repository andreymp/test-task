package org.fieldwire.challenge.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.fieldwire.challenge.exception.ErrorMessage;
import org.fieldwire.challenge.model.dto.FloorplanDto;
import org.fieldwire.challenge.service.FloorplanService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/floorplan")
@RequiredArgsConstructor
@Api(value = "Floorplan Controller")
public class FloorplanController {

	private final FloorplanService floorplanService;

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 404, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Get the floorplan's files (original, thumb, large)")
	@GetMapping("/{project-name}/{name}")
	public ResponseEntity<Resource> get(@PathVariable(name = "project-name") String projectName, @PathVariable String name) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", String.format("attachment; filename=%s.zip", name))
				.body(floorplanService.download(name, projectName));
	}

	@ApiResponses(value = {
			@ApiResponse(code = 201, message = ""),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Create floorplan")
	@PostMapping(value = "/create")
	public ResponseEntity<Void> create(@RequestBody MultipartFile file, @ApiParam(required = true) FloorplanDto dto) {
		floorplanService.create(dto, file);
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
	@ApiOperation(value = "Update the floorplan's name")
	@PatchMapping("/{project-name}/{name}/name")
	public ResponseEntity<FloorplanDto> editName(@PathVariable(name = "project-name") String projectName,
												 @PathVariable String name,
												 @RequestParam String newName) {
		FloorplanDto dto = new FloorplanDto();
		dto.setProjectName(projectName);
		dto.setName(name);
		return ResponseEntity
				.ok(floorplanService.updateName(dto, newName));
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 400, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 404, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Update the floorplan's project name")
	@PatchMapping("/{project-name}/{name}/project-name")
	public ResponseEntity<FloorplanDto> editProjectName(@PathVariable(name = "project-name") String projectName,
												 @PathVariable String name,
												 @RequestParam String newProjectName) {
		FloorplanDto dto = new FloorplanDto();
		dto.setProjectName(projectName);
		dto.setName(name);
		return ResponseEntity
				.ok(floorplanService.updateProjectName(dto, newProjectName));
	}

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 400, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 404, message = "", response = ErrorMessage.class),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Update the floorplan's files (original, thumb, large)")
	@PatchMapping("/{project-name}/{name}/original")
	public ResponseEntity<FloorplanDto> editOriginal(@PathVariable(name = "project-name") String projectName,
													 @PathVariable String name,
													 @RequestParam @ApiParam MultipartFile file) {
		FloorplanDto dto = new FloorplanDto();
		dto.setProjectName(projectName);
		dto.setName(name);
		return ResponseEntity
				.ok(floorplanService.updateOriginalThumbLarge(dto, file));
	}


	@ApiResponses(value = {
			@ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 500, message = "", response = ErrorMessage.class)
	})
	@ApiOperation(value = "Delete the floorplan")
	@DeleteMapping("/{project-name}/{name}/delete")
	public ResponseEntity<String> delete(@PathVariable(name = "project-name") String projectName, @PathVariable String name) {
		return ResponseEntity
				.ok(floorplanService.delete(name, projectName));
	}
}
