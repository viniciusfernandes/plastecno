package br.com.plastecno.service.validacao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.plastecno.service.constante.RegexValidacao;
import br.com.plastecno.service.constante.TipoDocumento;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface InformacaoValidavel {
	boolean cascata() default false;

	int[] decimal() default {};

	boolean estritamentePositivo() default false;

	String fromPadrao() default "";

	int[] intervaloComprimento() default {};

	double[] intervaloNumerico() default {};

	boolean iteravel() default false;

	String nomeExibicao() default "";

	boolean numerico() default false;

	boolean obrigatorio() default false;

	String padrao() default "";

	String padraoData() default "";

	String padraoExemplo() default "";

	boolean positivo() default false;

	RegexValidacao regex() default RegexValidacao.NENHUM;

	boolean relacionamentoObrigatorio() default false;

	int tamanho() default -1;

	int[] tamanhos() default {};

	TipoDocumento tipoDocumento() default TipoDocumento.NAO_EH_DOCUMENTO;

	String toPadrao() default "";

	boolean trim() default false;

	boolean validarHierarquia() default false;

	int[] valoresInteiros() default {};
}
