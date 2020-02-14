package com.example.skillstracker.controller;

import com.example.skillstracker.db.PersonRepository;
import com.example.skillstracker.exception.PersonNotFoundException;
import com.example.skillstracker.model.Person;
import com.example.skillstracker.model.PersonResourceAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author jeanniebrady
 * <p>
 * TODO Self generating documentation using Spring Boot
 * https://spring.io/guides/gs/testing-restdocs/ TODO Look at validation
 * in more depth https://www.baeldung.com/spring-boot-bean-validation
 * <p>
 * GETs http://localhost:8080/people
 * http://localhost:8080/people/{personId}
 * <p>
 * DELETEs http://localhost:8080/people http://localhost:8080/people/1
 * <p>
 * POSTs (create) http://localhost:8080/people +JSON
 * <p>
 * PUTs (replace/update) http://localhost:8080/people/1
 */
@RestController
public
class PersonController {

    private final PersonRepository repository;

    private final PersonResourceAssembler assembler;

    public PersonController(PersonRepository repository,
                            PersonResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * Aggregate root - GET all people
     *
     * @return CollectionModel
     */
    @GetMapping("/people")
    public CollectionModel<EntityModel<Person>> all() {

        List<EntityModel<Person>> people = repository.findAll().stream().map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(people, linkTo(methodOn(PersonController.class).all()).withSelfRel());
    }

    /**
     * Single item - GET an individual person
     *
     * @param id identifying the person to be returned
     * @return EntityModel<Person
     */
    @GetMapping("/people/{id}")
    public EntityModel<Person> one(@PathVariable Long id) {

        Optional<Person> byId = repository.findById(id);
        Person person = byId.orElseThrow(() -> new PersonNotFoundException(id));

        return assembler.toModel(person);
    }

    /**
     * Post - Create (POST) a new person
     *
     * @param newPerson
     * @return ResponseEntity<?>
     * @throws URISyntaxException //org.hibernate.exception.ConstraintViolationException
     */
    @PostMapping("/people")
    ResponseEntity<?> newPerson(@Valid @RequestBody Person newPerson) throws URISyntaxException {

        EntityModel<Person> resource = assembler.toModel(repository.save(newPerson));

        return ResponseEntity.created(MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")
                .buildAndExpand(newPerson.getId()).toUri()).body(resource);
    }

    /**
     * Put - Update an existing person
     *
     * @param newPerson
     * @param id        identifying the person to be updated
     * @return Person
     */
    @PutMapping("/people/{id}")
    Person replacePerson(@Valid @RequestBody Person newPerson, @PathVariable Long id) {

        return repository.findById(id).map(person -> {
            person.setFirstName(newPerson.getFirstName());
            person.setLastName(newPerson.getLastName());
            person.setRole(newPerson.getRole());
            return repository.save(person);
        }).orElseGet(() -> {
            newPerson.setId(id);
            return repository.save(newPerson);
        });
    }

    /**
     * Single item - DELETE an individual person
     *
     * @param id identifying the person to be deleted
     */
    @DeleteMapping("/people/{id}")
    ResponseEntity<?> deletePerson(@PathVariable Long id) {
        repository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();

    }

}
