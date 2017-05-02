
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
  cancelado boolean DEFAULT false,
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
  cancelado boolean DEFAULT false,
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

create sequence vendas.seq_logradouro_cliente_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence vendas.seq_logradouro_pedido_id increment by 1 minvalue 1 no maxvalue start with 1;
create sequence vendas.seq_logradouro_usuario_id increment by 1 minvalue 1 no maxvalue start with 1;

