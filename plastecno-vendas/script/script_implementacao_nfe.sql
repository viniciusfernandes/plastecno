alter table vendas.tb_representada add telefone varchar(15) default null;
UPDATE vendas.tb_representada SET telefone = '1130219600' where nome_fantasia = 'PLASTECNO MATRIZ';

insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_COFINS', '3');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_PIS', '0.65');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('REGIME_TRIBUTACAO', '3');

create table vendas.tb_pedido_nfe (
	id_pedido integer not null,
	xml_nfe text not null
);
ALTER TABLE vendas.tb_pedido_nfe ADD PRIMARY KEY (id_pedido);

insert into vendas.tb_configuracao_sistema (parametro, valor) values ('DIRETORIO_XML_NFE', 'C:\\Users\\vinicius\\AppData\\Local\\Temp');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('CNAE', '4689399');

