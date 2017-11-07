package br.com.plastecno.service.test;

import java.util.Calendar;
import java.util.Date;

public class TestUtils {
	public static Date gerarDataOntem() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, -1);
		return data.getTime();
	}

	public static Date gerarDataAmanha() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.DAY_OF_MONTH, 1);
		return data.getTime();
	}

	private TestUtils() {
	}
}
