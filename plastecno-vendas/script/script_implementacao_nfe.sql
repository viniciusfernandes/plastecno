alter table vendas.tb_representada add telefone varchar(15) default null;
UPDATE vendas.tb_representada SET telefone = '1130219600' where nome_fantasia = 'PLASTECNO MATRIZ';
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_COFINS', '3');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_PIS', '0.65');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('REGIME_TRIBUTACAO', '3');
create table vendas.tb_pedido_nfe (
	id_pedido integer not null,
	xml_nfe text default null
);
ALTER TABLE vendas.tb_pedido_nfe ADD PRIMARY KEY (id_pedido);
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('DIRETORIO_XML_NFE', 'C:\\Users\\vinicius\\AppData\\Local\\Temp');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('CNAE', '4689399');
ALTER TABLE vendas.tb_configuracao_sistema ADD PRIMARY KEY (parametro);
create index idx_transportadora_cnpj on vendas.tb_transportadora (cnpj);
create index idx_cliente_cnpj on vendas.tb_cliente (cnpj);
alter table vendas.tb_logradouro ALTER COLUMN numero SET DATA TYPE varchar(20);
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('CODIGO_MUNICIPIO_GERADOR_ICMS', '3550308');
alter table vendas.tb_pedido_nfe add numero integer default null;
alter table vendas.tb_pedido_nfe add serie integer default null;
alter table vendas.tb_pedido_nfe add modelo integer default null;
create index idx_pedido_nfe_numero on vendas.tb_pedido_nfe (numero);

alter table vendas.tb_pedido_nfe add numero_triang integer default null;
alter table vendas.tb_pedido_nfe add xml_nfe_triang text default null;
