alter table vendas.tb_pedido drop column data_inclusao;
alter table vendas.tb_pedido add prazo_entrega integer default null;

alter table vendas.tb_cliente add documento_estrangeiro varchar(15);