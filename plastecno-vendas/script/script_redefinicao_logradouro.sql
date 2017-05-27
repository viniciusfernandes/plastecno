
ALTER TABLE vendas.tb_logradouro_cliente add cep varchar (8);
ALTER TABLE vendas.tb_logradouro_cliente add endereco varchar (500);
ALTER TABLE vendas.tb_logradouro_cliente add numero varchar (20);
ALTER TABLE vendas.tb_logradouro_cliente add complemento varchar (250);
ALTER TABLE vendas.tb_logradouro_cliente add cidade varchar (50);
ALTER TABLE vendas.tb_logradouro_cliente add uf varchar (2);
ALTER TABLE vendas.tb_logradouro_cliente add pais varchar (50);
ALTER TABLE vendas.tb_logradouro_cliente add id_tipo_logradouro integer NOT NULL default 3;
ALTER TABLE vendas.tb_logradouro_cliente add codificado boolean DEFAULT true;
ALTER TABLE vendas.tb_logradouro_cliente add bairro varchar (50);

ALTER TABLE vendas.tb_logradouro_cliente add constraint id_tipo_logradouro foreign key (id_tipo_logradouro) references vendas.tb_tipo_logradouro (id);


CREATE TABLE vendas.tb_logradouro_pedido (
  id integer NOT NULL,
  id_pedido integer NOT NULL,
  cep character varying(8),
  endereco character varying(500),
  numero character varying(20),
  complemento character varying(250),
  bairro character varying(50),
  cidade character varying(50),
  uf character varying(2),
  pais character varying(50),
  id_tipo_logradouro integer NOT NULL DEFAULT 3,
  codificado boolean DEFAULT true,
  CONSTRAINT tb_logradouro_pedido_pkey PRIMARY KEY (id),
  CONSTRAINT id_pedido FOREIGN KEY (id_pedido) REFERENCES vendas.tb_pedido (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES vendas.tb_tipo_logradouro (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE vendas.tb_logradouro_usuario (
  id integer NOT NULL,
  cep character varying(8),
  endereco character varying(500),
  numero character varying(20),
  complemento character varying(250),
  bairro character varying(50),
  cidade character varying(50),
  uf character varying(2),
  pais character varying(50),
  id_tipo_logradouro integer NOT NULL DEFAULT 3,
  codificado boolean DEFAULT true,
  CONSTRAINT tb_logradouro_usuario_pkey PRIMARY KEY (id),
  CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES vendas.tb_tipo_logradouro (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
alter table vendas.tb_usuario add id_logradouro_usuario integer default null;
ALTER TABLE vendas.tb_usuario add constraint id_logradouro_usuario  foreign key (id_logradouro_usuario ) references vendas.tb_logradouro_usuario  (id);

CREATE TABLE vendas.tb_logradouro_representada (
  id integer NOT NULL,
  cep character varying(8),
  endereco character varying(500),
  numero character varying(20),
  complemento character varying(250),
  bairro character varying(50),
  cidade character varying(50),
  uf character varying(2),
  pais character varying(50),
  id_tipo_logradouro integer NOT NULL DEFAULT 3,
  codificado boolean DEFAULT true,
  CONSTRAINT tb_logradouro_representada_pkey PRIMARY KEY (id),
  CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES vendas.tb_tipo_logradouro (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
alter table vendas.tb_representada add id_logradouro_representada integer default null;
ALTER TABLE vendas.tb_representada add constraint id_logradouro_representada foreign key (id_logradouro_representada) references vendas.tb_logradouro_representada (id);

create sequence vendas.seq_logradouro_cliente_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence vendas.seq_logradouro_pedido_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence vendas.seq_logradouro_usuario_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence vendas.seq_logradouro_representada_id increment by 1 minvalue 1 no maxvalue start with 1;


CREATE TABLE vendas.tb_logradouro_transportadora (
  id integer NOT NULL,
  cep character varying(8),
  endereco character varying(500),
  numero character varying(20),
  complemento character varying(250),
  bairro character varying(50),
  cidade character varying(50),
  uf character varying(2),
  pais character varying(50),
  id_tipo_logradouro integer NOT NULL DEFAULT 3,
  codificado boolean DEFAULT true,
  CONSTRAINT tb_logradouro_transportadora_pkey PRIMARY KEY (id),
  CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES vendas.tb_tipo_logradouro (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
alter table vendas.tb_transportadora add id_logradouro_transportadora integer default null;
ALTER TABLE vendas.tb_transportadora add constraint id_logradouro_transportadora foreign key (id_logradouro_transportadora) references vendas.tb_logradouro_transportadora (id);
create sequence vendas.seq_logradouro_transportadora_id increment by 1 minvalue 1 no maxvalue start with 1;


CREATE TABLE vendas.tb_logradouro_contato (
  id integer NOT NULL,
  cep character varying(8),
  endereco character varying(500),
  numero character varying(20),
  complemento character varying(250),
  bairro character varying(50),
  cidade character varying(50),
  uf character varying(2),
  pais character varying(50),
  id_tipo_logradouro integer NOT NULL DEFAULT 3,
  codificado boolean DEFAULT true,
  CONSTRAINT tb_logradouro_contato_pkey PRIMARY KEY (id),
  CONSTRAINT id_tipo_logradouro FOREIGN KEY (id_tipo_logradouro) REFERENCES vendas.tb_tipo_logradouro (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
alter table vendas.tb_contato add id_logradouro_contato integer default null;
ALTER TABLE vendas.tb_contato add constraint id_logradouro_contato foreign key (id_logradouro_contato) references vendas.tb_logradouro_contato (id);
create sequence vendas.seq_logradouro_contato_id increment by 1 minvalue 1 no maxvalue start with 1;


ALTER TABLE enderecamento.tb_cidade ALTER COLUMN cod_ibge DROP NOT NULL;