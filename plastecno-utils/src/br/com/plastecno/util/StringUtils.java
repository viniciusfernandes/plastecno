package br.com.plastecno.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtils {

	private static final String DATA_HORA_PATTERN = "dd/MM/yyyy HH:mm:ss";

	private static final String DATA_PATTERN = "dd/MM/yyyy";
	private static final String DATA_PATTERN_AMERICANO = "yyyy-MM-dd";

	private static final SimpleDateFormat FORMATADOR_DATA = new SimpleDateFormat(DATA_PATTERN);

	private static final SimpleDateFormat FORMATADOR_DATA_AMERICANO = new SimpleDateFormat(DATA_PATTERN_AMERICANO);
	private static final SimpleDateFormat FORMATADOR_DATA_HORA = new SimpleDateFormat(DATA_HORA_PATTERN);

	public static String formatarCNPJ(String conteudo) {
		if (conteudo == null) {
			return "";
		}

		final String[] conteudoArray = conteudo.replaceAll("\\D", "").split("");
		final StringBuilder documento = new StringBuilder();

		final int posicao2 = 2;
		final int posicao5 = 5;
		final int posicao8 = 8;
		final int posicao12 = 12;

		for (int i = 0; i < conteudoArray.length; i++) {
			documento.append(conteudoArray[i]);
			switch (i) {
			case posicao2:
				documento.append(".");
				break;
			case posicao5:
				documento.append(".");
				break;
			case posicao8:
				documento.append("/");
				break;
			case posicao12:
				documento.append("-");
				break;
			default:
				continue;
			}
		}
		return documento.toString();
	}

	public static String formatarCPF(String conteudo) {
		if (conteudo == null) {
			return "";
		}

		final String[] conteudoArray = conteudo.replaceAll("\\D", "").split("");
		final StringBuilder documento = new StringBuilder();

		final int posicao3 = 3;
		final int posicao6 = 6;
		final int posicao9 = 9;
		for (int i = 0; i < conteudoArray.length; i++) {
			documento.append(conteudoArray[i]);
			switch (i) {
			case posicao3:
				documento.append(".");
				break;
			case posicao6:
				documento.append(".");
				break;
			case posicao9:
				documento.append("-");
				break;
			default:
				continue;
			}
		}
		return documento.toString();
	}

	public static String formatarData(Date date) {
		return date == null ? "" : FORMATADOR_DATA.format(date);
	}

	public static String formatarDataAmericana(Date date) {
		return date == null ? "" : FORMATADOR_DATA_AMERICANO.format(date);
	}

	public static String formatarDataHora(Date date) {
		return date == null ? "" : FORMATADOR_DATA_HORA.format(date);
	}

	public static String formatarInscricaoEstadual(String conteudo) {
		if (conteudo == null) {
			return "";
		}

		final String[] conteudoArray = conteudo.replaceAll("\\D", "").split("");
		final StringBuilder documento = new StringBuilder();

		final int posicao3 = 3;
		final int posicao6 = 6;
		final int posicao9 = 9;
		final int posicao12 = 12;
		for (int i = 0; i < conteudoArray.length; i++) {
			documento.append(conteudoArray[i]);
			switch (i) {
			case posicao3:
				documento.append(".");
				break;
			case posicao6:
				documento.append(".");
				break;
			case posicao9:
				documento.append(".");
				break;
			case posicao12:
				documento.append(".");
				break;
			default:
				continue;
			}
		}
		return documento.toString();
	}

	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}

	public static Date parsearData(String date) throws ParseException {
		return isEmpty(date) ? null : FORMATADOR_DATA.parse(date);
	}

	public static Date parsearDataHora(String date) throws ParseException {
		return isEmpty(date) ? null : FORMATADOR_DATA_HORA.parse(date);
	}

	public static String removerAcentuacao(String valor) {
		if (valor == null) {
			return null;
		}
		if (valor.length() <= 0) {
			return "";
		}
		return Normalizer.normalize(valor, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String removerMascaraDocumento(String documento) {
		if (documento == null) {
			return null;
		}
		return documento.replaceAll("\\.", "").replace("-", "").replaceAll("/", "");
	}

	private StringUtils() {
	}
}
