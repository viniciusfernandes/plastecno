package br.com.svr.vendas.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Ponto2D {
    private final String label;
    private final Integer x;
    private final Double y;

    public Ponto2D() {
        x = null;
        y = null;
        label = null;
    }

    public Ponto2D(Integer x, Double y) {
        this.x = x;
        this.y = y;
        this.label = null;
    }

    public Ponto2D(Integer x, Double y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}