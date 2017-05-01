package br.com.plastecno.service.entity;

public class LogradouroUtils {

	static String gerarDescricao(Logradouro l, boolean codificado) {
		return codificado ? gerarDescricaoLogradouroCodificado(l) : gerarDescricaoLogradouroNaoCodificado(l);
	}

	private static String gerarDescricaoLogradouroCodificado(Logradouro l) {
		return gerarDescricaoLogradouroCodificado(l.getBairro(), l.getCep(), l.getCidade(), l.getComplemento(),
				l.getEndereco(), l.getNumero(), l.getPais(), l.getUf());
	}

	private static String gerarDescricaoLogradouroCodificado(String bairro, String cep, String cidade,
			String complemento, String endereco, String numero, String pais, String uf) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(cep).append(" - ").append(endereco);
		if (numero != null) {
			logradouro.append(", No. ").append(numero);
		}

		logradouro.append(" - ").append(complemento != null ? complemento : "").append(" - ").append(bairro)
				.append(" - ").append(cidade).append(" - ").append(uf).append(" - ").append(pais);

		return logradouro.toString();
	}

	private static String gerarDescricaoLogradouroNaoCodificado(Logradouro l) {
		return gerarDescricaoLogradouroNaoCodificado(l.getBairro(), l.getCep(), l.getCidade(), l.getComplemento(),
				l.getEndereco(), l.getNumero(), l.getPais(), l.getUf());
	}

	private static String gerarDescricaoLogradouroNaoCodificado(String bairro, String cep, String cidade,
			String complemento, String endereco, String numero, String pais, String uf) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(cep).append(" - ")
				.append(complemento != null ? complemento : "");
		if (numero != null) {
			logradouro.append(", No. ").append(numero);
		}

		logradouro.append(" - ").append(bairro).append(" - ").append(bairro).append(" - ").append(uf).append(" - ")
				.append(pais);

		return logradouro.toString();
	}

	private LogradouroUtils() {
	}
}
