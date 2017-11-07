package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.DateUtils;
import br.com.plastecno.util.StringUtils;

public class FluxoCaixa {

	private Date dataFinal;
	private Date dataInicial;

	private List<Integer> lAno = new ArrayList<>();

	private List<Fluxo> lFluxo = new ArrayList<>();

	private List<Integer> lMes = new ArrayList<>();

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
			throw new BusinessException("A data de vencimento " + StringUtils.formatarData(dtVencimento)
					+ " da duplicata com valor " + valor + " esta fora do periodo definido para o fluxo de caixa");
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
			throw new BusinessException("A data de vencimento " + StringUtils.formatarData(dtVencimento)
					+ " do pagamento com valor " + valor + " esta fora do periodo definido para o fluxo de caixa");
		}
		addFluxo(new Fluxo(DateUtils.gerarCalendarioSemHorario(dtVencimento), valor, tipoPagamento, valorCredICMS, 0));
	}

	public List<Fluxo> gerarFluxoByAno() {
		Map<Integer, Fluxo> mapAno = new HashMap<>();
		List<Fluxo> lFluxoAno = new ArrayList<>();
		Fluxo f = null;
		Integer ano = null;
		for (Fluxo fluxo : lFluxo) {
			ano = fluxo.getAno();
			if ((f = mapAno.get(ano)) == null) {
				f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
						fluxo.getValCredICMS(), fluxo.getValDuplicata());
				mapAno.put(ano, f);
				lFluxoAno.add(f);
				continue;
			}
			f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
		}
		ordernar(lFluxoAno);
		return lFluxoAno;
	}

	public List<Fluxo> gerarFluxoByDia() {
		Map<Integer, Fluxo[][]> mapDia = new HashMap<>();
		List<Fluxo> lFluxoDia = new ArrayList<>();
		Fluxo f = null;
		Integer ano = null;
		Integer mes = null;
		Integer dia = null;
		for (Fluxo fluxo : lFluxo) {
			ano = fluxo.getAno();
			mes = fluxo.getMes();
			dia = fluxo.getDia() - 1;

			if (!mapDia.containsKey(ano)) {
				mapDia.put(ano, new Fluxo[12][31]);
			}

			if ((f = mapDia.get(ano)[mes][dia]) == null) {
				f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
						fluxo.getValCredICMS(), fluxo.getValDuplicata());

				mapDia.get(ano)[mes][dia] = f;
				lFluxoDia.add(f);
				continue;
			}
			f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
		}
		ordernar(lFluxoDia);
		return lFluxoDia;
	}

	public List<Fluxo> gerarFluxoByMes() {
		Map<Integer, Fluxo[]> mapMes = new HashMap<>();
		List<Fluxo> lFluxoMes = new ArrayList<>();
		Fluxo f = null;
		Integer ano = null;
		Integer mes = null;
		for (Fluxo fluxo : lFluxo) {
			ano = fluxo.getAno();
			mes = fluxo.getMes();
			if (!mapMes.containsKey(ano)) {
				// Criamos um array com o total de meses do ano.
				mapMes.put(ano, new Fluxo[12]);
			}

			if ((f = mapMes.get(ano)[mes]) == null) {
				f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
						fluxo.getValCredICMS(), fluxo.getValDuplicata());
				mapMes.get(ano)[mes] = f;
				lFluxoMes.add(f);
				continue;
			}
			f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), fluxo.getValDuplicata());
		}

		ordernar(lFluxoMes);
		return lFluxoMes;
	}

	public List<Fluxo> gerarFluxoByTipoPagamento() {
		Map<TipoPagamento, Fluxo> mapTipo = new HashMap<>();
		List<Fluxo> lFluxoPag = new ArrayList<>();
		Fluxo f = null;
		TipoPagamento tpPag = null;
		for (Fluxo fluxo : lFluxo) {
			tpPag = fluxo.getTipoPagamento();
			if (tpPag == null) {
				continue;
			}

			if ((f = mapTipo.get(tpPag)) == null) {
				f = new Fluxo(fluxo.getDtVencimento(), fluxo.getValPagamento(), fluxo.getTipoPagamento(),
						fluxo.getValCredICMS(), fluxo.getValDuplicata());
				mapTipo.put(tpPag, f);
				lFluxoPag.add(f);
				continue;
			}
			f.adicionar(fluxo.getValPagamento(), fluxo.getValCredICMS(), 0d);
		}
		return lFluxoPag;
	}

	private boolean isDataVencimentoValida(Date dtVencimento) {
		return dtVencimento != null
				&& (dtVencimento.compareTo(dataInicial) >= 0 && dtVencimento.compareTo(dataFinal) <= 0);

	}

	private void ordernar(List<Fluxo> lFluxo) {
		Collections.sort(lFluxo, new Comparator<Fluxo>() {
			@Override
			public int compare(Fluxo o1, Fluxo o2) {
				return o1.getDtVencimento().compareTo(o2.getDtVencimento());
			}
		});
	}
}
