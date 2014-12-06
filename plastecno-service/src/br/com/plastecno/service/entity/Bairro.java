package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_bairro", schema = "enderecamento")
@InformacaoValidavel
public class Bairro implements Serializable {

    private static final long serialVersionUID = 2897817249870206835L;

    @Id
    @SequenceGenerator(name = "bairroSequence", sequenceName = "enderecamento.seq_bairro_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bairroSequence")
    @Column(name = "id_bairro")
    private Integer id;

    @Column(name = "bairro")
    @InformacaoValidavel(obrigatorio = true, intervalo = { 1, 40 }, nomeExibicao = "Bairro")
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    public Bairro() {
        this.cidade = new Cidade();
    }

    public Bairro(Cidade cidade) {
        this.cidade = cidade;
    }

    /*
     * Construtor utilizado na pesquisa de bairros pelo prefixo do CEP
     */
    public Bairro(Integer idBairro, String descricaoBairro, Integer idCidade, String descricaoCidade) {
        this();
        this.cidade.setId(idCidade);
        this.cidade.setDescricao(descricaoCidade);
        this.id = idBairro;
        this.descricao = descricaoBairro;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
