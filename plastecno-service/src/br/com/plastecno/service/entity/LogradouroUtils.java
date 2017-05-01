package br.com.plastecno.service.entity;

public class LogradouroUtils {

	static String gerarDescricao(Logradouravel l, boolean codificado) {
		return codificado ? gerarDescricaoLogradouroCodificado(l) : gerarDescricaoLogradouroNaoCodificado(l);
	}

	private static String gerarDescricaoLogradouroCodificado(Logradouravel l) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(l.getCep()).append(" - ")
				.append(l.getEndereco());
		if (l.getNumero() != null) {
			logradouro.append(", No. ").append(l.getNumero());
		}

		logradouro.append(" - ").append(l.getComplemento() != null ? l.getComplemento() : "").append(" - ")
				.append(l.getBairro()).append(" - ").append(l.getCidade()).append(" - ").append(l.getUf())
				.append(" - ").append(l.getPais());

		return logradouro.toString();
	}

	private static String gerarDescricaoLogradouroNaoCodificado(Logradouravel l) {
		StringBuilder logradouro = new StringBuilder().append(" CEP: ").append(l.getCep()).append(" - ")
				.append(l.getComplemento() != null ? l.getComplemento() : "");
		if (l.getNumero() != null) {
			logradouro.append(", No. ").append(l.getNumero());
		}

		logradouro.append(" - ").append(l.getBairro()).append(" - ").append(l.getCidade()).append(" - ")
				.append(l.getUf()).append(" - ").append(l.getPais());

		return logradouro.toString();
	}

	private LogradouroUtils() {
	}
}
