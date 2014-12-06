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
	boolean iteravel() default false;
	String nomeExibicao() default "";
	boolean numerico() default false;
	boolean obrigatorio() default false;
	String padrao() default "";
	boolean relacionamentoObrigatorio() default false;
	int tamanho() default -1;
	TipoDocumento tipoDocumento() default TipoDocumento.NAO_EH_DOCUMENTO;
	boolean validarHierarquia() default false;
	boolean valorNaoNegativo() default true;
	boolean valorNegativo() default true;
}
