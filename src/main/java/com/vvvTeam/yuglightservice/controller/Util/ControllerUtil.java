package com.vvvTeam.yuglightservice.controller.Util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class ControllerUtil {
    static  public Map<String, String> getErrorsMap(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream().collect(Collectors.toMap(
                        fieldError -> fieldError.getField()+"error",
                        FieldError::getDefaultMessage
                ));
    }
}
