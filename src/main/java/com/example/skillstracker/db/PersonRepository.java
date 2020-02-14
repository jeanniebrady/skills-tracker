package com.example.skillstracker.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.skillstracker.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
