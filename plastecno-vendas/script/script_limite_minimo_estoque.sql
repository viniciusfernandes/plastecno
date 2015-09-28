drop table IF EXISTS vendas.tb_limite_minimo_estoque cascade ;
alter table vendas.tb_item_estoque drop column if exists id_limite_minimo_estoque;

alter table vendas.tb_item_estoque add quantidade_minima integer default null;
alter table vendas.tb_item_estoque add margem_minima_lucro numeric(5, 2) default null;
alter table vendas.tb_item_pedido add preco_minimo numeric(9, 2) default null;

alter table vendas.tb_comissao rename column valor to aliquota_revenda;
alter table vendas.tb_comissao add aliquota_representacao numeric(2, 2) default null;

alter table vendas.tb_item_pedido add valor_comissionado_representacao numeric(7,2) default null;

alter table vendas.tb_comissao alter column aliquota_representacao type numeric(5, 4) ;