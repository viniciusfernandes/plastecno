package br.com.plastecno.service.impl.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface REVIEW {
	String data() default "";

	String descricao() default "";
}
