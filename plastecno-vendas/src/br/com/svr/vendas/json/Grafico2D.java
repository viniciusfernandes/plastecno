package br.com.svr.vendas.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Grafico2D {
    @XmlElement(name = "dados")
    private List<Double> listaDado = new ArrayList<>();

    @XmlElement(name = "labels")
    private List<String> listaLabel = new ArrayList<>();

    private String titulo;

    public Grafico2D(String titulo) {
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

    public void setListaDado(List<Double> listaDado) {
        this.listaDado = listaDado;
    }

    public void setListaLabel(List<String> listaLabel) {
        this.listaLabel = listaLabel;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
