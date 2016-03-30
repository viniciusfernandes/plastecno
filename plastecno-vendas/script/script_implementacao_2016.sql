alter table vendas.tb_pedido drop column data_inclusao;
alter table vendas.tb_pedido add prazo_entrega integer default null;
alter table vendas.tb_cliente add documento_estrangeiro varchar(15);
alter table vendas.tb_cliente add email_cobranca varchar(250) default null;
alter table vendas.tb_item_pedido add id_pedido_compra integer default null;
create index idx_item_pedido_id_pedido_compra on vendas.tb_item_pedido (id_pedido_compra);
alter table vendas.tb_item_pedido add id_pedido_venda integer default null;
alter table vendas.tb_item_pedido add prazo_entrega integer default null;
alter table vendas.tb_pedido add numero_nf varchar(8) default null;
alter table vendas.tb_pedido add data_emissao_nf date default null;
alter table vendas.tb_pedido add data_vencimento_nf date default null;
alter table vendas.tb_pedido add valor_total_nf numeric(9,2) default null;
alter table vendas.tb_pedido add valor_parcela_nf numeric(9,2) default null;
alter table vendas.tb_pedido add numero_coleta varchar(8) default null;
alter table vendas.tb_pedido add numero_volumes integer default null;
create table vendas.tb_tipo_cst (
	id integer not null,
	descricao varchar(50) not null
);
ALTER TABLE vendas.tb_tipo_cst ADD PRIMARY KEY (id);
insert into vendas.tb_tipo_cst values (0, 'PRODUTO_NACIONAL');
insert into vendas.tb_tipo_cst values (1, 'IMPORTADO_DIRETAMENTE');
insert into vendas.tb_tipo_cst values (2, 'IMPORTADO_ADQUIRIDO_MERCADO_INTERNO');
insert into vendas.tb_tipo_cst values (3, 'PRODUTO_NACIONAL_IMPORTACAO_40_70');
insert into vendas.tb_tipo_cst values (4, 'PRODUTO_NACIONAL_IMPORTACAO_ATE_40');
alter table vendas.tb_item_pedido add id_tipo_cst integer default null;
create index idx_item_pedido_tipo_cst on vendas.tb_item_pedido (id_tipo_cst);
alter table vendas.tb_item_pedido add ncm varchar(15) default null;


alter table vendas.tb_material ALTER COLUMN sigla SET DATA TYPE varchar(20);
alter table vendas.tb_item_estoque add ncm varchar(15) default null;