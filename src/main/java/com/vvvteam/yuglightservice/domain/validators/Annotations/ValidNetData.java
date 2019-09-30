package com.vvvteam.yuglightservice.domain.validators.Annotations;


import com.vvvteam.yuglightservice.domain.validators.NetDataValueMatchValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NetDataValueMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNetData {
    String message() default "Fields values don't match!";
    String field();
    int max();
    int min();
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ValidNetData[] value();
    }
}

