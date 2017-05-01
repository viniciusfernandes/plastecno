package br.com.plastecno.service.entity;

public class LogradouroUtils {

	static String gerarDescricao(Logradouro l, boolean codificado) {
		return codificado ? gerarDescricaoLogradouroCodificado(l) : gerarDescricaoLogradouroNaoCodificado(l);
	}

	static String gerarDescricao(LogradouroCliente l, boolean codificado) {
		return codificado ? gerarDescricaoLogradouroCodificado(l) : gerarDescricaoLogradouroNaoCodificado(l);
	}

	static String gerarDescricao(LogradouroPedido l, boolean codificado) {
		return codificado ? gerarDescricaoLogradouroCodificado(l) : gerarDescricaoLogradouroNaoCodificado(l);
	}

	static String gerarDescricaoLogradouroCodificado(Logradouro l) {
		return gerarDescricaoLogradouroCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	static String gerarDescricaoLogradouroCodificado(LogradouroCliente l) {
		return gerarDescricaoLogradouroCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	static String gerarDescricaoLogradouroCodificado(LogradouroPedido l) {
		return gerarDescricaoLogradouroCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	private static String gerarDescricaoLogradouroCodificado(String cep, String endereco, String numero,
			String complemento, String bairro, String cidade, String uf, String pais) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(cep).append(" - ").append(endereco);
		if (numero != null) {
			logradouro.append(", No. ").append(numero);
		}

		logradouro.append(" - ").append(complemento != null ? complemento : "").append(" - ").append(bairro)
				.append(" - ").append(cidade).append(" - ").append(uf).append(" - ").append(pais);

		return logradouro.toString();
	}

	static String gerarDescricaoLogradouroNaoCodificado(Logradouro l) {
		return gerarDescricaoLogradouroNaoCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	static String gerarDescricaoLogradouroNaoCodificado(LogradouroCliente l) {
		return gerarDescricaoLogradouroNaoCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	static String gerarDescricaoLogradouroNaoCodificado(LogradouroPedido l) {
		return gerarDescricaoLogradouroNaoCodificado(l.getCep(), l.getEndereco(), l.getNumero(), l.getComplemento(),
				l.getBairro(), l.getCidade(), l.getUf(), l.getPais());
	}

	private static String gerarDescricaoLogradouroNaoCodificado(String cep, String endereco, String numero,
			String complemento, String bairro, String cidade, String uf, String pais) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(cep).append(" - ")
				.append(complemento != null ? complemento : "");
		if (numero != null) {
			logradouro.append(", No. ").append(numero);
		}

		logradouro.append(" - ").append(bairro).append(" - ").append(cidade).append(" - ").append(uf).append(" - ")
				.append(pais);

		return logradouro.toString();
	}

	private LogradouroUtils() {
	}
}
