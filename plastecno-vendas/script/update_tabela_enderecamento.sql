set search_path to enderecamento;
create table tb_pais (
	id_pais integer not null,
	pais varchar (50) not null 
); 

ALTER TABLE tb_pais ADD PRIMARY KEY (id_pais);
insert into tb_pais (id_pais, pais) values (1, 'Brasil'); 

ALTER TABLE tb_cidade add column id_pais integer;
UPDATE tb_cidade set id_pais = 1;
ALTER TABLE tb_cidade ADD CONSTRAINT id_pais FOREIGN KEY (id_pais) REFERENCES tb_pais (id_pais);

ALTER TABLE tb_estado add column id_pais integer;
UPDATE tb_estado set id_pais = 1;
ALTER TABLE tb_estado ADD CONSTRAINT id_pais FOREIGN KEY (id_pais) REFERENCES tb_pais (id_pais);

create sequence seq_bairro_id increment by 1 minvalue 1 no maxvalue start with 56678;
create sequence seq_cidade_id increment by 1 minvalue 1 no maxvalue start with 12758;
create sequence seq_pais_id increment by 1 minvalue 1 no maxvalue start with 2;

