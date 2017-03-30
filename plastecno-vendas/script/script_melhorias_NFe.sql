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

alter table vendas.tb_item_pedido add aliquota_comissao_representada numeric(5,5) default 0;
ALTER TABLE vendas.tb_item_pedido RENAME column valor_comissionado_representacao TO valor_comissionado_representada;

alter table vendas.tb_cliente add inscricao_suframa varchar(10) default null;


create table vendas.tb_situacao_duplicata (
	id integer not null,	
	descricao varchar(20) not null
);

ALTER TABLE vendas.tb_situacao_duplicata ADD PRIMARY KEY (id);
insert into vendas.tb_situacao_duplicata(id, descricao) values (1, 'A VENCER');
insert into vendas.tb_situacao_duplicata(id, descricao) values (2, 'VENCIDO');
insert into vendas.tb_situacao_duplicata(id, descricao) values (3, 'LIQUIDADO');

create table vendas.tb_nfe_duplicata (
	id integer not null,
	id_nfe_pedido integer not null,
	data_vencimento date not null,
	valor numeric(10,2) not null,
	id_situacao_duplicata integer not null
);
ALTER TABLE vendas.tb_nfe_duplicata ADD PRIMARY KEY (id);
alter table vendas.tb_nfe_duplicata add constraint id_nfe_pedido foreign key (id_nfe_pedido) references vendas.tb_nfe_pedido (numero);
alter table vendas.tb_nfe_duplicata add constraint id_situacao_duplicata foreign key (id_situacao_duplicata) references vendas.tb_situacao_duplicata (id);
