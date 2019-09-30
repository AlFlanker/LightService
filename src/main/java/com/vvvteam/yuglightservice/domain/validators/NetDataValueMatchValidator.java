package com.vvvteam.yuglightservice.domain.validators;

import com.vvvteam.yuglightservice.domain.validators.Annotations.ValidNetData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Getter
@Setter
public class NetDataValueMatchValidator implements ConstraintValidator<ValidNetData, Object> {
    private String field;
    private int max;
    private int min;
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        boolean check = false;
        String username,password,token;
        Object fieldValue = wrapper.getPropertyValue(field);
        if (!StringUtils.isEmpty(fieldValue)) {
            if("Vega".equals(fieldValue)){
                username = String.valueOf(wrapper.getPropertyValue("vega_username"));
                password = String.valueOf(wrapper.getPropertyValue("vega_password"));
                if(StringUtils.isEmpty(username)
                        && StringUtils.isEmpty(password)){
                    return false;
                } else {
                    return (username.length() >min) && (username.length() <max) &&
                            (password.length() >min) && (password.length() <max);
                }
            } else if("net868".equals(fieldValue)){
                token = String.valueOf(wrapper.getPropertyValue("net868_token"));
                if(StringUtils.isEmpty(token)){
                    return false;
                } else {
                    return (token.length() >min) && (token.length() <max);

                }
            }

        }
        return !StringUtils.isEmpty(fieldValue);
    }

    @Override
    public void initialize(ValidNetData constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.max = constraintAnnotation.max();
        this.min = constraintAnnotation.min();

    }
}
