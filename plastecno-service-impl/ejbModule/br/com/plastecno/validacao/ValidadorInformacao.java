package br.com.plastecno.validacao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

public final class ValidadorInformacao {
	
	public static void validar (Object obj) throws InformacaoInvalidaException {
		List<String> listaMensagem = new ArrayList<String>(20);
		preencherListaMensagemValidacao(obj, listaMensagem);
		
		if (listaMensagem.size() != 0) {
			throw new InformacaoInvalidaException(listaMensagem);
		}
	}
	
	public static void preencherListaMensagemValidacao (Object obj, List<String> listaMensagem) {
		if(obj.getClass().getAnnotation(InformacaoValidavel.class) == null){
			throw new IllegalArgumentException("A classe "+obj.getClass()+
					" não pode ser validada pelo mecanismo de verificacão de preenchimento dos campos. " +
					"No caso em que o campo seja um ENUM, remova o atributo cascata = true");
		}
		
		//variavel sera utilizada tambem para verificar se o campo eh uma String
		int COMPRIMENTO_STRING = -1;
		Field[] camposValidaveis = recuperarCamposValidaveis(obj);
		
		for (Field campo : camposValidaveis) {
			InformacaoValidavel informacao = campo.getAnnotation(InformacaoValidavel.class);
			
			if (informacao == null) {
				continue;
			}
			
			Object conteudoCampo = recuperarConteudo(campo, obj);
			if (informacao.obrigatorio() && conteudoCampo == null) {
				listaMensagem.add(informacao.nomeExibicao()+" é obrigatório");
				continue;
			}
			
			if ((informacao.relacionamentoObrigatorio() && 
					(conteudoCampo == null || recuperarId(conteudoCampo) == null))) {
				listaMensagem.add(informacao.nomeExibicao()+" deve ser associado");
				continue;
			}
			
			if (informacao.numerico() && informacao.valorNegativo() 
					&& (conteudoCampo != null && Double.valueOf(conteudoCampo.toString()) < 0)) {
				listaMensagem.add(informacao.nomeExibicao()+" não deve ser negativo");
				continue;
			}
			
			if (informacao.numerico() && informacao.valorNaoNegativo() 
					&& (conteudoCampo != null && Double.valueOf(conteudoCampo.toString()) <= 0)) {
				listaMensagem.add(informacao.nomeExibicao()+" deve ser positivo");
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
			
			// Essa variavel sera sempre ZERO no caso em que o conteudo nao seja uma string
			COMPRIMENTO_STRING = conteudoCampo instanceof String ? conteudoCampo.toString().trim().length() : -1;
			if (informacao.intervalo().length > 0 && COMPRIMENTO_STRING >= 0 && (COMPRIMENTO_STRING < informacao.intervalo()[0] || COMPRIMENTO_STRING > informacao.intervalo()[1])) {
				listaMensagem.add(informacao.nomeExibicao()+" deve conter de "+informacao.intervalo()[0]
						+" a "+informacao.intervalo()[1]+" caracteres. Foi enviado "+COMPRIMENTO_STRING+" caracteres");
				continue;
			}
			
			if (informacao.tamanho() >= 0 && COMPRIMENTO_STRING != informacao.tamanho()) {
				listaMensagem.add(informacao.nomeExibicao()+" deve conter apenas "+informacao.tamanho()+" caracteres. Foi enviado "+COMPRIMENTO_STRING+" caracteres");
				continue;
			}
			
			if (!TipoDocumento.NAO_EH_DOCUMENTO.equals(informacao.tipoDocumento()) && COMPRIMENTO_STRING > 0 
					&& !ValidadorDocumento.isValido(informacao.tipoDocumento(), conteudoCampo.toString())) {
				listaMensagem.add(informacao.nomeExibicao()+" não é válido");
				continue;
			}
			
			if (COMPRIMENTO_STRING > 0 && informacao.padrao().length() > 0 && !conteudoCampo.toString().matches(informacao.padrao())) {
				listaMensagem.add(informacao.nomeExibicao()+" não está no formato correto. Exemplo: teste@gmail.com");
				continue;
			}
			
			if(informacao.cascata() && conteudoCampo != null) {
				preencherListaMensagemValidacao(conteudoCampo, listaMensagem);
			}
		}
	}
	
	private static Field[] recuperarCamposValidaveis(Object obj) {
		final InformacaoValidavel informacao = obj.getClass().getAnnotation(InformacaoValidavel.class);
		final Field[] camposClasse = obj.getClass().getDeclaredFields();

		if (informacao.validarHierarquia()) {
			Field[] camposSuperClasse = obj.getClass().getSuperclass()
					.getDeclaredFields();
			Field[] campos = new Field[camposClasse.length + camposSuperClasse.length];

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
			campo.setAccessible(false);
			return conteudoCampo;
		} catch (Exception e) {
			throw new IllegalStateException("O valor do campo "+campo.getType()+" do objecto "+obj.getClass()+" não pode ser acessado");
		} 
	}
	
	private static Object recuperarId (Object conteudoCampo) {
		try {
			return conteudoCampo.getClass().getMethod("getId").invoke(conteudoCampo, (Object[]) null);
			} catch (Exception e) {
				throw new IllegalStateException("O objeto do tipo "+conteudoCampo.getClass()+" não possui um de acesso ao campo de identificação ID. Implementar um metodo de acesso getId(), mas no caso de ENUM, substitua pelo atributo \"obrigatório\"");
			}
		
	}
}
