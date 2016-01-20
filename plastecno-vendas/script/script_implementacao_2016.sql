alter table vendas.tb_pedido drop column data_inclusao;
alter table vendas.tb_pedido add prazo_entrega integer default null;