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