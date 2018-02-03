package br.com.svr.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

	public static List<Date> gerarListaDataParcelamento(Date dataBase, Integer... dias) {
		List<Date> lData = new ArrayList<>();
		if (dataBase == null || dias == null || dias.length == 0) {
			return lData;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dataBase);
		for (Integer dia : dias) {
			if (dia == null) {
				continue;
			}
			cal.add(Calendar.DAY_OF_MONTH, dia);
			lData.add(cal.getTime());

			// Retornando a data atual para somar os outros dias corridos e
			// evitar criar outros objetos Calendar.
			cal.add(Calendar.DAY_OF_MONTH, -dia);
		}
		return lData;
	}

	public static List<Date> gerarListaDataParcelasIguais(Date dataBase, int totalParcelas) {
		List<Date> lData = new ArrayList<>();
		if (dataBase == null) {
			return lData;
		}

		lData.add(dataBase);
		if (totalParcelas <= 1) {
			return lData;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dataBase);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int last = 0;
		do {
			cal.add(Calendar.MONTH, 1);
			if (day > (last = cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
				cal.set(Calendar.DAY_OF_MONTH, last);
			} else {
				cal.set(Calendar.DAY_OF_MONTH, day);
			}
			lData.add(cal.getTime());
		} while (--totalParcelas > 1);
		return lData;
	}

	public static boolean isAnterior(Date inicio, Date fim) {
		if (inicio == null || fim == null) {
			throw new IllegalArgumentException("Ambas as datas inicio e fim devem ser preenchidas para a comparacao");
		}

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

	public static Date retrocederData(Date dt) {
		return retrocederData(dt, 1, 0);
	}

	public static Date retrocederData(Date dt, int dias, int meses) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.MONTH, -dias);
		c.add(Calendar.DAY_OF_MONTH, -meses);
		return c.getTime();
	}

	private DateUtils() {
	}
}
