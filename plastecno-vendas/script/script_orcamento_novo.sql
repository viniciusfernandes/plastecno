insert into vendas.tb_situacao_pedido (id, descricao) values (13, 'ORCAMENTO ACEITO');
alter table vendas.tb_pedido add id_orcamento integer default null;