alter table vendas.tb_nfe_duplicata add id_cliente integer default null;
create index idx_nfe_duplicata_id_cliente on vendas.tb_nfe_duplicata (id_cliente);
alter table vendas.tb_nfe_duplicata add parcela integer default null;
alter table vendas.tb_nfe_duplicata add total_parcelas integer default null;


