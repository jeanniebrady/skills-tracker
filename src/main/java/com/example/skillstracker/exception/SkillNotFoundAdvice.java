package com.example.skillstracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SkillNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(SkillNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String personNotFoundHandler(SkillNotFoundException ex) {
    return ex.getMessage();
  }
}
