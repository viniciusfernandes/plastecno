alter table vendas.tb_representada add telefone varchar(15) default null;
UPDATE vendas.tb_representada SET telefone = '1130219600' where nome_fantasia = 'PLASTECNO MATRIZ';