alter table vendas.tb_item_pedido add peso numeric(9,2) default 0;
alter table vendas.tb_pedido add observacao_producao varchar(800) default null;

insert into vendas.tb_situacao_pedido (id, descricao) values (12, 'ORCAMENTO DIGITACAO');