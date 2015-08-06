create table vendas.tb_limite_minimo_estoque (
	id integer not null,
	id_forma_material integer not null,
	id_material integer not null,
	quantidade_minima integer not null,
	medida_externa numeric(10,2),
	medida_interna numeric(10,2),
	comprimento numeric(10,2)
);

alter table vendas.tb_limite_minimo_estoque add primary key (id);
alter table vendas.tb_limite_minimo_estoque add constraint id_forma_material foreign key (id_forma_material) references vendas.tb_forma_material (id);
alter table vendas.tb_limite_minimo_estoque add constraint id_material foreign key (id_material) references vendas.tb_material (id);
create sequence vendas.seq_limite_minimo_estoque_id increment by 1 minvalue 1 no maxvalue start with 1;

alter table vendas.tb_item_estoque add id_limite_minimo_estoque integer default null;
alter table vendas.tb_item_estoque add constraint id_limite_minimo_estoque foreign key (id_limite_minimo_estoque) references vendas.tb_item_estoque (id);

alter table vendas.tb_limite_minimo_estoque add taxa_minima numeric(2,2) default 0;