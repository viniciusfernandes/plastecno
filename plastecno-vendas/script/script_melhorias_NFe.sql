create table vendas.tb_situacao_nfe (
	id integer not null,	
	descricao varchar(20) not null
);
ALTER TABLE vendas.tb_situacao_nfe ADD PRIMARY KEY (id);
insert into vendas.tb_situacao_nfe(id, descricao) values (1, 'EMITIDA');
insert into vendas.tb_situacao_nfe(id, descricao) values (2, 'CANCELADA');

ALTER TABLE vendas.tb_nfe_pedido add id_situacao_nfe integer default null;
create index idx_nfe_pedido_id_pedido on vendas.tb_nfe_pedido (id_pedido);

create table vendas.tb_tipo_nfe (
	id integer not null,	
	descricao varchar(20) not null
);
ALTER TABLE vendas.tb_tipo_nfe ADD PRIMARY KEY (id);
insert into vendas.tb_tipo_nfe(id, descricao) values (1, 'SAIDA');
insert into vendas.tb_tipo_nfe(id, descricao) values (2, 'ENTRADA');
insert into vendas.tb_tipo_nfe(id, descricao) values (3, 'DEVOLUCAO');

ALTER TABLE vendas.tb_nfe_pedido add id_tipo_nfe integer default null;
