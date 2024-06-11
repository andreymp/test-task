package org.fieldwire.challenge.serivice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fieldwire.challenge.BaseTest;
import org.fieldwire.challenge.exception.ServiceException;
import org.fieldwire.challenge.model.Floorplan;
import org.fieldwire.challenge.model.dto.FloorplanDto;
import org.fieldwire.challenge.repository.FloorplanRepository;
import org.fieldwire.challenge.repository.ProjectRepository;
import org.fieldwire.challenge.service.FloorplanService;
import org.fieldwire.challenge.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@Slf4j
class FloorplanServiceTest extends BaseTest {
	private static final String TEST_NAME = "TEST_NAME";
	private static final String TEST_PROJECT_NAME = "TEST_PROJECT_NAME";
	private static final String TEST_NEW_NAME = "TEST_NEW_NAME";
	
	@Mock
	FloorplanRepository floorplanRepository;
	
	@Mock
	ProjectRepository projectRepository;

	@InjectMocks
	FloorplanService floorplanService;

	@Test
	@SneakyThrows
	void downloadTest() {
		Floorplan response = new Floorplan();
		response.setName(TEST_NAME);
		response.setProjectName(TEST_PROJECT_NAME);
		try {
			response.setOriginal(Objects.requireNonNull(TestUtil.getImageBytes("/test_img.jpg")));
			response.setThumb(Objects.requireNonNull(TestUtil.getImageBytes("/test_img_thumb.jpg")));
			response.setLarge(Objects.requireNonNull(TestUtil.getImageBytes("/test_img_large.jpg")));
		} catch (Exception e) {
			fail();
		}

		Floorplan request1 = new Floorplan();
		request1.setName(TEST_NAME);
		request1.setProjectName(TEST_PROJECT_NAME);

		Floorplan request2 = new Floorplan();
		request2.setName(TEST_NAME);
		request2.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(request1))
				.thenReturn(Optional.of(response));
		when(floorplanRepository.findByNameAndProjectName(response))
				.thenThrow(SecurityException.class);

		assertThat(floorplanService.download(TEST_NAME, TEST_PROJECT_NAME))
				.isNotNull();
		assertThatThrownBy(() -> floorplanService.download(TEST_NAME, TEST_NAME))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void updateNameTest() {
		Floorplan floorplan1 = new Floorplan();
		floorplan1.setName(TEST_NAME);
		floorplan1.setProjectName(TEST_PROJECT_NAME);
		Floorplan floorplan2 = new Floorplan();
		floorplan2.setName(TEST_PROJECT_NAME);
		floorplan2.setProjectName(TEST_PROJECT_NAME);
		Floorplan floorplan3 = new Floorplan();
		floorplan3.setName(TEST_PROJECT_NAME);
		floorplan3.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(floorplan1))
				.thenReturn(Optional.of(floorplan1));
		when(floorplanRepository.findByNameAndProjectName(floorplan2))
				.thenThrow(ServiceException.class);
		when(floorplanRepository.findByNameAndProjectName(floorplan3))
				.thenReturn(Optional.of(floorplan3));

		Floorplan request1 = new Floorplan();
		request1.setName(TEST_NEW_NAME);
		request1.setProjectName(TEST_PROJECT_NAME);
		Floorplan request3 = new Floorplan();
		request3.setName(TEST_NEW_NAME);
		request3.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(request1))
				.thenReturn(Optional.empty());
		when(floorplanRepository.findByNameAndProjectName(request3))
				.thenThrow(ServiceException.class);

		when(floorplanRepository.updateName(request1))
				.thenReturn(request1);

		FloorplanDto dto1 = new FloorplanDto();
		dto1.setName(TEST_NAME);
		dto1.setProjectName(TEST_PROJECT_NAME);
		FloorplanDto dto2 = new FloorplanDto();
		dto2.setName(TEST_PROJECT_NAME);
		dto2.setProjectName(TEST_PROJECT_NAME);
		FloorplanDto dto3 = new FloorplanDto();
		dto3.setName(TEST_PROJECT_NAME);
		dto3.setProjectName(TEST_NAME);

		FloorplanDto response1 = new FloorplanDto();
		response1.setName(TEST_NEW_NAME);
		response1.setProjectName(TEST_PROJECT_NAME);

		assertThat(floorplanService.updateName(dto1, TEST_NEW_NAME))
				.isEqualTo(response1);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto2, TEST_NEW_NAME))
				.isInstanceOf(ServiceException.class);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto3, TEST_NEW_NAME))
				.isInstanceOf(ServiceException.class);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto1, TEST_NAME))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void updateProjectNameTest() {
		Floorplan floorplan1 = new Floorplan();
		floorplan1.setName(TEST_NAME);
		floorplan1.setProjectName(TEST_PROJECT_NAME);
		Floorplan floorplan2 = new Floorplan();
		floorplan2.setName(TEST_NAME);
		floorplan2.setProjectName(TEST_NAME);
		Floorplan floorplan3 = new Floorplan();
		floorplan3.setName(TEST_PROJECT_NAME);
		floorplan3.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(floorplan1))
				.thenReturn(Optional.of(floorplan1));
		when(floorplanRepository.findByNameAndProjectName(floorplan2))
				.thenThrow(ServiceException.class);
		when(floorplanRepository.findByNameAndProjectName(floorplan3))
				.thenReturn(Optional.of(floorplan3));

		Floorplan request1 = new Floorplan();
		request1.setName(TEST_NAME);
		request1.setProjectName(TEST_NEW_NAME);
		Floorplan request3 = new Floorplan();
		request3.setName(TEST_PROJECT_NAME);
		request3.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(request1))
				.thenReturn(Optional.empty());
		when(floorplanRepository.findByNameAndProjectName(request3))
				.thenThrow(ServiceException.class);

		when(floorplanRepository.updateProjectName(request1))
				.thenReturn(request1);

		FloorplanDto dto1 = new FloorplanDto();
		dto1.setName(TEST_NAME);
		dto1.setProjectName(TEST_PROJECT_NAME);
		FloorplanDto dto2 = new FloorplanDto();
		dto2.setName(TEST_PROJECT_NAME);
		dto2.setProjectName(TEST_PROJECT_NAME);
		FloorplanDto dto3 = new FloorplanDto();
		dto3.setName(TEST_PROJECT_NAME);
		dto3.setProjectName(TEST_NAME);

		FloorplanDto response1 = new FloorplanDto();
		response1.setName(TEST_NEW_NAME);
		response1.setProjectName(TEST_PROJECT_NAME);

		assertThat(floorplanService.updateName(dto1, TEST_NEW_NAME))
				.isEqualTo(response1);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto2, TEST_NEW_NAME))
				.isInstanceOf(ServiceException.class);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto3, TEST_NEW_NAME))
				.isInstanceOf(ServiceException.class);
		assertThatThrownBy(() -> floorplanService.updateProjectName(dto1, TEST_PROJECT_NAME))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void updateOriginalThumbLargeTest() {
		Floorplan floorplan1 = new Floorplan();
		floorplan1.setName(TEST_NAME);
		floorplan1.setProjectName(TEST_PROJECT_NAME);

		Floorplan floorplan2 = new Floorplan();
		floorplan2.setName(TEST_NAME);
		floorplan2.setProjectName(TEST_NAME);

		when(floorplanRepository.findByNameAndProjectName(floorplan1))
				.thenReturn(Optional.of(floorplan1));
		when(floorplanRepository.findByNameAndProjectName(floorplan2))
				.thenThrow(ServiceException.class);

		Floorplan request1 = new Floorplan();
		request1.setName(TEST_NAME);
		request1.setProjectName(TEST_PROJECT_NAME);
		try {
			request1.setOriginal(Objects.requireNonNull(TestUtil.getImageBytes("/test_img.jpg")));
		} catch (Exception e) {
			fail();
		}

		when(floorplanRepository.updateOriginalThumbLarge(request1))
				.thenReturn(request1);

		FloorplanDto dto1 = new FloorplanDto();
		dto1.setName(TEST_NAME);
		dto1.setProjectName(TEST_PROJECT_NAME);
		FloorplanDto dto2 = new FloorplanDto();
		dto2.setName(TEST_NAME);
		dto2.setProjectName(TEST_NAME);

		FloorplanDto response1 = new FloorplanDto();
		response1.setName(TEST_NAME);
		response1.setProjectName(TEST_PROJECT_NAME);


		assertThat(floorplanService.updateOriginalThumbLarge(dto1, new MockMultipartFile("test_img.jpg", TestUtil.getImageBytes("/test_img.jpg"))))
				.isEqualTo(response1);
		assertThatThrownBy(() -> floorplanService.updateOriginalThumbLarge(dto2, new MockMultipartFile("test_img.jpg", TestUtil.getImageBytes("/test_img.jpg"))))
				.isInstanceOf(ServiceException.class);
	}

	@Test
	void deleteTest() {
		Floorplan floorplan = new Floorplan();
		floorplan.setName(TEST_NAME);
		floorplan.setProjectName(TEST_PROJECT_NAME);

		when(floorplanRepository.delete(floorplan))
				.thenReturn(floorplan);

		assertThat(floorplanService.delete(TEST_NAME, TEST_PROJECT_NAME))
				.isEqualTo(TEST_NAME);
	}
}
