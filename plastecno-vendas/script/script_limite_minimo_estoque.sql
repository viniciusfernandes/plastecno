drop table IF EXISTS vendas.tb_limite_minimo_estoque cascade ;
alter table vendas.tb_item_estoque drop column if exists id_limite_minimo_estoque;

alter table vendas.tb_item_estoque add quantidade_minima integer default null;
alter table vendas.tb_item_estoque add margem_minima_lucro  numeric(2, 2) default null;
alter table vendas.tb_item_estoque add preco_minimo numeric(9, 2) default null;