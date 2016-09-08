package br.com.plastecno.service.validacao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.plastecno.service.constante.TipoDocumento;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface InformacaoValidavel {
	String campoCondicional() default "";

	boolean cascata() default false;

	boolean customizado() default false;

	int[] decimal() default {};

	boolean estritamentePositivo() default false;

	String fromPadrao() default "";

	int[] intervaloComprimento() default {};

	double[] intervaloNumerico() default {};

	boolean iteravel() default false;

	String nomeExibicao() default "";

	String nomeExibicaoCampoCondicional() default "";

	boolean numerico() default false;

	boolean obrigatorio() default false;

	String[] opcoes() default {};

	String[] padrao() default {};

	String padraoExemplo() default "";

	boolean positivo() default false;

	boolean relacionamentoObrigatorio() default false;

	String[] substituicao() default {};

	int tamanho() default -1;

	int[] tamanhos() default {};

	TipoDocumento tipoDocumento() default TipoDocumento.NAO_EH_DOCUMENTO;

	String[] tiposNaoPermitidos() default {};

	String[] tiposObrigatorios() default {};

	String[] tiposPermitidos() default {};

	String toPadrao() default "";

	boolean trim() default false;

	boolean validarHierarquia() default false;

}
