alter table vendas.tb_nfe_duplicata add id_cliente integer default null;
create index idx_nfe_duplicata_id_cliente on vendas.tb_nfe_duplicata (id_cliente);
alter table vendas.tb_nfe_duplicata add parcela integer default null;
alter table vendas.tb_nfe_duplicata add total_parcelas integer default null;
alter table vendas.tb_nfe_duplicata add codigo_banco varchar(5) default null;
alter table vendas.tb_nfe_duplicata add nome_banco varchar(30) default null;
create index idx_nfe_duplicata_codigo_banco on vendas.tb_nfe_duplicata (codigo_banco);

insert into vendas.tb_situacao_duplicata values (4, 'CARTORIO');
insert into vendas.tb_situacao_duplicata values (5, 'PROTESTADO');
insert into vendas.tb_situacao_duplicata values (6, 'A_VISTA');

insert into vendas.tb_tipo_pagamento values (7, 'PRESTACAO_SERVICO');
insert into vendas.tb_tipo_pagamento values (8, 'FRETE');
insert into vendas.tb_tipo_pagamento values (9, 'BENEFICIOS_FOLHA');
insert into vendas.tb_tipo_pagamento values (10, 'ICMS');
insert into vendas.tb_tipo_pagamento values (11, 'BANCOS_EMPRESTIMOS_JUROS');


