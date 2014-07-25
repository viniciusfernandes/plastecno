package br.com.plastecno.service.validacao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.plastecno.service.constante.TipoDocumento;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InformacaoValidavel {
	boolean cascata() default false;
	int[] intervalo() default {};
	String nomeExibicao() default "";
	boolean obrigatorio() default false;
	boolean relacionamentoObrigatorio() default false;
	boolean iteravel() default false;
	int tamanho() default -1;
	boolean valorNegativo() default true;
	boolean valorNaoNegativo() default true;
	boolean numerico() default false;
	TipoDocumento tipoDocumento() default TipoDocumento.NAO_EH_DOCUMENTO;
	String padrao() default "";
	boolean validarHierarquia() default false;
}
