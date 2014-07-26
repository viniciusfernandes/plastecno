package br.com.plastecno.vendas.json;

import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoJson {
    private final Integer id;
    private final Integer idMaterial;
    private final Integer quantidade;
    private final String formaMaterial;
    private final String descricaoPeca;
    private final boolean peca;
    private final boolean vendaKilo;
    private final String siglaMaterial;
    private final Double medidaExterna;
    private final Double medidaInterna;
    private final Double comprimento;
    private final String precoUnidadeIPI;
    private final String precoItem;
    private final Double precoVenda;
    private final String precoUnidade;
    private final String valorPedido;
    private final String valorPedidoIPI;
    private final String aliquotaICMS;
    private final String descricaoItemPedido;

    public ItemPedidoJson(ItemPedido itemPedido) {
        comprimento = itemPedido.getComprimento();
        descricaoPeca = itemPedido.getDescricaoPeca();
        formaMaterial = itemPedido.getFormaMaterial() != null ? itemPedido.getFormaMaterial().toString() : "";
        id = itemPedido.getId();
        idMaterial = itemPedido.getMaterial() != null ? itemPedido.getMaterial().getId() : -1;
        medidaExterna = itemPedido.getMedidaExterna();
        // Caso a barra seja quadrada ambas as medidas serao iguais
        medidaInterna = itemPedido.isMedidaExternaIgualInterna() ? itemPedido.getMedidaExterna() : itemPedido
                .getMedidaInterna();
        peca = itemPedido.isPeca();
        precoUnidadeIPI = itemPedido.getPrecoUnidadeIPIFormatado();
        precoItem = itemPedido.getPrecoItemFormatado();
        precoVenda = itemPedido.getPrecoVenda();
        precoUnidade = itemPedido.getPrecoUnidadeFormatado();
        quantidade = itemPedido.getQuantidade();
        siglaMaterial = itemPedido.getMaterial() != null ? itemPedido.getMaterial().getSigla() : "";
        vendaKilo = itemPedido.isVendaKilo();
        valorPedido = itemPedido.getPedido() != null && itemPedido.getPedido().getValorPedido() != null ? itemPedido
                .getPedido().getValorPedidoFormatado() : "";
        valorPedidoIPI = itemPedido.getPedido() != null && itemPedido.getPedido().getValorPedidoIPI() != null
                ? itemPedido.getPedido().getValorPedidoIPIFormatado() : "";
        descricaoItemPedido = itemPedido.getDescricao();
        aliquotaICMS = itemPedido.getAliquotaICMSFormatado();
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public String getFormaMaterial() {
        return formaMaterial;
    }

    public String getDescricaoPeca() {
        return descricaoPeca;
    }

    public boolean isPeca() {
        return peca;
    }

    public boolean isVendaKilo() {
        return vendaKilo;
    }

    public String getSiglaMaterial() {
        return siglaMaterial;
    }

    public Double getMedidaExterna() {
        return medidaExterna;
    }

    public Double getMedidaInterna() {
        return medidaInterna;
    }

    public Double getComprimento() {
        return comprimento;
    }

    public Double getPrecoVenda() {
        return precoVenda;
    }

    public String getValorPedido() {
        return valorPedido;
    }

    public String getDescricaoItemPedido() {
        return descricaoItemPedido;
    }

    public String getValorPedidoIPI() {
        return valorPedidoIPI;
    }

    public String getPrecoUnidadeIPI() {
        return precoUnidadeIPI;
    }

    public String getAliquotaICMS() {
        return aliquotaICMS;
    }

    public String getPrecoUnidade() {
        return precoUnidade;
    }

    public String getPrecoItem() {
        return precoItem;
    }
    
}
