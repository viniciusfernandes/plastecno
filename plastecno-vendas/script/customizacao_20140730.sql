alter table vendas.tb_contato add departamento varchar(50) default null;
alter table vendas.tb_contato alter column telefone_1 type varchar(10);
alter table vendas.tb_contato alter column telefone_2 type varchar(10);
alter table vendas.tb_contato alter column fax_1 type varchar(9);
alter table vendas.tb_contato alter column fax_2 type varchar(9);

alter table vendas.tb_pedido alter column observacao type varchar(800);

create table vendas.tb_comentario_cliente (
	id integer not null,
	data_inclusao date not null,
	conteudo varchar(300) not null,
	id_cliente integer not null,
	id_vendedor integer not null
);

ALTER TABLE vendas.tb_comentario_cliente ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_comentario_cliente  ADD CONSTRAINT id_vendedor FOREIGN KEY (id_vendedor) REFERENCES vendas.tb_usuario (id);
ALTER TABLE vendas.tb_comentario_cliente  ADD CONSTRAINT id_cliente FOREIGN KEY (id_cliente) REFERENCES vendas.tb_cliente (id);

create sequence vendas.seq_comentario_cliente_id increment by 1 minvalue 1 no maxvalue start with 1;
