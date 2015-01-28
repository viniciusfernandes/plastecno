package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

public class MasterDetail {
	private final Object value;
	private final List<Object[]> details = new ArrayList<Object[]>();
	private final int columns;
	private int count = 0;

	public MasterDetail(Object value, int columns) {
		this.value = value;
		this.columns = columns;
	}

	public void addDetail(int col, String value) {
		if (details.isEmpty()) {
			details.add(new String[columns]);
		}

		Object[] detail = details.get(count);
		if (detail == null) {
			detail = new String[columns];
			details.add(detail);
		}
		detail[col] = value;
	}

	public List<Object[]> getDetails() {
		return details;
	}

	public Object getValue() {
		return value;
	}

	public void next() {
		count++;
	}
}
