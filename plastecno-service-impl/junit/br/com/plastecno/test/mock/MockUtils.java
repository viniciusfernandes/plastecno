package br.com.plastecno.test.mock;

import java.util.Calendar;
import java.util.Date;

public class MockUtils {
	public static Date gerarDataAnterior() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -1);
		return data.getTime();
	}

	public static Date gerarDataPosterior() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, 1);
		return data.getTime();
	}

	private MockUtils() {
	}
}
