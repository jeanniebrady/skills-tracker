package com.example.skillstracker.db;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.skillstracker.model.Person;
import com.example.skillstracker.model.PersonSkill;
import com.example.skillstracker.model.Skill;
import com.example.skillstracker.model.SkillLevel;

/**
 * @author jeanniebrady
 * @implNote This data loads when app starts and is used for
 * example/demo purposes.
 * 
 */
@Configuration
@Slf4j
public class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(PersonRepository personRepo,
			SkillRepository skillsRepo) {
		return args -> {

			// people
			Person personAlberto = personRepo.save(new Person("Alberto", "Alessi", "Head Coach"));
			Person personBruno = personRepo.save(new Person("Bruno", "Baldini", "Coach"));
			Person personCarlo = personRepo.save(new Person("Carlo", "Conti", "Coach"));
			Person personDante = personRepo.save(new Person("Dante", "Dellucci", "Junior Coach"));
			Person personEdelberga = personRepo.save(new Person("Edelberga", "Esposito", "Receptionist"));
			Person personFlavio = personRepo.save(new Person("Flavio", "Ferrari", "Administrator"));

			// skills
			Skill skillKarate = skillsRepo.save(new Skill("Karate"));
			Skill skillBoxing = skillsRepo.save(new Skill("Boxing"));
			Skill skillFirstAid = skillsRepo.save(new Skill("FirstAid"));
			Skill skillSwimming = skillsRepo.save(new Skill("Swimming"));
			Skill skillRunning = skillsRepo.save(new Skill("Running"));

			// Alberto
			personAlberto.getPersonSkills().add(new PersonSkill(skillKarate, SkillLevel.Expert));
			personRepo.save(personAlberto);
			log.info("Loaded " + personAlberto);
			
			// Bruno
			personBruno.getPersonSkills().add(new PersonSkill(skillBoxing,SkillLevel.Awareness));
			personBruno.getPersonSkills().add(new PersonSkill(skillFirstAid,SkillLevel.Expert));
			personRepo.save(personBruno);
			log.info("Loaded " + personBruno);
			
			// Carlo
			personCarlo.getPersonSkills().add(new PersonSkill(skillKarate,SkillLevel.Practitioner));
			personCarlo.getPersonSkills().add(new PersonSkill(skillFirstAid,SkillLevel.Awareness));
			personCarlo.getPersonSkills().add(new PersonSkill(skillBoxing,SkillLevel.Practitioner));
			personRepo.save(personCarlo);
			log.info("Loaded " + personCarlo);
			
			// Dante
			personDante.getPersonSkills().add(new PersonSkill(skillSwimming,SkillLevel.Working));
			personRepo.save(personDante);
			log.info("Loaded " + personDante);

			// Edelberga - no skills :(
			log.info("Loaded " + personEdelberga);

			// Flavio
			personFlavio.getPersonSkills().add(new PersonSkill(skillKarate,SkillLevel.Practitioner));
			personFlavio.getPersonSkills().add(new PersonSkill(skillBoxing,SkillLevel.Practitioner));
			personFlavio.getPersonSkills().add(new PersonSkill(skillSwimming,SkillLevel.Awareness));
			personFlavio.getPersonSkills().add(new PersonSkill(skillFirstAid,SkillLevel.Practitioner));
			personFlavio.getPersonSkills().add(new PersonSkill(skillRunning,SkillLevel.Expert));
			personRepo.save(personFlavio);
			log.info("Loaded " + personFlavio);
		};
	}
}
