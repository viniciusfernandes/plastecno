create table crm.tb_categoria_negociacao (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_categoria_negociacao ADD PRIMARY KEY (id);

insert into crm.tb_categoria_negociacao values (0, 'PROPOSTA_CLIENTE');
insert into crm.tb_categoria_negociacao values (1, 'PRIMEIRO_CONTATO');
insert into crm.tb_categoria_negociacao values (2, 'POTENCIAIS');
insert into crm.tb_categoria_negociacao values (3, 'PROJETOS');
insert into crm.tb_categoria_negociacao values (4, 'PROVAVEIS');
insert into crm.tb_categoria_negociacao values (5, 'ESPECIAIS');

create table crm.tb_situacao_negociacao (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_situacao_negociacao ADD PRIMARY KEY (id);

insert into crm.tb_situacao_negociacao values (0, 'ABERTO');
insert into crm.tb_situacao_negociacao values (1, 'ACEITO');
insert into crm.tb_situacao_negociacao values (2, 'ANDAMENTO');
insert into crm.tb_situacao_negociacao values (3, 'CANCELADO');

create table crm.tb_tipo_nao_fechamento (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_tipo_nao_fechamento ADD PRIMARY KEY (id);

insert into crm.tb_tipo_nao_fechamento values (0, 'OK');
insert into crm.tb_tipo_nao_fechamento values (1, 'PRECO');
insert into crm.tb_tipo_nao_fechamento values (2, 'FRETE');
insert into crm.tb_tipo_nao_fechamento values (3, 'FORMA_PAGAMENTO');
insert into crm.tb_tipo_nao_fechamento values (4, 'PRAZO_ENTREGA');
insert into crm.tb_tipo_nao_fechamento values (5, 'OUTROS');

create table crm.tb_negociacao (
	id integer not null,
	id_cliente integer not null,
	id_orcamento integer not null,
	id_situacao_negociacao integer not null,
	id_tipo_nao_fechamento integer not null,
	id_vendedor integer not null,
	id_categoria_negociacao integer not null,
	comentario varchar(1000) default null,
	data_encerramento date default null,
	nome_cliente varchar(150) not null,
	nome_contato varchar(100) default null,
	telefone_contato varchar(15) default null,
	indice_conversao_valor numeric(9, 5) default 0
);
ALTER TABLE crm.tb_negociacao ADD PRIMARY KEY (id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_situacao_negociacao FOREIGN KEY (id_situacao_negociacao) REFERENCES crm.tb_situacao_negociacao(id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_categoria_negociacao FOREIGN KEY (id_categoria_negociacao) REFERENCES crm.tb_categoria_negociacao(id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_tipo_nao_fechamento  FOREIGN KEY (id_tipo_nao_fechamento) REFERENCES crm.tb_tipo_nao_fechamento (id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_orcamento FOREIGN KEY (id_orcamento) REFERENCES vendas.tb_pedido(id);
create index idx_negociacao_id_vendedor on crm.tb_negociacao (id_vendedor);
create index idx_negociacao_id_cliente on crm.tb_negociacao (id_cliente);

create sequence crm.seq_negociacao_id increment by 1 minvalue 1 no maxvalue start with 1;

create table crm.tb_indice_conversao (
	id integer not null,
	id_cliente integer not null unique,
	quantidade_vendas integer not null default 0,
	indice_conversao_valor numeric(9, 5) not null default 0,
	indice_conversao_quantidade numeric(9, 5) not null default 0
);
ALTER TABLE crm.tb_indice_conversao ADD PRIMARY KEY (id);
create index idx_indice_conversao_id_cliente on crm.tb_indice_conversao (id_cliente);
create sequence crm.seq_indice_conversao_id increment by 1 minvalue 1 no maxvalue start with 1;

