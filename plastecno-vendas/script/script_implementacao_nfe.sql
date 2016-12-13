alter table vendas.tb_representada add telefone varchar(15) default null;
UPDATE vendas.tb_representada SET telefone = '1130219600' where nome_fantasia = 'PLASTECNO MATRIZ';
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_COFINS', '3');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('PERCENTUAL_PIS', '0.65');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('REGIME_TRIBUTACAO', '3');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('DIRETORIO_XML_NFE', 'C:\\Users\\vinicius\\AppData\\Local\\Temp');
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('CNAE', '4689399');
ALTER TABLE vendas.tb_configuracao_sistema ADD PRIMARY KEY (parametro);
create index idx_transportadora_cnpj on vendas.tb_transportadora (cnpj);
create index idx_cliente_cnpj on vendas.tb_cliente (cnpj);
alter table vendas.tb_logradouro ALTER COLUMN numero SET DATA TYPE varchar(20);
insert into vendas.tb_configuracao_sistema (parametro, valor) values ('CODIGO_MUNICIPIO_GERADOR_ICMS', '3550308');

create table vendas.tb_nfe_pedido (
	numero integer not null,
	serie integer default null,
	modelo integer default null,
	numero_triang integer default null,
	id_pedido integer not null,
	xml_nfe text default null
);
ALTER TABLE vendas.tb_nfe_pedido ADD PRIMARY KEY (numero);
create index idx_pedido_id_pedido on vendas.tb_nfe_pedido (id_pedido);
create index idx_pedido_nfe_numero_triang on vendas.tb_nfe_pedido (numero_triang);


