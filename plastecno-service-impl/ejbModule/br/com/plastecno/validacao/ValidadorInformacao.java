package br.com.plastecno.validacao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.validacao.Validavel;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.util.NumeroUtils;

public final class ValidadorInformacao {

	public static void preencherListaMensagemValidacao(Object obj,
			List<String> listaMensagem) {
		if (obj.getClass().getAnnotation(InformacaoValidavel.class) == null) {
			throw new IllegalArgumentException(
					"A classe "
							+ obj.getClass()
							+ " não pode ser validada pelo mecanismo de verificacão de preenchimento dos campos. "
							+ "No caso em que o campo seja um ENUM, remova o atributo cascata = true");
		}

		// variavel sera utilizada tambem para verificar se o campo eh uma
		// String
		int COMPRIMENTO_STRING = -1;
		Field[] camposValidaveis = recuperarCamposValidaveis(obj);
		boolean isString = false;
		int[] tamanhos = null;
		int[] valores = null;
		boolean ok = false;
		for (Field campo : camposValidaveis) {
			InformacaoValidavel informacao = campo
					.getAnnotation(InformacaoValidavel.class);

			if (informacao == null) {
				continue;
			}

			Object conteudoCampo = recuperarConteudo(campo, obj);
			if (informacao.obrigatorio() && conteudoCampo == null) {
				listaMensagem.add(informacao.nomeExibicao() + " é obrigatório");
				continue;
			}

			if ((informacao.relacionamentoObrigatorio() && (conteudoCampo == null || recuperarId(conteudoCampo) == null))) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " deve ser associado");
				continue;
			}

			if (informacao.numerico()
					&& informacao.estritamentePositivo()
					&& (conteudoCampo != null && Double.valueOf(conteudoCampo
							.toString()) <= 0)) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " deve ser positivo");
				continue;
			}

			if (informacao.numerico()
					&& informacao.intervaloNumerico().length > 0
					&& (conteudoCampo != null
							&& (Double) conteudoCampo >= informacao
									.intervaloNumerico()[0] && (Double) conteudoCampo <= informacao
							.intervaloNumerico()[1])) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " deve estar dentro o intervalo "
						+ informacao.intervaloNumerico()[0] + " à "
						+ informacao.intervaloNumerico()[1]);

				continue;
			}

			if (informacao.numerico()
					&& informacao.positivo()
					&& (conteudoCampo != null && Double.valueOf(conteudoCampo
							.toString()) < 0)) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " não deve ser negativo");
				continue;
			}

			if (informacao.iteravel()) {
				Collection<?> lista = (Collection<?>) conteudoCampo;
				if (lista != null) {
					for (Object object : lista) {
						preencherListaMensagemValidacao(object, listaMensagem);
					}
				}
				continue;
			}

			// Essa variavel sera sempre ZERO no caso em que o conteudo nao seja
			// uma
			// string
			isString = conteudoCampo instanceof String;

			if (isString && informacao.trim()) {
				conteudoCampo = trim(campo, obj, conteudoCampo);
			}

			COMPRIMENTO_STRING = isString ? conteudoCampo.toString().trim()
					.length() : -1;
			if (informacao.intervaloComprimento().length > 0
					&& COMPRIMENTO_STRING >= 0
					&& (COMPRIMENTO_STRING < informacao.intervaloComprimento()[0] || COMPRIMENTO_STRING > informacao
							.intervaloComprimento()[1])) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " deve conter de "
						+ informacao.intervaloComprimento()[0] + " a "
						+ informacao.intervaloComprimento()[1]
						+ " caracteres. Foi enviado " + COMPRIMENTO_STRING
						+ " caracteres");
				continue;
			}

			if (conteudoCampo != null && informacao.tamanho() >= 0
					&& COMPRIMENTO_STRING != informacao.tamanho()) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " deve conter apenas " + informacao.tamanho()
						+ " caracteres. Foi enviado " + COMPRIMENTO_STRING
						+ " caracteres");
				continue;
			}

			tamanhos = informacao.tamanhos();
			if (tamanhos.length > 0) {
				ok = false;
				for (int i = 0; i < tamanhos.length; i++) {
					if (COMPRIMENTO_STRING == tamanhos[i]) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					listaMensagem.add(informacao.nomeExibicao()
							+ " deve conter um dos tamanhos \""
							+ Arrays.toString(tamanhos)
							+ "\" mas contém o tamanho de \""
							+ COMPRIMENTO_STRING + "\"");
				}
				continue;
			}

			valores = informacao.valores();
			if (valores.length > 0) {
				ok = false;
				for (int i = 0; i < valores.length; i++) {
					if (conteudoCampo.equals(valores[i])) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					listaMensagem.add(informacao.nomeExibicao()
							+ " deve conter um dos valores \""
							+ Arrays.toString(valores)
							+ "\" mas contém o valores de \"" + conteudoCampo
							+ "\"");
				}
				continue;
			}

			if (informacao.decimal().length >= 2 && conteudoCampo != null) {
				campo.setAccessible(true);
				try {
					campo.set(obj, NumeroUtils.arredondar(
							(Double) conteudoCampo, informacao.decimal()[1]));
				} catch (Exception e) {
					listaMensagem
							.add("Falha no arredondamento decimal do campo "
									+ campo);
				} finally {
					campo.setAccessible(false);
				}
				continue;
			}

			if (!TipoDocumento.NAO_EH_DOCUMENTO.equals(informacao
					.tipoDocumento())
					&& COMPRIMENTO_STRING > 0
					&& !ValidadorDocumento.isValido(informacao.tipoDocumento(),
							conteudoCampo.toString())) {
				listaMensagem.add(informacao.nomeExibicao() + " não é válido");
				continue;
			}

			if (COMPRIMENTO_STRING > 0 && informacao.padrao().length() > 0
					&& !conteudoCampo.toString().matches(informacao.padrao())) {
				listaMensagem.add(informacao.nomeExibicao()
						+ " não está no formato padronizado correto"
						+ (informacao.padraoExemplo().length() > 0 ? " \""
								+ informacao.padraoExemplo() + "\"" : "")
						+ ". Enviado \"" + conteudoCampo + "\"");
				continue;
			}

			if (conteudoCampo != null && conteudoCampo instanceof Validavel) {
				try {
					((Validavel) conteudoCampo).validar();
				} catch (BusinessException e) {
					listaMensagem.addAll(e.getListaMensagem());
				}
				continue;
			}

			if (informacao.cascata() && conteudoCampo != null) {
				preencherListaMensagemValidacao(conteudoCampo, listaMensagem);
			}
		}
	}

	private static Field[] recuperarCamposValidaveis(Object obj) {
		final InformacaoValidavel informacao = obj.getClass().getAnnotation(
				InformacaoValidavel.class);
		final Field[] camposClasse = obj.getClass().getDeclaredFields();

		if (informacao.validarHierarquia()) {
			Field[] camposSuperClasse = obj.getClass().getSuperclass()
					.getDeclaredFields();
			Field[] campos = new Field[camposClasse.length
					+ camposSuperClasse.length];

			for (int i = 0; i < camposClasse.length; i++) {
				campos[i] = camposClasse[i];
			}

			int k = camposClasse.length;
			for (int i = 0; i < camposSuperClasse.length; i++) {
				campos[i + k] = camposSuperClasse[i];

			}

			return campos;
		} else {
			return camposClasse;
		}
	}

	private static Object recuperarConteudo(Field campo, Object obj) {

		try {
			campo.setAccessible(true);
			Object conteudoCampo = campo.get(obj);
			return conteudoCampo;
		} catch (Exception e) {
			throw new IllegalStateException("O valor do campo "
					+ campo.getName() + " do objeto " + obj.getClass()
					+ " não pode ser acessado", e);
		} finally {
			campo.setAccessible(false);
		}
	}

	private static Object recuperarId(Object conteudoCampo) {
		try {
			return conteudoCampo.getClass().getMethod("getId")
					.invoke(conteudoCampo, (Object[]) null);
		} catch (Exception e) {
			throw new IllegalStateException(
					"O objeto do tipo "
							+ conteudoCampo.getClass()
							+ " não possui um de acesso ao campo de identificação ID. Implementar um metodo de acesso getId(), mas no caso de ENUM, substitua pelo atributo \"obrigatório\"");
		}

	}

	private static Object trim(Field campo, Object obj, Object conteudoCampo) {
		conteudoCampo = conteudoCampo.toString().trim();
		try {
			campo.setAccessible(true);
			campo.set(obj, conteudoCampo);
		} catch (Exception e) {
			throw new IllegalStateException(
					"O valor do campo "
							+ campo.getName()
							+ " do objeto "
							+ obj.getClass()
							+ " nao pode ter os espacos em branco removidos cujo conteudo eh \""
							+ conteudoCampo + "\"", e);
		} finally {
			campo.setAccessible(false);
		}
		return conteudoCampo;
	}

	public static void validar(Object obj) throws InformacaoInvalidaException {
		List<String> listaMensagem = new ArrayList<String>(20);
		preencherListaMensagemValidacao(obj, listaMensagem);

		if (listaMensagem.size() != 0) {
			throw new InformacaoInvalidaException(listaMensagem);
		}
	}
}
