package br.com.plastecno.service.wrapper;

import java.util.List;

import br.com.plastecno.service.wrapper.exception.AgrupamentoException;

public final class RelatorioVendaVendedorByRepresentada {

    private Agrupamento<RepresentadaWrapper, VendaClienteWrapper> agrupamento;

    public RelatorioVendaVendedorByRepresentada(String titulo) {
        this.agrupamento = new Agrupamento<RepresentadaWrapper, VendaClienteWrapper>(titulo);
    }

    public void addRepresentada(String nomeRepresentada, VendaClienteWrapper venda) throws AgrupamentoException {
        this.agrupamento.addSubgrupo(nomeRepresentada, venda, RepresentadaWrapper.class);
    }

    public List<RepresentadaWrapper> getListaRepresentada() {
        return this.agrupamento.getListaGrupo();
    }

    public double getTotalVendido() {
        return agrupamento.getValorTotalAgrupado();
    }

    public String getTotalVendidoFormatado() {
        return agrupamento.getValorTotalAgrupadoFormatado();
    }

    public String getTitulo() {
        return this.agrupamento.getTitulo();
    }
}
