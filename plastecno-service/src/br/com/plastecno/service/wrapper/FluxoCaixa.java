package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.DateUtils;

class Fluxo implements Cloneable {
	private final int ano;
	private final int mes;
	private final int dia;
	private double valPagamento;
	private double valDuplicata;
	private double valCredICMS;
	private TipoPagamento tipoPagamento;
	private double valFluxo;
	private Calendar dtVencimento;

	public Fluxo(Calendar dtVencimento, double valPagamento, TipoPagamento tipoPagamento, double valCredICMS,
			double valDuplicata) {
		this.dtVencimento = dtVencimento;
		this.ano = this.dtVencimento.get(Calendar.YEAR);
		this.mes = this.dtVencimento.get(Calendar.MONTH);
		this.dia = this.dtVencimento.get(Calendar.DAY_OF_MONTH);
		this.tipoPagamento = tipoPagamento;
		this.valCredICMS = valCredICMS;
		this.valPagamento = valPagamento;
		this.valDuplicata = valDuplicata;
		calcularValoFluxo();
	}

	public void adicionar(double valPagamento, double valCredICMS, double valDuplicata) {
		this.valPagamento += valPagamento;
		this.valCredICMS += valCredICMS;
		this.valDuplicata += valDuplicata;
		calcularValoFluxo();
	}

	private void calcularValoFluxo() {
		valFluxo = (this.valDuplicata - this.valPagamento) + this.valCredICMS;
	}

	public int getAno() {
		return ano;
	}

	public int getDia() {
		return dia;
	}

	public Calendar getDtVencimento() {
		return dtVencimento;
	}

	public int getMes() {
		return mes;
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public double getValCredICMS() {
		return valCredICMS;
	}

	public double getValDuplicata() {
		return valDuplicata;
	}

	public double getValFluxo() {
		return valFluxo;
	}

	public double getValPagamento() {
		return valPagamento;
	}

}

public class FluxoCaixa {

	private Date dataFinal;
	private Date dataInicial;

	private List<Integer> lAno = new ArrayList<>();

	private List<Integer> lMes = new ArrayList<>();

	private List<Fluxo> lFluxo = new ArrayList<>();

	public FluxoCaixa(Periodo periodo) {
		super();
		this.dataInicial = periodo.getInicio();
		this.dataFinal = periodo.getFim();

		if (dataInicial == null || dataFinal == null) {
			throw new IllegalStateException("As datas de inicio e fim do fluxo de caixa nao podem ser nulas.");
		}
	}

	public void addDuplicata(Date dtVencimento, Double valor) throws BusinessException {

		if (!isDataVencimentoValida(dtVencimento)) {
			throw new BusinessException(
					"A data de vencimento da duplicata esta fora do periodo definido para o fluxo de caixa");
		}
		addFluxo(new Fluxo(DateUtils.gerarCalendarioSemHorario(dtVencimento), 0, null, 0, valor));
	}

	public void addFluxo(Fluxo fluxo) {
		if (fluxo == null) {
			return;
		}
		if (fluxo.getDtVencimento() == null) {
			throw new IllegalStateException("A data eh necessaria para inserir o fluxo de caixa.");
		}
		lAno.add(fluxo.getAno());
		lMes.add(fluxo.getMes());
		lFluxo.add(fluxo);
	}

	public void addPagamento(Date dtVencimento, Double valor, TipoPagamento tipoPagamento, double valorCredICMS)
			throws BusinessException {

		if (!isDataVencimentoValida(dtVencimento)) {
			throw new BusinessException("A data de pagamento esta fora do periodo definido para o fluxo de caixa");
		}
		addFluxo(new Fluxo(DateUtils.gerarCalendarioSemHorario(dtVencimento), valor, tipoPagamento, valorCredICMS, 0));
	}

	public List<Fluxo> gerarFluxoByAno() {
		Map<Integer, Fluxo> mapAno = new HashMap<>();
		List<Fluxo> lFluxoAno = new ArrayList<>();
		Fluxo f = null;
		for (Integer ano : lAno) {
			for (Fluxo fluxo : lFluxo) {
				if (fluxo.getAno() != ano.intValue()) {
					continue;
				}

				if ((f = mapAno.get(ano)) == null) {
					f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
							fluxo.getValCredICMS(), fluxo.getValDuplicata());
					mapAno.put(ano, f);
					lFluxoAno.add(f);
				} else {
					f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
				}
			}

		}
		return lFluxoAno;
	}
	
	public List<Fluxo> gerarFluxoByAnoxx() {
		Map<Integer, Fluxo> mapAno = new HashMap<>();
		List<Fluxo> lFluxoAno = new ArrayList<>();
		Fluxo f = null;
		Integer ano = null;
			for (Fluxo fluxo : lFluxo) {
				ano=fluxo.getAno();

				if ((f = mapAno.get(ano)) == null) {
					f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
							fluxo.getValCredICMS(), fluxo.getValDuplicata());
					mapAno.put(ano, f);
					lFluxoAno.add(f);
				} else {
					f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
				}

		}
		return lFluxoAno;
	}

	public List<Fluxo> gerarFluxoByMes() {
		Map<Integer, Map<Integer, Fluxo>> mapMes = new HashMap<>();
		List<Fluxo> lFluxoMes = new ArrayList<>();
		Fluxo f = null;
		for (Integer ano : lAno) {
			for (Integer mes : lMes) {
				for (Fluxo fluxo : lFluxo) {
					if (fluxo.getAno() != ano.intValue() || fluxo.getMes() != mes.intValue()) {
						continue;
					}

					if (!mapMes.containsKey(ano) || (f = mapMes.get(ano).get(mes)) == null) {
						f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
								fluxo.getValCredICMS(), fluxo.getValDuplicata());
						
						Map<Integer, Fluxo> m = new HashMap<>();
						m.put(mes, f);
						mapMes.put(ano, m);

						lFluxoMes.add(f);
						continue;
					}
					f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
				}
			}
		}
		return lFluxoMes;
	}
	
	public List<Fluxo> gerarFluxoByDia() {
		Map<Integer, Map<Integer, Map<Integer, Fluxo>>> mapDia= new HashMap<>();
		List<Fluxo> lFluxoMes = new ArrayList<>();
		Fluxo f = null;
		for (Integer ano : lAno) {
			for (Integer mes : lMes) {
				for (Fluxo fluxo : lFluxo) {
					if (fluxo.getAno() != ano.intValue() || fluxo.getMes() != mes.intValue()) {
						continue;
					}

					if (!mapDia.containsKey(ano) ||!mapDia.containsKey(mes) || (f = mapDia.get(ano).get(mes)) == null) {
						f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
								fluxo.getValCredICMS(), fluxo.getValDuplicata());
						
						Map<Integer, Fluxo> m = new HashMap<>();
						m.put(mes, f);
						mapMes.put(ano, m);

						lFluxoMes.add(f);
						continue;
					}
					f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
				}
			}
		}
		return lFluxoMes;
	}

	private boolean isDataVencimentoValida(Date dtVencimento) {
		return dtVencimento != null && (dtVencimento.after(dataInicial) && dtVencimento.before(dataFinal));

	}
}
