package com.example.skillstracker.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.skillstracker.model.Person;
import com.example.skillstracker.model.Skill;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SkillsTrackerIntegrationTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();

	private static final ObjectMapper om = new ObjectMapper();

	@BeforeEach
	public void setUp() throws Exception {
		// TODO Data for integration tests should be loaded here
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

	@Test
	public void testGETPerson() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/people/1"), HttpMethod.GET, entity,
				String.class);

		String expected = "{\"id\":1,\"firstName\":\"Alberto\",\"lastName\":\"Alessi\",\"role\":\"Head Coach\",\"personSkills\":[{\"id\":12,\"skill\":{\"id\":7,\"name\":\"Karate\"},\"skillLevel\":\"Expert\"}],\"_links\":{\"self\":{\"href\":\"http://localhost:"
				+ port + "/people/1\"},\"people\":{\"href\":\"http://localhost:" + port + "/people\"}}}";
		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	public void testGETPerson_404NotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/people/999"), HttpMethod.GET,
				entity, String.class);

		assertThat(response.getBody()).isEqualTo("Could not find person 999");
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	public void testGETPerson_BadRequest() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/peope/9"), HttpMethod.GET, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	public void testGETSkill() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/skills/7"), HttpMethod.GET, entity,
				String.class);

		String expected = "{\"id\":7,\"name\":\"Karate\",\"_links\":{\"self\":{\"href\":\"http://localhost:" + port
				+ "/skills/7\"},\"skills\":{\"href\":\"http://localhost:" + port + "/skills\"}}}";
		JSONAssert.assertEquals(expected, response.getBody(), false);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	public void testGETSkill_404NotFound() throws Exception {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/skills/999"), HttpMethod.GET,
				entity, String.class);

		String expected = "Could not find skill 999";
		assertThat(response.getBody()).isEqualTo(expected);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	public void testCreatePerson() throws Exception {
		String personAsString = om.writeValueAsString(new Person("Test", "Person", "Tester"));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(personAsString, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/people"), HttpMethod.POST, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

/// TODO Fix this test!
//    @Test
//    public void testCreateSkilledPerson() throws Exception {
//    	
//    	// get a skill with an id
//    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//          createURLWithPort("/skills/7"), HttpMethod.GET, entity, String.class);
//    	
//        EntityModel<Skill> ems = new EntityModel<Skill>(new Skill(""),new Link("http://localhost"));
//        EntityModel<Skill> existingSkill = om.readValue(response.getBody(), ems.getClass());
//        
//    	Person personNewSwimCoach = new Person("SuperSwimmer", "SkilledPerson", "Swim Coach");
//    	personNewSwimCoach.getPersonSkills().add(new PersonSkill(existingSkill.getContent(), SkillLevel.Expert));
//    	String personAsString = om.writeValueAsString(personNewSwimCoach);
//    	
//    	headers.setContentType(MediaType.APPLICATION_JSON);
//        entity = new HttpEntity<String>(personAsString, headers);
//        response = restTemplate.exchange(
//          createURLWithPort("/people"), HttpMethod.POST, entity, String.class);
//        
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }

	@Test
	public void testCreateSkill() throws Exception {
		String skillAsString = om.writeValueAsString(new Skill("TestSkill"));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(skillAsString, headers);
		ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/skills"), HttpMethod.POST, entity,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void contextLoads() {
	}

}