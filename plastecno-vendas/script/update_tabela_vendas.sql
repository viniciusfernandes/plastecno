set search_path to vendas;
alter table tb_pedido add column valor_pedido_ipi numeric(10, 2) default 0;
alter table tb_item_pedido add column preco_unidade_ipi numeric(9, 2);
alter table tb_material add column importado boolean default false;

create table tb_tipo_apresentacao_ipi (
	id integer not null,
	descricao varchar(50)
);

ALTER TABLE tb_tipo_apresentacao_ipi ADD PRIMARY KEY (id);

insert into tb_tipo_apresentacao_ipi values (0, 'NUNCA');
insert into tb_tipo_apresentacao_ipi values (1, 'SEMPRE');
insert into tb_tipo_apresentacao_ipi values (2, 'OCASIONAL');

alter table tb_representada add column id_tipo_apresentacao_ipi integer;
ALTER TABLE tb_representada ADD CONSTRAINT id_tipo_apresentacao_ipi FOREIGN KEY (id_tipo_apresentacao_ipi ) REFERENCES tb_tipo_apresentacao_ipi (id);

alter table tb_logradouro alter column complemento type varchar(250) ;
alter table tb_logradouro add column codificado boolean default true;


alter table tb_logradouro_cliente add column cancelado boolean default false;

create table tb_pedido_tb_logradouro_cliente (
	id_pedido integer not null,
	id_logradouro_cliente integer not null
);

ALTER TABLE tb_pedido_tb_logradouro_cliente ADD CONSTRAINT id_pedido FOREIGN KEY (id_pedido) REFERENCES tb_pedido (id);
ALTER TABLE tb_pedido_tb_logradouro_cliente ADD CONSTRAINT id_logradouro_cliente FOREIGN KEY (id_logradouro_cliente) REFERENCES tb_logradouro_cliente (id);