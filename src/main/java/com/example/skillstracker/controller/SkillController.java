package com.example.skillstracker.controller;

import com.example.skillstracker.db.SkillRepository;
import com.example.skillstracker.exception.SkillNotFoundException;
import com.example.skillstracker.model.Skill;
import com.example.skillstracker.model.SkillResourceAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to provide RESTful CRUD operations for skills
 *
 * @author jeanniebrady
 */
@RestController
public
class SkillController {

    private final SkillRepository repository;

    private final SkillResourceAssembler assembler;

    public SkillController(SkillRepository repository, SkillResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * GET all available skills (Aggregate root)
     *
     * @return
     */
    @GetMapping("/skills")
    public CollectionModel<EntityModel<Skill>> all() {

        List<EntityModel<Skill>> skills = repository.findAll().stream().map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(skills, linkTo(methodOn(SkillController.class).all()).withSelfRel());
    }

    /**
     * GET single skill by id
     *
     * @param id
     * @return
     */
    @GetMapping("/skills/{id}")
    public EntityModel<Skill> one(@PathVariable Long id) {

        Skill skill = repository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

        return assembler.toModel(skill);
    }

    /**
     * POST a new skill
     *
     * @param newSkill
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/skills")
    ResponseEntity<?> newSkill(@RequestBody Skill newSkill) throws URISyntaxException {

        EntityModel<Skill> resource = assembler.toModel(repository.save(newSkill));

        return ResponseEntity.created(MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newSkill.getId()).toUri()).body(resource);

    }

    /**
     * PUT/Alter an existing skill - change the name
     *
     * @param newSkill
     * @param id
     * @return
     */
    @PutMapping("/skills/{id}")
    Skill replaceSkill(@RequestBody Skill newSkill, @PathVariable Long id) {

        return repository.findById(id)
                .map(skill -> {
                    skill.setName(newSkill.getName());
                    return repository.save(newSkill);
                })
                .orElseGet(() -> {
                    newSkill.setId(id);
                    return repository.save(newSkill);
                });
    }

    /**
     * DELETE a skill
     *
     * @param id
     */
    @DeleteMapping("/skills/{id}")
    ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        repository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
