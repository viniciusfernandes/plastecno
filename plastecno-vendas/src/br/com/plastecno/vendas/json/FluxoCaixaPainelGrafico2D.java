package br.com.plastecno.vendas.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FluxoCaixaPainelGrafico2D {
    private Grafico2D graficoFaturamentoAnual;
    private Grafico2D graficoFaturamentoMensal;
    private Grafico2D graficoFluxoMensal;
    private Grafico2D graficoPagamentoMensal;
    private Grafico2D graficoTipoPagamento;

    public Grafico2D getGraficoFaturamentoAnual() {
        return graficoFaturamentoAnual;
    }

    public Grafico2D getGraficoFaturamentoMensal() {
        return graficoFaturamentoMensal;
    }

    public Grafico2D getGraficoFluxoMensal() {
        return graficoFluxoMensal;
    }

    public Grafico2D getGraficoPagamentoMensal() {
        return graficoPagamentoMensal;
    }

    public Grafico2D getGraficoTipoPagamento() {
        return graficoTipoPagamento;
    }

    public void setGraficoFaturamentoAnual(Grafico2D graficoFaturamentoAnual) {
        this.graficoFaturamentoAnual = graficoFaturamentoAnual;
    }

    public void setGraficoFaturamentoMensal(Grafico2D graficoFaturamentoMensal) {
        this.graficoFaturamentoMensal = graficoFaturamentoMensal;
    }

    public void setGraficoFluxoMensal(Grafico2D graficoFluxoMensal) {
        this.graficoFluxoMensal = graficoFluxoMensal;
    }

    public void setGraficoPagamentoMensal(Grafico2D graficoPagamentoMensal) {
        this.graficoPagamentoMensal = graficoPagamentoMensal;
    }

    public void setGraficoTipoPagamento(Grafico2D graficoTipoPagamento) {
        this.graficoTipoPagamento = graficoTipoPagamento;
    }
}
