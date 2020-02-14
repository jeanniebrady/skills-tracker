package com.example.skillstracker.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.skillstracker.controller.SkillController;

@Component
public
class SkillResourceAssembler implements RepresentationModelAssembler<Skill, EntityModel<Skill>> {

  @Override
  public EntityModel<Skill> toModel(Skill skill) {

    return new EntityModel<>(skill,
      linkTo(methodOn(SkillController.class).one(skill.getId())).withSelfRel(),
      linkTo(methodOn(SkillController.class).all()).withRel("skills"));
  }
}
