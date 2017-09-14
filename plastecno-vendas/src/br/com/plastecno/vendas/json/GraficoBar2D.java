package br.com.plastecno.vendas.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GraficoBar2D {
    @XmlElement(name = "dados")
    private List<Double> listaDado = new ArrayList<>();

    @XmlElement(name = "labels")
    private List<String> listaLabel = new ArrayList<>();

    private String titulo;

    public GraficoBar2D(String titulo) {
        super();
        this.titulo = titulo;
    }

    public void adicionar(String label, Double dado) {
        listaLabel.add(label);
        listaDado.add(dado);
    }

    public List<Double> getListaDado() {
        return listaDado;
    }

    public List<String> getListaLabel() {
        return listaLabel;
    }

    public String getTitulo() {
        return titulo;
    }

}
