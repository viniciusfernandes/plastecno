drop schema IF EXISTS vendas cascade;
create schema vendas;
set search_path to vendas;

create table tb_cliente (
	id integer not null,
	id_ramo_atividade integer not null,
	id_vendedor integer default null,
	nome_fantasia varchar(150) not null,
	razao_social varchar(150) default null,
	cnpj varchar(15) null,
	cpf varchar(18) default null,
	insc_estadual varchar(12) null,
	site varchar (250) default null,
	email varchar (250) default null,
	data_ultimo_contato date default null,
	informacoes_adicionais varchar(500),
	prospeccao_finalizada boolean default false
);

create table tb_logradouro_cliente (
	id integer not null,
	id_cliente integer not null
);

create table tb_cliente_tb_transportadora (
	id_cliente integer not null,
	id_transportadora integer not null
);


create table tb_contato (
	id integer not null,
	id_logradouro integer default null,
	nome varchar (20) not null,
	sobrenome varchar (100) default null,
	cpf varchar(18) default null,
	ddi_1 varchar(3) default null,
	ddd_1 varchar(3) default null,
	telefone_1 varchar(9) default null,
	ramal_1 varchar(5),
	fax_1 varchar(8) default null,
	ddi_2 varchar(3) default null,
	ddd_2 varchar(3),
	telefone_2 varchar(9) default null,
	ramal_2 varchar(5) default null,
	fax_2 varchar(8) default null,
	email varchar(200) default null
);

create table tb_logradouro (
	id integer not null,
	id_endereco varchar(8) not null,
	id_tipo_logradouro integer not null,
	numero integer,
	complemento varchar(50)
);

create table tb_ramo_atividade (
	id integer not null,
	sigla varchar(10) not null,
	descricao varchar (100) not null,
	ativo boolean default true
);

create table tb_regiao (
	id integer not null,
	nome varchar(50) not null
);

create table tb_regiao_tb_bairro (
	id_regiao integer not null,
	id_bairro integer not null
);

create table tb_material (
	id integer not null,
	sigla varchar (10) not null,
	descricao varchar(50),
	peso_especifico numeric(5, 2) not null,
	ativo boolean default true
);

create table tb_material_tb_representada (
	id_material integer not null,
	id_representada integer not null
);

create table tb_perfil_acesso (
	id integer not null,
	descricao varchar(50) not null
);

create table tb_representada (
	id integer not null,
	id_logradouro integer default null,
	nome_fantasia varchar(150) not null,
	razao_social varchar(150) default null,
	cnpj varchar(15) null,
	insc_estadual varchar(12) null,
	site varchar (250) default null,
	email varchar (250) default null,
	ativo boolean default true
);

create table tb_tipo_logradouro (
	id integer not null,
	descricao varchar(20) not null
);

create table tb_contato_representada (
	id integer not null,
	id_representada integer not null
);

create table tb_contato_cliente (
	id integer not null,
	id_cliente integer not null
);

create table tb_contato_transportadora (
	id integer not null,	
	id_transportadora integer not null
);

create table tb_transportadora (
	id integer not null,
	id_logradouro integer default null,
	nome_fantasia varchar(150) not null,
	razao_social varchar(150) default null,
	cnpj varchar(15) null,
	insc_estadual varchar(12) null,
	site varchar (250) default null,
	area_atuacao varchar(250) default null,
	ativo boolean default true
);


create table tb_usuario (
	id integer not null, 
	id_logradouro integer default null,
	email varchar(50) not null unique,
	senha varchar(30) not null, 
	nome varchar(20) not null, 
	sobrenome varchar(40) not null,
	cpf varchar(11) default null,	
	ativo boolean default false,
	vendedor boolean default false 
);

create table tb_usuario_tb_perfil_acesso (
	id_usuario integer not null,
	id_perfil_acesso integer not null
);

create table tb_contato_usuario (
	id integer not null,
	id_usuario integer not null
);

create table tb_tipo_entrega (
	id integer not null,
	sigla varchar(10) not null,
	descricao varchar(100)
);

create table tb_situacao_pedido (
	id integer not null,
	descricao varchar(50)
);

create table tb_finalidade_pedido (
	id varchar(25) not null,
	descricao varchar(100)
);

create table tb_pedido (
	id integer not null,
	id_cliente integer not null,
	id_representada integer not null,
	id_transportadora integer,
	id_transportadora_redespacho integer,
	id_tipo_entrega integer,
	id_vendedor integer not null,
	id_situacao_pedido integer not null,
	id_finalidade_pedido varchar not null,
	id_contato integer not null,
	numero_pedido_cliente varchar(15),
	data_inclusao date not null,
	data_envio date,
	data_entrega date,
	observacao varchar(500),
	valor_pedido numeric(10, 2) default 0,
	forma_pagamento varchar (30)
);

create table tb_contato_pedido (
	id integer not null,
	id_pedido integer not null
);

create table tb_forma_material (
	id integer not null,
	sigla varchar(5) not null,
	descricao varchar(100),
	aliquota_ipi numeric(5,5)
);

create table tb_item_pedido (
	id integer not null,
	id_pedido integer not null,
	id_material integer not null,
	id_forma_material integer not null,
	id_tipo_venda integer not null,
	descricao_peca varchar (100),
	comprimento integer,
	medida_interna integer,
	medida_externa integer,
	quantidade integer not null,
	preco_unidade numeric(9, 2),
	preco_venda numeric(9, 2)
);

create table tb_tipo_venda (
	id integer not null,
	sigla char not null,
	descricao varchar (20) 
);

create table tb_configuracao_sistema (
	parametro varchar (40) not null,
	valor varchar (50) not null
);

create table tb_remuneracao (
	id integer not null,
	id_usuario integer not null,
	salario real,
	comissao real,
	data_inicio_vigencia date default null,
	data_fim_vigencia date default null
);

ALTER TABLE tb_cliente ADD PRIMARY KEY (id);
ALTER TABLE tb_contato_cliente ADD PRIMARY KEY (id);
ALTER TABLE tb_contato_representada ADD PRIMARY KEY (id);
ALTER TABLE tb_contato_transportadora ADD PRIMARY KEY (id);
ALTER TABLE tb_contato ADD PRIMARY KEY (id);
ALTER TABLE tb_forma_material ADD PRIMARY KEY (id);
ALTER TABLE tb_item_pedido ADD PRIMARY KEY (id);
ALTER TABLE tb_logradouro ADD PRIMARY KEY (id);
ALTER TABLE tb_logradouro_cliente ADD PRIMARY KEY (id);
ALTER TABLE tb_material ADD PRIMARY KEY (id);
ALTER TABLE tb_perfil_acesso ADD PRIMARY KEY (id);
ALTER TABLE tb_pedido ADD PRIMARY KEY (id);
ALTER TABLE tb_ramo_atividade ADD PRIMARY KEY (id);
ALTER TABLE tb_regiao ADD PRIMARY KEY (id);
ALTER TABLE tb_remuneracao ADD PRIMARY KEY (id);
ALTER TABLE tb_representada ADD PRIMARY KEY (id);
ALTER TABLE tb_tipo_logradouro ADD PRIMARY KEY (id);
ALTER TABLE tb_tipo_venda ADD PRIMARY KEY (id);
ALTER TABLE tb_transportadora ADD PRIMARY KEY (id);
ALTER TABLE tb_situacao_pedido ADD PRIMARY KEY (id);
ALTER TABLE tb_usuario ADD PRIMARY KEY (id);

ALTER TABLE tb_cliente ADD CONSTRAINT id_ramo_atividade FOREIGN KEY (id_ramo_atividade) REFERENCES tb_ramo_atividade (id);
ALTER TABLE tb_cliente ADD CONSTRAINT id_vendedor FOREIGN KEY (id_vendedor) REFERENCES tb_usuario (id);
ALTER TABLE tb_cliente_tb_transportadora ADD CONSTRAINT id_cliente FOREIGN KEY (id_cliente) REFERENCES tb_cliente (id);
ALTER TABLE tb_cliente_tb_transportadora ADD CONSTRAINT id_transportadora FOREIGN KEY (id_transportadora) REFERENCES tb_transportadora (id);
ALTER TABLE tb_contato ADD CONSTRAINT id_logradouro FOREIGN KEY (id_logradouro) REFERENCES tb_logradouro (id);
ALTER TABLE tb_pedido ADD CONSTRAINT id_contato FOREIGN KEY (id_contato) REFERENCES tb_contato (id);
ALTER TABLE tb_item_pedido ADD CONSTRAINT id_material FOREIGN KEY (id_material) REFERENCES tb_material (id);
ALTER TABLE tb_item_pedido ADD CONSTRAINT id_pedido FOREIGN KEY (id_pedido) REFERENCES tb_pedido (id);
ALTER TABLE tb_item_pedido ADD CONSTRAINT id_tipo_venda FOREIGN KEY (id_tipo_venda) REFERENCES tb_tipo_venda (id);
ALTER TABLE tb_item_pedido ADD CONSTRAINT id_forma_material FOREIGN KEY (id_forma_material) REFERENCES tb_forma_material (id);

ALTER TABLE tb_logradouro ADD CONSTRAINT id_endereco FOREIGN KEY (id_endereco) REFERENCES enderecamento.tb_endereco (cep);
ALTER TABLE tb_logradouro ADD CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES tb_tipo_logradouro (id);
ALTER TABLE tb_logradouro_cliente ADD CONSTRAINT id_logradouro_cliente FOREIGN KEY (id) REFERENCES tb_logradouro (id);
ALTER TABLE tb_logradouro_cliente ADD CONSTRAINT id_cliente FOREIGN KEY (id_cliente) REFERENCES tb_cliente (id);
ALTER TABLE tb_contato_cliente ADD CONSTRAINT id_cliente FOREIGN KEY (id_cliente) references tb_cliente (id);
ALTER TABLE tb_contato_cliente ADD CONSTRAINT id_contato_cliente FOREIGN KEY (id) references tb_contato (id);

ALTER TABLE tb_contato_representada ADD CONSTRAINT id_representada FOREIGN KEY (id_representada) references tb_representada (id);
ALTER TABLE tb_contato_representada ADD CONSTRAINT id_contato_representada FOREIGN KEY (id) references tb_contato (id);
ALTER TABLE tb_contato_transportadora ADD CONSTRAINT id_transportadora FOREIGN KEY (id_transportadora) references tb_transportadora (id);
ALTER TABLE tb_contato_transportadora ADD CONSTRAINT id_contato_transportadora FOREIGN KEY (id) references tb_contato (id);
ALTER TABLE tb_contato_usuario ADD CONSTRAINT id_usuario FOREIGN KEY (id_usuario) references tb_usuario (id);
ALTER TABLE tb_contato_usuario ADD CONSTRAINT id_contato_usuario FOREIGN KEY (id) references tb_contato (id);

ALTER TABLE tb_material_tb_representada ADD CONSTRAINT id_material FOREIGN KEY (id_material) references tb_material (id);
ALTER TABLE tb_material_tb_representada ADD CONSTRAINT id_representada FOREIGN KEY (id_representada) references tb_representada (id);
ALTER TABLE tb_regiao_tb_bairro ADD CONSTRAINT id_regiao FOREIGN KEY (id_regiao) references tb_regiao (id);
ALTER TABLE tb_regiao_tb_bairro ADD CONSTRAINT id_bairro FOREIGN KEY (id_bairro) references enderecamento.tb_bairro (id_bairro);
ALTER TABLE tb_remuneracao ADD CONSTRAINT id_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id);
ALTER TABLE tb_transportadora ADD CONSTRAINT id_logradouro FOREIGN KEY (id_logradouro) REFERENCES tb_logradouro (id);
ALTER TABLE tb_usuario_tb_perfil_acesso ADD CONSTRAINT id_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id);
ALTER TABLE tb_usuario_tb_perfil_acesso ADD CONSTRAINT id_perfil_acesso FOREIGN KEY (id_perfil_acesso) REFERENCES tb_perfil_acesso (id);

create sequence seq_cliente_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_contato_representada_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_contato_transportadora_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_contato_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_item_pedido_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_logradouro_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_material_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_pedido_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_perfil_acesso_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_ramo_atividade_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_regiao_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_representada_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_remuneracao_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_transportadora_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence seq_usuario_id increment by 1 minvalue 1 no maxvalue start with 1;

insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'ADMINISTRACAO');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'ASSOCIACAO_CLIENTE_VENDEDOR');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CONSULTA_RELATORIO_VENDAS_REPRESENTADA');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CONSULTA_RELATORIO_CLIENTE_REGIAO');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CONSULTA_RELATORIO_CLIENTE_VENDEDOR');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CONSULTA_RELATORIO_ENTREGA');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CADASTRO_BASICO');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CADASTRO_CLIENTE');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'CADASTRO_PEDIDO');
insert into tb_perfil_acesso values (nextval('seq_perfil_acesso_id'), 'MANUTENCAO');

insert into tb_tipo_logradouro values (0, 'COBRANCA');
insert into tb_tipo_logradouro values (1, 'COMERCIAL');
insert into tb_tipo_logradouro values (2, 'ENTREGA');
insert into tb_tipo_logradouro values (3, 'FATURAMENTO');
insert into tb_tipo_logradouro values (4, 'RESIDENCIAL');

insert into tb_forma_material values (0, 'CH', 'CHAPA', 0.15);
insert into tb_forma_material values (1, 'BR', 'BARRA REDONDA', 0.1);
insert into tb_forma_material values (2, 'BQ', 'BARRA QUADRADA', 0.1);
insert into tb_forma_material values (3, 'BS', 'BARRA SEXTAVADA', 0.1);
insert into tb_forma_material values (4, 'TB', 'TUBO', 0.0);
insert into tb_forma_material values (5, 'PC', 'PEÇA', 0.0);

insert into tb_situacao_pedido values (0, 'PEDIDO EM DIGITAÇÃO');
insert into tb_situacao_pedido values (1, 'ORÇAMENTO');
insert into tb_situacao_pedido values (2, 'PEDIDO ENVIADO');
insert into tb_situacao_pedido values (3, 'PEDIDO CANCELADO');

insert into tb_finalidade_pedido values ('INDUSTRIALIZACAO', 'INSDUSTRIALIZAÇÃO');
insert into tb_finalidade_pedido values ('CONSUMO', 'CONSUMO');
insert into tb_finalidade_pedido values ('REVENDA', 'REVENDA');

insert into tb_tipo_entrega values (0, 'CIF', 'PEDIDO ENTREGUE NO CLIENTE');
insert into tb_tipo_entrega values (1, 'CIF_TRANS', 'PEDIDO ENTREGUE NA TRANPORTADORA PARA REDESPACHO');
insert into tb_tipo_entrega values (2, 'FOB', 'PEDIDO RETIRADO PELO CLIENTE');

insert into tb_tipo_venda values (0, 'K', 'VENDA POR KILO');
insert into tb_tipo_venda values (1, 'P', 'VENDA POR PEÇA');

/*
 * Configuracao para teste local
insert into tb_configuracao_sistema values ('DIAS_INATIVIDADE_CLIENTE', '90');
insert into tb_configuracao_sistema values ('NOME_SERVIDOR_SMTP', 'smtp.googlemail.com');
insert into tb_configuracao_sistema values ('PORTA_SERVIDOR_SMTP', '465');
insert into tb_configuracao_sistema values ('SSL_HABILITADO_PARA_SMTP', 'true');
*/

/* Ainda nao foi usado
 *insert into tb_configuracao_sistema values ('NOME_SERVIDOR_SMTP_ENTRADA', 'pop3.plastecno.com.br entrada');
 *insert into tb_configuracao_sistema values ('PORTA_SERVIDOR_SMTP_ENTRADA', '110'); 
 */

insert into tb_configuracao_sistema values ('DIAS_INATIVIDADE_CLIENTE', '90');
insert into tb_configuracao_sistema values ('NOME_SERVIDOR_SMTP', 'smtp.plastecno.com.br');
insert into tb_configuracao_sistema values ('PORTA_SERVIDOR_SMTP', '587');
insert into tb_configuracao_sistema values ('SSL_HABILITADO_PARA_SMTP', 'false');

insert into vendas.tb_usuario (id, email, senha, nome, sobrenome, ativo, vendedor) 
	values (nextval('vendas.seq_usuario_id'), 'admin@plastecno.com.br', '345678', 'ADMIN', 'ADMIN', true, false);
insert into vendas.tb_usuario_tb_perfil_acesso values (
	(select id from vendas.tb_usuario where email = 'admin@plastecno.com.br'),
	(select id from vendas.tb_perfil_acesso where descricao = 'ADMINISTRACAO'));
											
