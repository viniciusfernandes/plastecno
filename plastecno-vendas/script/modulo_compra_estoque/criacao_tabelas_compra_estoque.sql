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
INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (4, 'COMPRA AGUARDANDO RECEBIMENTO');
INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (5, 'COMPRA RECEBIDA');
ALTER TABLE vendas.tb_item_pedido add item_recebido boolean default false;

create table vendas.tb_item_estoque (
	id integer not null,
	id_material integer not null,
	id_forma_material integer not null,
	descricao_peca varchar (100),
	comprimento numeric(10,2),
  	medida_interna numeric(10,2),
  	medida_externa numeric(10,2),
  	quantidade integer not null default 0,
	preco_medio numeric(9, 2),
	aliquota_icms numeric(5,5) default 0,
	aliquota_ipi numeric (5,5) default 0
);

ALTER TABLE vendas.tb_item_estoque ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_item_estoque ADD CONSTRAINT id_material FOREIGN KEY (id_material) REFERENCES vendas.tb_material (id);
ALTER TABLE vendas.tb_item_estoque ADD CONSTRAINT id_forma_material FOREIGN KEY (id_forma_material) REFERENCES vendas.tb_forma_material (id);
create sequence vendas.seq_item_estoque_id increment by 1 minvalue 1 no maxvalue start with 1;

create table vendas.tb_tipo_relacionamento (
	id integer not null,
	descricao varchar(50)
);
ALTER TABLE vendas.tb_tipo_relacionamento ADD PRIMARY KEY (id);
insert into vendas.tb_tipo_relacionamento values (0, 'REPRESENTACAO');
insert into vendas.tb_tipo_relacionamento  values (1, 'FORNECIMENTO');
insert into vendas.tb_tipo_relacionamento  values (2, 'REPRESENTACAO E FORNECIMENTO');
insert into vendas.tb_tipo_relacionamento  values (3, 'REVENDA');

ALTER TABLE vendas.tb_representada ADD id_tipo_relacionamento integer not null default 0;
ALTER TABLE vendas.tb_representada ADD CONSTRAINT id_tipo_relacionamento FOREIGN KEY (id_tipo_relacionamento) REFERENCES vendas.tb_tipo_relacionamento (id);

update vendas.tb_representada set id_tipo_relacionamento = 3 where nome_fantasia = 'PLASTECNO MATRIZ';

INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (6, 'REVENDA AGUARDANDO EMPACOTAMENTO');
INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (7, 'REVENDA AGUARDANDO ENCOMENDA');


create table vendas.tb_item_reservado (
	id integer not null,
	id_item_estoque integer not null,
	id_item_pedido integer not null,
	data_reserva date default null
);

ALTER TABLE vendas.tb_item_reservado ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_item_reservado ADD CONSTRAINT id_item_estoque FOREIGN KEY (id_item_estoque) REFERENCES vendas.tb_item_estoque (id);
ALTER TABLE vendas.tb_item_reservado ADD CONSTRAINT id_item_pedido FOREIGN KEY (id_item_pedido) REFERENCES vendas.tb_item_pedido (id);
create sequence vendas.seq_item_reservado_id increment by 1 minvalue 1 no maxvalue start with 1;


INSERT INTO VENDAS.TB_SITUACAO_PEDIDO VALUES (8, 'PEDIDO EMPACOTADO');
alter table vendas.tb_item_pedido add quantidade_reservada integer default 0;


CREATE TABLE VENDAS.TB_TIPO_CLIENTE(
	id integer not null,
	descricao varchar(50)
);
ALTER TABLE vendas.TB_TIPO_CLIENTE ADD PRIMARY KEY (id);

insert into vendas.TB_TIPO_CLIENTE values (0, 'NORMAL');
insert into vendas.TB_TIPO_CLIENTE values (1, 'COMPRADOR');

ALTER TABLE VENDAS.TB_CLIENTE ADD ID_TIPO_CLIENTE INTEGER DEFAULT 0;
ALTER TABLE vendas.TB_CLIENTE ADD CONSTRAINT ID_TIPO_CLIENTE FOREIGN KEY (ID_TIPO_CLIENTE) REFERENCES vendas.TB_TIPO_CLIENTE (id);

alter table vendas.tb_item_pedido add item_encomendado boolean default false;
alter table vendas.tb_cliente drop prospeccao_finalizada;
alter table vendas.tb_item_pedido add quantidade_recepcionada integer default 0;

INSERT INTO vendas.tb_perfil_acesso values(14, 'MANUTENCAO_ESTOQUE');
<!-- ULTIMA INSTRUCAO-->
alter table vendas.tb_usuario drop vendedor;


create table vendas.tb_comentario_representada (
	id integer not null,
	data_inclusao date not null,
	conteudo varchar(800) not null,
	id_usuario integer not null,
	id_representada integer not null
);

ALTER TABLE vendas.tb_comentario_representada ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_comentario_representada ADD CONSTRAINT id_usuario FOREIGN KEY (id_usuario) REFERENCES vendas.tb_usuario (id);
ALTER TABLE vendas.tb_comentario_representada ADD CONSTRAINT id_representada FOREIGN KEY (id_representada) REFERENCES vendas.tb_representada (id);

create sequence vendas.seq_comentario_representada_id increment by 1 minvalue 1 no maxvalue start with 1;
<!-- ULTIMA INSTRUCAO-->