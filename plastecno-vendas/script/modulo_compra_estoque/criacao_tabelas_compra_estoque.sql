create table vendas.tb_tipo_pedido (
	id integer not null,
	descricao varchar(50)
);
ALTER TABLE vendas.tb_tipo_pedido ADD PRIMARY KEY (id);

insert into vendas.tb_tipo_pedido values (0, 'REPRESENTACAO');
insert into vendas.tb_tipo_pedido values (1, 'REVENDA');
insert into vendas.tb_tipo_pedido values (2, 'COMPRA');

alter table vendas.tb_pedido add id_tipo_pedido integer default 0;
ALTER TABLE vendas.tb_pedido ADD CONSTRAINT id_tipo_pedido FOREIGN KEY (id_tipo_pedido) references vendas.tb_tipo_pedido (id);

insert into vendas.tb_perfil_acesso (id, descricao) values (nextval('vendas.seq_perfil_acesso_id'), 'CADASTRO_PEDIDO_COMPRA');

ALTER TABLE vendas.tb_pedido RENAME id_vendedor TO id_proprietario;
INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (4, 'COMPRA_PENDENTE_RECEBIMENTO');
INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (5, 'COMPRA_RECEBIDA');
ALTER TABLE vendas.tb_item_pedido add item_recebido boolean default false;

create table vendas.tb_item_estoque (
	id integer not null,
	id_material integer not null,
	id_forma_material integer not null,
	descricao_peca varchar (100),
	comprimento integer,
	medida_interna integer,
	medida_externa integer,
	quantidade integer not null default 0,
	preco_medio numeric(9, 2)
);

ALTER TABLE vendas.tb_item_estoque ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_item_estoque ADD CONSTRAINT id_material FOREIGN KEY (id_material) REFERENCES vendas.tb_material (id);
ALTER TABLE vendas.tb_item_estoque ADD CONSTRAINT id_forma_material FOREIGN KEY (id_forma_material) REFERENCES vendas.tb_forma_material (id);
create sequence vendas.seq_item_estoque_id increment by 1 minvalue 1 no maxvalue start with 1;

ALTER TABLE vendas.tb_item_estoque add aliquota_icms numeric(5,5) default 0;  
alter table vendas.tb_item_estoque add aliquota_ipi numeric (5,5) default 0;

create table vendas.tb_tipo_relacionamento (
	id integer not null,
	descricao varchar(50)
);
ALTER TABLE vendas.tb_tipo_relacionamento ADD PRIMARY KEY (id);
insert into vendas.tb_tipo_relacionamento values (0, 'REPRESENTACAO');
insert into vendas.tb_tipo_relacionamento  values (1, 'FORNECIMENTO');
insert into vendas.tb_tipo_relacionamento  values (2, 'REPRESENTACAO E FORNECIMENTO');


ALTER TABLE vendas.tb_representada ADD id_tipo_relacionamento integer not null default 0;
ALTER TABLE vendas.tb_representada ADD CONSTRAINT id_tipo_relacionamento FOREIGN KEY (id_tipo_relacionamento) REFERENCES vendas.tb_tipo_relacionamento (id);

