package com.example.skillstracker;

import com.example.skillstracker.db.SkillRepository;
import com.example.skillstracker.exception.SkillNotFoundAdvice;
import com.example.skillstracker.model.Skill;
import com.example.skillstracker.model.SkillResourceAssembler;
import com.example.skillstracker.controller.*;
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
@WebMvcTest(SkillController.class)
@ContextConfiguration(classes = {SkillResourceAssembler.class})
public class SkillControllerTest {

    @Autowired
    private ObjectMapper mapper;

    private MockMvc mockMvc;

    @MockBean
    private SkillRepository skillRepository;

    @Autowired
    private SkillResourceAssembler skillResourceAssembler;

    private static String BASE_PATH = "http://localhost/skills";
    private static final long ID = 1;
    private Skill skill = setUpSkill(ID, "skill ID " + ID);

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SkillController(skillRepository, skillResourceAssembler))
                .setControllerAdvice(SkillNotFoundAdvice.class)
                .build();
    }

    private Skill setUpSkill(long id, String skillName) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(skillName);
        return skill;
    }

    @Test
    public void one() throws Exception {
        given(skillRepository.findById(ID)).willReturn(Optional.of(skill));
        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));
        result.andExpect(status().isOk());
        assertResults(result);
    }

    @Test
    public void oneWhichDoesntExist() throws Exception {
        given(skillRepository.findById(ID)).willReturn(Optional.empty());
        final ResultActions result = mockMvc.perform(get(BASE_PATH + "/" + ID));
        result.andExpect(status().isNotFound());
    }

    private void assertResults(ResultActions result) throws Exception {
        result.andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("name", is("skill ID 1")));
    }

    @Test
    public void all() throws Exception {
        List<Skill> skills = Arrays.asList(setUpSkill(ID, "skill ID " + ID), setUpSkill(ID + 1, "skill ID " + (ID + 1)));
        given(skillRepository.findAll()).willReturn(skills);
        final ResultActions result = mockMvc.perform(get(BASE_PATH));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("skill ID 1", "skill ID 2")))
                .andExpect(jsonPath("$.content[*].links[?(@.rel=='self')].href", containsInAnyOrder(BASE_PATH + "/" + ID, BASE_PATH + "/" + (ID + 1))));
    }

    @Test
    public void newSkill() throws Exception {

        given(skillRepository.save(any(Skill.class))).willReturn(skill);

        final ResultActions result =
                mockMvc.perform(
                        post(BASE_PATH)
                                .content(mapper.writeValueAsBytes(skill))
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
        assertResults(result);
    }

    @Test
    public void replaceSkillName() throws Exception {
        Skill updatedSkill = new Skill();
        updatedSkill.setName("Tai Chi");
        updatedSkill.setId(ID);

        given(skillRepository.save(any(Skill.class))).willReturn(updatedSkill);

        final ResultActions result =
                mockMvc.perform(
                        put(BASE_PATH + "/" + ID)
                                .content(mapper.writeValueAsBytes(updatedSkill))
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("name", is("Tai Chi")));
    }

    @Test
    public void deleteSkill() throws Exception {

        given(skillRepository.findById(ID)).willReturn(Optional.of(skill));
        mockMvc
                .perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteNonexistentSkill() throws Exception {
        given(skillRepository.findById(ID)).willReturn(Optional.empty());
        mockMvc.perform(delete(BASE_PATH + "/" + ID))
                .andExpect(status().isNotFound());
    }
}

