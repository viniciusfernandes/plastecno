package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.plastecno.service.wrapper.exception.AgrupamentoException;

public class RelatorioPedidoPeriodo {
    private final Agrupamento<VendedorWrapper, RepresentadaValorWrapper> agrupamento;

    public RelatorioPedidoPeriodo(String titulo) {
        this.agrupamento = new Agrupamento<VendedorWrapper, RepresentadaValorWrapper>(titulo);
    }

    public void addValor(String nomeVendedor, RepresentadaValorWrapper venda) throws AgrupamentoException {
        this.agrupamento.addSubgrupo(nomeVendedor, venda, VendedorWrapper.class);
    }

    public List<RepresentadaValorWrapper> getListaVendaRepresentada() {
        final Map<String, Double> mapaValor = new HashMap<String, Double>();
        Double valor = null;
        for (VendedorWrapper vendedor : this.agrupamento.getListaGrupo()) {
            for (Grupo venda : vendedor.getListaRepresentada()) {
                valor = mapaValor.get(venda.getNome());
                mapaValor.put(venda.getNome(), valor != null ? valor + venda.getValor() : venda.getValor());
            }
        }

        final List<RepresentadaValorWrapper> listaVenda = new ArrayList<RepresentadaValorWrapper>();
        Set<Entry<String, Double>> entrySet = mapaValor.entrySet();
        for (Entry<String, Double> entry : entrySet) {
            listaVenda.add(new RepresentadaValorWrapper(entry.getKey(), entry.getValue()));
        }

        return listaVenda;
    }

    public List<VendedorWrapper> getListaVendedor() {
        return this.agrupamento.getListaGrupo();
    }

    public String getTitulo() {
        return this.agrupamento.getTitulo();
    }

    public String getValorTotalVendido() {
        return this.agrupamento.getValorTotalAgrupadoFormatado();
    }
}
