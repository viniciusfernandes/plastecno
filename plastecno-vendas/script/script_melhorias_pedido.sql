alter table vendas.tb_item_pedido add peso numeric(9,2) default 0;
alter table vendas.tb_pedido add observacao_producao varchar(800) default null;
insert into vendas.tb_situacao_pedido (id, descricao) values (12, 'ORCAMENTO DIGITACAO');

alter table vendas.tb_logradouro_cliente add codigo_municipio varchar(10) default null;
alter table vendas.tb_logradouro_contato add codigo_municipio varchar(10) default null;
alter table vendas.tb_logradouro_pedido add codigo_municipio varchar(10) default null;
alter table vendas.tb_logradouro_representada add codigo_municipio varchar(10) default null;
alter table vendas.tb_logradouro_transportadora  add codigo_municipio varchar(10) default null;
alter table vendas.tb_logradouro_usuario add codigo_municipio varchar(10) default null;

alter table vendas.tb_cliente ALTER COLUMN email SET DATA TYPE varchar(500);