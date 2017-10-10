package br.com.plastecno.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

	public static Calendar gerarCalendario(Date data) {

		if (data == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		return calendar;
	}

	public static Calendar gerarCalendarioSemHorario(Date data) {

		if (data == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Date gerarDataAmanha() {
		Calendar c = gerarCalendarioSemHorario(new Date());
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	public static Date gerarDataAtualSemHorario() {
		return gerarCalendarioSemHorario(new Date()).getTime();
	}

	public static Date gerarDataOntem() {
		Calendar c = gerarCalendarioSemHorario(new Date());
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	public static Date gerarDataSemHorario(Date data) {
		return data != null ? gerarCalendarioSemHorario(data).getTime() : null;
	}

	public static boolean isAnterior(Date inicio, Date fim) {
		if (inicio == null || fim == null) {
			throw new IllegalArgumentException("Ambas as datas inicio e fim devem ser preenchidas para a comparacao");
		}

		inicio = gerarDataSemHorario(inicio);
		fim = gerarDataSemHorario(fim);
		return inicio.compareTo(fim) < 0;
	}

	public static boolean isAnteriorDataAtual(Date inicio) {
		return isAnterior(inicio, new Date());
	}

	public static boolean isPosteriror(Date inicio, Date fim) {

		if (inicio == null || fim == null) {
			throw new IllegalArgumentException("Ambas as datas inicio e fim devem ser preenchidas para a comparacao");
		}

		inicio = gerarDataSemHorario(inicio);
		fim = gerarDataSemHorario(fim);
		return inicio.compareTo(fim) > 0;
	}

	public static boolean isPosterirorDataAtual(final Date inicio) {
		return isPosteriror(inicio, new Date());
	}

	private DateUtils() {
	}
}
