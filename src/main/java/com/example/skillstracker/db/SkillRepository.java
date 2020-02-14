package com.example.skillstracker.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skillstracker.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

}
