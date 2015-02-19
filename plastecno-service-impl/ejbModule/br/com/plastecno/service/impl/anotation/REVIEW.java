package br.com.plastecno.service.impl.anotation;

public @interface REVIEW {
	String data() default "";

	String descricao() default "";
}
