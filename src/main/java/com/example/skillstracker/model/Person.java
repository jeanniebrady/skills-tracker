package com.example.skillstracker.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeanniebrady
 *
 * Person entity 
 * 
 * @implNote Annotation JsonIgnoreProperties with hibernateLazyInitializer 
 * property was required to avoid HATEOAS JSON recursion StackOverflow
 * for GET responses with PersonSKills
 */
@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Person {

	private @Id
    @GeneratedValue
    Long id;
	
	@NotBlank
    private String firstName;
	
	@NotBlank
    private String lastName;
	
	@NotBlank
    private String role;

    @JoinTable(
        name = "PS_JT",
        joinColumns = @JoinColumn(
                name = "PERS_ID",
                referencedColumnName = "ID"
        ),
        inverseJoinColumns = @JoinColumn(
                name = "PSKILL_ID",
                referencedColumnName = "ID"
        )
    )
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PersonSkill> personSkills = new HashSet<>();

    public Person(String firstName, String lastName, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}