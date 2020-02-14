package com.example.skillstracker;

import com.example.skillstracker.controller.PersonController;
import com.example.skillstracker.db.PersonRepository;
import com.example.skillstracker.exception.PersonNotFoundAdvice;
import com.example.skillstracker.model.Person;
import com.example.skillstracker.model.PersonResourceAssembler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PersonController.class)
@ContextConfiguration(classes = {PersonResourceAssembler.class})
public class PersonControllerTest {

    @Autowired
    private ObjectMapper mapper;

    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @Autowired
    private PersonResourceAssembler personResourceAssembler;

    private static String BASE_PATH = "http://localhost/people";
    private static final long ID = 1;
    private Person person = setUpPerson(ID, "testFirstName", "testLastName", "testRole");

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PersonController(personRepository, personResourceAssembler))
                .setControllerAdvice(PersonNotFoundAdvice.class)
                .build();
    }

    private Person setUpPerson(long id, String firstName, String lastName, String role) {
        Person person = new Person();
        person.setId(id);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setRole(role);
        return person;
    }

    private void assertResults(ResultActions result) throws Exception {
        result.andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("firstName", is("testFirstName")))
                .andExpect(jsonPath("lastName", is("testLastName")))
                .andExpect(jsonPath("role", is("testRole")));
    }

    @Test
    public void one() throws Exception {
        given(personRepository.findById(ID)).willReturn(Optional.of(person));
        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));
        result.andExpect(status().isOk());
        assertResults(result);
    }

    @Test
    public void oneWhichDoesntExist() throws Exception {
        given(personRepository.findById(ID)).willReturn(Optional.empty());
        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void all() throws Exception {
        List<Person> skills = Arrays.asList(setUpPerson(ID, "testFirstName ", "testLastName" + ID , "testRole"), setUpPerson(ID+1, "testFirstName ", "testLastName" + (ID + 1), "testRole"));
        given(personRepository.findAll()).willReturn(skills);
        final ResultActions result = mockMvc.perform(get(BASE_PATH));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].lastName", containsInAnyOrder("testLastName1", "testLastName2")))
                .andExpect(jsonPath("$.content[*].links[?(@.rel=='self')].href", containsInAnyOrder(BASE_PATH + "/" + ID, BASE_PATH + "/" + (ID + 1))));
    }

    @Test
    public void newPerson() throws Exception {

        given(personRepository.save(any(Person.class))).willReturn(person);

        final ResultActions result =
                mockMvc.perform(
                        post(BASE_PATH)
                                .content(mapper.writeValueAsBytes(person))
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
        assertResults(result);
    }

    @Test
    public void replacePersonName() throws Exception {
        Person updatedPerson = new Person();
        updatedPerson.setFirstName("Bruce");
        updatedPerson.setLastName("Lee");
        updatedPerson.setRole("Martial artist");
        updatedPerson.setId(ID);

        given(personRepository.save(any(Person.class))).willReturn(updatedPerson);

        final ResultActions result =
                mockMvc.perform(
                        put(BASE_PATH + "/" + ID)
                                .content(mapper.writeValueAsBytes(updatedPerson))
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("firstName", is("Bruce")))
                .andExpect(jsonPath("lastName", is("Lee")))
                .andExpect(jsonPath("role", is("Martial artist")));
    }

    @Test
    public void deletePerson() throws Exception {

        given(personRepository.findById(ID)).willReturn(Optional.of(person));
        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonexistentPerson() throws Exception {
        given(personRepository.findById(ID)).willReturn(Optional.empty());
        mockMvc.perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNotFound());
    }
}

