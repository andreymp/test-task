package org.fieldwire.challenge.serivice;

import com.google.common.collect.Lists;
import org.fieldwire.challenge.BaseTest;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Project;
import org.fieldwire.challenge.model.dto.ProjectRequestDto;
import org.fieldwire.challenge.model.dto.ProjectResponseDto;
import org.fieldwire.challenge.repository.ProjectRepository;
import org.fieldwire.challenge.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ProjectServiceTest extends BaseTest {

	@Mock
	ProjectRepository projectRepository;

	@InjectMocks
	ProjectService projectService;

	@Test
	void getAllTest() {
		Project project1 = new Project();
		project1.setName("project1");
		Project project2 = new Project();
		project2.setName("project2");
		Project project3 = new Project();
		project3.setName("project3");

		when(projectRepository.findAll())
				.thenReturn(Lists.newArrayList(project1, project2, project3));

		ProjectResponseDto responseDto1 = new ProjectResponseDto();
		responseDto1.setName(project1.getName());
		ProjectResponseDto responseDto2 = new ProjectResponseDto();
		responseDto2.setName(project2.getName());
		ProjectResponseDto responseDto3 = new ProjectResponseDto();
		responseDto3.setName(project3.getName());


		assertThat(projectService.getAll())
				.asList()
				.hasSize(3)
				.containsOnly(responseDto1, responseDto2, responseDto3);
	}

	@Test
	void getTest() {
		Project project1 = new Project();
		project1.setName("project1");
		Project project2 = new Project();
		project2.setName("project2");

		when(projectRepository.findByName(project1))
				.thenReturn(Optional.of(project1));
		when(projectRepository.findByName(project2))
				.thenThrow(ServiceException.class);

		ProjectRequestDto requestDto1 = new ProjectRequestDto();
		requestDto1.setName(project1.getName());
		ProjectResponseDto responseDto1 = new ProjectResponseDto();
		responseDto1.setName(project1.getName());
		ProjectRequestDto requestDto2 = new ProjectRequestDto();
		requestDto2.setName(project2.getName());

		assertThat(projectService.get(requestDto1))
				.isEqualTo(responseDto1);
		assertThatThrownBy(() -> projectService.get(requestDto2))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void updateNameTest() {
		Project project1 = new Project();
		project1.setName("project1");
		Project project2 = new Project();
		project2.setName("project2");

		when(projectRepository.findByName(project1))
				.thenReturn(Optional.of(project1));
		when(projectRepository.findByName(project2))
				.thenThrow(ServiceException.class);

		ProjectResponseDto responseDto1 = new ProjectResponseDto();
		responseDto1.setName("new_project_1");

		Project project3 = new Project();
		project3.setName("new_project_1");
		when(projectRepository.updateName(project3))
				.thenReturn(project3);

		ProjectRequestDto requestDto1 = new ProjectRequestDto();
		requestDto1.setName("project1");
		ProjectRequestDto requestDto2 = new ProjectRequestDto();
		requestDto2.setName("project2");

		assertThat(projectService.updateName(requestDto1, "new_project_1"))
				.isEqualTo(responseDto1);
		assertThatThrownBy(() -> projectService.updateName(requestDto2, "new_project_1"))
				.isInstanceOf(ServiceException.class);
		assertThatThrownBy(() -> projectService.updateName(requestDto1, requestDto1.getName()))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void deleteTest() {
		Project project1 = new Project();
		project1.setName("project1");

		when(projectRepository.deleteByName(project1))
				.thenReturn(project1);

		assertThat(projectService.delete("project1"))
				.isEqualTo("project1");
	}
}
