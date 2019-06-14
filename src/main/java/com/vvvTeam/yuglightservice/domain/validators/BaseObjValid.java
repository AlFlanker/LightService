package com.vvvTeam.yuglightservice.domain.validators;

import com.vvvTeam.yuglightservice.domain.entries.Lamp;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;
@Component("beforeCreateBaseObjectValidator")
public class BaseObjValid implements Validator{
    @Override
    public boolean supports(Class<?> aClass) {
        return Lamp.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Lamp lamp = (Lamp) o;
        if(!checkEUI(((Lamp) o).getName())){
            errors.reject("name","incorrect value");
        }

    }
    private boolean checkEUI(String eui){
        Pattern pattern = Pattern.compile("(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})");
        return pattern.matcher(eui).matches();
    }
    private boolean checkInputString(String input) {
        return (input == null || input.trim().length() == 0);
    }
}
