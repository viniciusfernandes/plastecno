create table vendas.tb_situacao_nfe (
	id integer not null,	
	descricao varchar(20) not null
);
ALTER TABLE vendas.tb_situacao_nfe ADD PRIMARY KEY (id);
insert into vendas.tb_situacao_nfe(id, descricao) values (0, 'INDEFINIDO');
insert into vendas.tb_situacao_nfe(id, descricao) values (1, 'EMITIDA');
insert into vendas.tb_situacao_nfe(id, descricao) values (2, 'CANCELADA');

ALTER TABLE vendas.tb_nfe_pedido add id_situacao_nfe integer default null;
create index idx_nfe_pedido_id_pedido on vendas.tb_nfe_pedido (id_pedido);

create table vendas.tb_tipo_nfe (
	id integer not null,	
	descricao varchar(20) not null
);
ALTER TABLE vendas.tb_tipo_nfe ADD PRIMARY KEY (id);
insert into vendas.tb_tipo_nfe(id, descricao) values (0, 'INDEFINIDO');
insert into vendas.tb_tipo_nfe(id, descricao) values (1, 'SAIDA');
insert into vendas.tb_tipo_nfe(id, descricao) values (2, 'ENTRADA');
insert into vendas.tb_tipo_nfe(id, descricao) values (3, 'DEVOLUCAO');
insert into vendas.tb_tipo_nfe(id, descricao) values (4, 'TRIANGULARIZACAO');

ALTER TABLE vendas.tb_nfe_pedido add id_tipo_nfe integer default null;

ALTER TABLE vendas.tb_nfe_pedido RENAME column numero_triang TO numero_associado;

update vendas.tb_nfe_pedido set id_tipo_nfe = 0, id_situacao_nfe =0;

alter table vendas.tb_nfe_pedido add constraint id_tipo_nfe foreign key (id_tipo_nfe ) references vendas.tb_tipo_nfe (id);
alter table vendas.tb_nfe_pedido add constraint id_situacao_nfe foreign key (id_situacao_nfe ) references vendas.tb_situacao_nfe (id);