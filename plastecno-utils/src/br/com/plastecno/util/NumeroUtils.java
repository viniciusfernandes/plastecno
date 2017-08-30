package br.com.plastecno.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumeroUtils {
	public static final int CEM = 100;

	public static final int ESCALA_MONETARIA = 2;

	public static final DecimalFormat FORMATADOR_DECIMAL = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(
			new Locale("pt", "BR")));

	public static Double arredondar(Double valor, Integer escala) {
		if (valor == null) {
			return null;
		}
		return new BigDecimal(valor).setScale(escala, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static Float arredondar(Float valor, Integer escala) {
		if (valor == null) {
			return null;
		}
		return new BigDecimal(valor).setScale(escala, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static Double arredondarValorMonetario(Double valor) {
		return arredondar(valor, ESCALA_MONETARIA);
	}

	public static String formatarPercentual(Double decimal, int escala) {
		return String.valueOf(gerarPercentual(decimal, escala));
	}

	public static String formatarPercentualInteiro(Double decimal) {
		return String.valueOf(gerarPercentualInteiro(decimal).intValue());
	}

	public static String formatarValorMonetario(Double valor) {
		return valor == null ? null : FORMATADOR_DECIMAL.format(valor);
	}

	public static Double gerarAliquota(Double percentual) {
		return gerarAliquota(percentual, 2);
	}

	public static Double gerarAliquota(Double percentual, int escala) {
		if (percentual == null) {
			return 0d;
		}
		return arredondar(percentual / CEM, escala);
	}

	public static Double gerarPercentual(Double decimal, int escala) {
		if (decimal == null) {
			return 0.00;
		}
		return arredondar(decimal * CEM, escala);
	}

	public static Double gerarPercentualInteiro(Double decimal) {
		return gerarPercentual(decimal, 0);
	}

	public static double normalizarPercentual(Double percentual) {
		if (percentual == null) {
			return 0.00;
		}
		return arredondar(percentual, ESCALA_MONETARIA);
	}

	private NumeroUtils() {
	}

}
