package pl.jakubmichalowski.kainos;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;
import pl.jakubmichalowski.kainos.controllers.ApiController;
import pl.jakubmichalowski.kainos.exceptions.BadDateFormatException;
import pl.jakubmichalowski.kainos.exceptions.DateOutOfRangeException;
import pl.jakubmichalowski.kainos.exceptions.WrongDateException;
import pl.jakubmichalowski.kainos.jsonMappings.Price;
import pl.jakubmichalowski.kainos.utilities.DataGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KainosApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private DataGenerator dg;

	@Mock
	private ApiController apiController;

	@Mock
	View mockView;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = standaloneSetup(apiController)
				.setSingleView(mockView)
				.build();

	}

	@Test
	public void contextLoads() {
	}

	@Test(expected = WrongDateException.class)
	public void WhenFromDateIsAfterToDateThrowsExcpetion(){

		dg.getData("2018-05-05", "2018-05-04");
	}

	@Test(expected = WrongDateException.class)
	public void WhenFromDateEqualsToDateThrowsExcpetion(){

		dg.getData("2018-05-05", "2018-05-05");
	}

	@Test(expected = BadDateFormatException.class)
	public void WhenDateFormatIsWrongThrowsExcpetion(){

		dg.getData("2018/05/08", "2018/05/10");
	}

	@Test(expected = DateOutOfRangeException.class)
	public void WhenDateOutOfRangeThrowsExcpetion(){

		dg.getData("1950-05-05", "2018-05-05");
	}

	@Test
	public void WhenNoDatesParametersReturn400BadRequest() throws Exception {
		mockMvc.perform(get("/api"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void WhenNoFromDateParametersReturn400BadRequest() throws Exception {
		mockMvc.perform(get("/api?from=2018-05-05"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void WhenNoToDateParametersReturn400BadRequest() throws Exception {
		mockMvc.perform(get("/api?to=2018-05-05"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void WhenDatesArePassedAndAreValidReturnJsonAnd200() throws Exception {
		mockMvc.perform(get("/api?to=2018-05-05&from=2018-05-02"))
				.andExpect(status().isOk());
	}




}
