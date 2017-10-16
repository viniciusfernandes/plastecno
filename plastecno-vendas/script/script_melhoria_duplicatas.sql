alter table vendas.tb_nfe_duplicata add id_cliente integer default null;
create index idx_nfe_duplicata_id_cliente on vendas.tb_nfe_duplicata (id_cliente);
alter table vendas.tb_nfe_duplicata add parcela integer default null;
alter table vendas.tb_nfe_duplicata add total_parcelas integer default null;
alter table vendas.tb_nfe_duplicata add codigo_banco varchar(5) default null;
alter table vendas.tb_nfe_duplicata add nome_banco varchar(30) default null;
create index idx_nfe_duplicata_codigo_banco on vendas.tb_nfe_duplicata (codigo_banco);