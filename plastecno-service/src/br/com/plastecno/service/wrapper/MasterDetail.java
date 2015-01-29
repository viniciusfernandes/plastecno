package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

public class MasterDetail {
	private final Object label;
	private final List<Object[]> details = new ArrayList<Object[]>();
	private final int columns;
	private int currentDetail = -1;

	public MasterDetail(Object label, int detailColumns) {
		this.label = label;
		this.columns = detailColumns;
	}

	public void addDetail(int col, String detailValue) {
		Object[] detail = details.get(currentDetail);
		if (detail == null) {
			detail = new String[columns];
			details.add(detail);
		}
		detail[col] = detailValue;
	}

	public List<Object[]> getDetails() {
		return details;
	}

	public Object getLabel() {
		return label;
	}

	public void nextDetail() {
		details.add(new String[columns]);
		currentDetail++;
	}

	public int getSize() {
		return details.size();
	}
}
