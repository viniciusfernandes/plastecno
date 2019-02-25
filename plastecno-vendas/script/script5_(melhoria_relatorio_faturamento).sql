create index idx_nfe_pedido_numero_associado on vendas.tb_nfe_pedido (numero_associado);
drop index if exists vendas.idx_nfe_pedido_id_pedido ;
drop index if exists vendas.idx_pedido_id_pedido ;
drop index if exists vendas.idx_pedido_nfe_numero_triang;

alter table vendas.tb_nfe_pedido add constraint id_pedido foreign key (id_pedido ) references vendas.tb_pedido (id);
