create table crm.tb_categoria_negociacao (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_categoria_negociacao ADD PRIMARY KEY (id);

insert into crm.tb_categoria_negociacao values (1, 'PROPOSTA_CLIENTE');
insert into crm.tb_categoria_negociacao values (2, 'PRIMEIRO_CONTATO');
insert into crm.tb_categoria_negociacao values (3, 'POTENCIAIS');
insert into crm.tb_categoria_negociacao values (4, 'PROJETOS');
insert into crm.tb_categoria_negociacao values (5, 'PROVAVEIS');
insert into crm.tb_categoria_negociacao values (6, 'ESPECIAIS');

create table crm.tb_situacao_negociacao (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_situacao_negociacao ADD PRIMARY KEY (id);

insert into crm.tb_situacao_negociacao values (1, 'ABERTO');
insert into crm.tb_situacao_negociacao values (2, 'ACEITO');
insert into crm.tb_situacao_negociacao values (3, 'ANDAMENTO');
insert into crm.tb_situacao_negociacao values (4, 'CANCELADO');

create table crm.tb_tipo_nao_fechamento (
	id integer not null,
	descricao varchar (50) not null
);
ALTER TABLE crm.tb_tipo_nao_fechamento ADD PRIMARY KEY (id);

insert into crm.tb_tipo_nao_fechamento values (1, 'OK');
insert into crm.tb_tipo_nao_fechamento values (2, 'PRECO');
insert into crm.tb_tipo_nao_fechamento values (3, 'FRETE');
insert into crm.tb_tipo_nao_fechamento values (4, 'FORMA_PAGAMENTO');
insert into crm.tb_tipo_nao_fechamento values (5, 'PRAZO_ENTREGA');
insert into crm.tb_tipo_nao_fechamento values (6, 'OUTROS');

create table crm.tb_negociacao (
	id integer not null,
	id_pedido integer not null,
	id_situacao_negociacao integer not null,
	id_tipo_nao_fechamento integer not null,
	id_vendedor integer not null,
	id_categoria_negociacao integer not null,
	comentario varchar(1000) default null,
	data_encerramento date default null
);
ALTER TABLE crm.tb_negociacao ADD PRIMARY KEY (id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_situacao_negociacao FOREIGN KEY (id_situacao_negociacao) REFERENCES crm.tb_situacao_negociacao(id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_categoria_negociacao FOREIGN KEY (id_categoria_negociacao) REFERENCES crm.tb_categoria_negociacao(id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_tipo_nao_fechamento  FOREIGN KEY (id_tipo_nao_fechamento) REFERENCES crm.tb_tipo_nao_fechamento (id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_pedido  FOREIGN KEY (id_pedido) REFERENCES vendas.tb_pedido (id);
ALTER TABLE crm.tb_negociacao ADD CONSTRAINT id_vendedor  FOREIGN KEY (id_vendedor) REFERENCES vendas.tb_usuario (id);

create sequence crm.seq_negociacao_id increment by 1 minvalue 1 no maxvalue start with 1;


