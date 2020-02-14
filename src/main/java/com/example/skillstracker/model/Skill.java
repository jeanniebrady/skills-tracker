package com.example.skillstracker.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Skill {

	  private @Id @GeneratedValue Long id;
	  
	  @NotBlank
	  private String name;
	   
	  public Skill(String name) {
		    this.name = name;   
	  }	
}