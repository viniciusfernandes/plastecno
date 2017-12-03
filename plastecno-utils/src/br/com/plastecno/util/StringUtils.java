package br.com.plastecno.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtils {

	private static final String DATA_AMERICANO_PATTERN = "yyyy-MM-dd";

	private static final String DATA_HORA_PATTERN = "dd/MM/yyyy HH:mm:ss";

	private static final String DATA_PATTERN = "dd/MM/yyyy";

	private static final SimpleDateFormat FORMATADOR_DATA = new SimpleDateFormat(DATA_PATTERN);

	private static final SimpleDateFormat FORMATADOR_DATA_AMERICANO = new SimpleDateFormat(DATA_AMERICANO_PATTERN);

	private static final SimpleDateFormat FORMATADOR_DATA_HORA = new SimpleDateFormat(DATA_HORA_PATTERN);

	private static final SimpleDateFormat FORMATADOR_DATA_HORA_TIMEZONE = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXXX");
	private static final SimpleDateFormat FORMATADOR_HORA = new SimpleDateFormat("HH:mm");

	public static String formatarCNPJ(String conteudo) {
		if (conteudo == null) {
			return "";
		}

		final String[] conteudoArray = conteudo.replaceAll("\\D", "").split("");
		final StringBuilder documento = new StringBuilder();

		final int idxBloco1 = 1;
		final int idxBloco2 = 4;
		final int idxBloco3 = 7;
		final int idxBloco4 = 11;

		for (int i = 0; i < conteudoArray.length; i++) {
			documento.append(conteudoArray[i]);
			switch (i) {
			case idxBloco1:
				documento.append(".");
				break;
			case idxBloco2:
				documento.append(".");
				break;
			case idxBloco3:
				documento.append("/");
				break;
			case idxBloco4:
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

		final int idxBloco1 = 2;
		final int idxBloco2 = 5;
		final int idxBloco3 = 8;
		for (int i = 0; i < conteudoArray.length; i++) {
			documento.append(conteudoArray[i]);
			switch (i) {
			case idxBloco1:
				documento.append(".");
				break;
			case idxBloco2:
				documento.append(".");
				break;
			case idxBloco3:
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

	public static String formatarDataHoraTimezone(Date date) {
		return date == null ? "" : FORMATADOR_DATA_HORA_TIMEZONE.format(date);
	}

	public static String formatarHora(Date date) {
		return date == null ? "" : FORMATADOR_HORA.format(date);
	}

	public static String formatarInscricaoEstadual(String conteudo) {
		if (conteudo == null) {
			return "";
		}

		final String[] conteudoArray = conteudo.replaceAll("\\D", "").split("");
		final StringBuilder documento = new StringBuilder();

		final int posicao3 = 2;
		final int posicao6 = 5;
		final int posicao9 = 8;
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
			default:
				continue;
			}
		}
		return documento.toString();
	}

	public static Date gerarDataHoraTimezone(String date) {
		try {
			return isNotEmpty(date) ? FORMATADOR_DATA_HORA_TIMEZONE.parse(date) : null;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Falha na formatacao da data/hora e timezone", e);
		}
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

	public static Date parsearDataAmericano(String date) throws ParseException {
		return isEmpty(date) ? null : FORMATADOR_DATA_AMERICANO.parse(date);
	}

	public static Date parsearDataHora(String date) throws ParseException {
		return isEmpty(date) ? null : FORMATADOR_DATA_HORA.parse(date);
	}

	public static Date parsearDataHoraTimezone(String date) throws ParseException {
		return isEmpty(date) ? null : FORMATADOR_DATA_HORA_TIMEZONE.parse(date);
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

	public static String removerAcentuacaoESubstituir(String valor, String oldChar, String newChar) {
		if (valor == null) {
			return null;
		}
		if (valor.length() <= 0) {
			return "";
		}
		return Normalizer.normalize(valor, Form.NFD).replaceAll("[^\\p{ASCII}]", "").replace(oldChar, newChar);
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
