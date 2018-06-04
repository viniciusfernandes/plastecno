create table vendas.tb_tipo_operacao_estoque (
	id integer not null,
	descricao varchar(50) not null
);
ALTER TABLE vendas.tb_tipo_operacao_estoque ADD PRIMARY KEY (id);

insert into vendas.tb_tipo_operacao_estoque values (0, 'ENTRADA_PEDIDO_COMPRA');
insert into vendas.tb_tipo_operacao_estoque values (1, 'SAIDA_PEDIDO_VENDA');
insert into vendas.tb_tipo_operacao_estoque values (2, 'SAIDA_DEVOLUCAO_COMPRA');
insert into vendas.tb_tipo_operacao_estoque values (3, 'ENTRADA_DEVOLUCAO_VENDA');
insert into vendas.tb_tipo_operacao_estoque values (4, 'ENTRADA_MANUAL');
insert into vendas.tb_tipo_operacao_estoque values (5, 'SAIDA_MANUAL');
insert into vendas.tb_tipo_operacao_estoque values (6, 'ALTERACAO_MANUAL_VALOR');
insert into vendas.tb_tipo_operacao_estoque values (7, 'CONFIGURACAO_MANUAL');

create table vendas.tb_registro_estoque (
	id integer not null,
	id_item_estoque integer not null,
	id_item_pedido integer default null,
	id_pedido integer default null,
	id_tipo_operacao integer not null,
	id_usuario integer default null,
	data_operacao timestamp not null,
	nome_usuario varchar(50) default null,
	quantidade_anterior integer not null,
	quantidade_item integer default null,
	quantidade_posterior integer not null,
	sequencial_item_pedido integer default null,
	valor_anterior numeric(9, 2) default null,
	valor_posterior numeric(9, 2) default null
);
ALTER TABLE vendas.tb_registro_estoque ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_registro_estoque ADD CONSTRAINT id_tipo_operacao FOREIGN KEY (id_tipo_operacao) REFERENCES vendas.tb_tipo_operacao_estoque(id);
create index idx_registro_estoque_id_pedido on vendas.tb_registro_estoque (id_pedido);
create index idx_registro_estoque_id_usuario on vendas.tb_registro_estoque (id_usuario);
create index idx_registro_estoque_id_item_estoque on vendas.tb_registro_estoque (id_item_estoque);
create index idx_registro_estoque_id_item_pedido on vendas.tb_registro_estoque (id_item_pedido);

create sequence vendas.seq_registro_estoque_id increment by 1 minvalue 1 no maxvalue start with 1;

INSERT INTO VENDAS.TB_CONFIGURACAO_SISTEMA VALUES ('EXPIRACAO_REGISTRO_ESTOQUE_MESES', '4');