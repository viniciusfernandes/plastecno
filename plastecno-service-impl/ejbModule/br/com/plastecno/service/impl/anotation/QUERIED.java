package br.com.plastecno.service.impl.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(value = { ElementType.CONSTRUCTOR })
public @interface QUERIED {
	Class<Object>[] referencias();
}
