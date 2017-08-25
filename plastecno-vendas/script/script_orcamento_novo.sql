insert into vendas.tb_situacao_pedido (id, descricao) values (13, 'ORCAMENTO ACEITO');
alter table vendas.tb_pedido add id_orcamento integer default null;
insert into vendas.tb_situacao_pedido (id, descricao) values (14, 'ORCAMENTO CANCELADO');
create index idx_pedido_id_orcamento on vendas.tb_pedido (id_orcamento);
