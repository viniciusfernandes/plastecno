create table vendas.tb_tipo_pagamento (
	id integer not null,
	descricao varchar(50)
);

create table vendas.tb_pagamento (
	id integer not null,
	id_fornecedor integer default null,
	id_item_pedido integer default null,
	id_pedido integer default null,
	numero_nf integer default null,
	parcela integer default null,
	total_parcelas integer default null,
	valor_nf numeric(10, 2) default 0,
	valor numeric(10, 2) default 0,
	valor_credito_icms numeric(10, 2) default 0,
	modalidade_frete integer default null,
	data_emissao date default null,
	data_vencimento date not null,
	data_recebimento date default null,
	descricao varchar(200),
	quantidade_item integer default 0,
	sequencial_item integer default 0,
	id_tipo_pagamento integer not null,
	liquidado boolean default false,
	nome_fornecedor varchar(150) default null
);
ALTER TABLE vendas.tb_tipo_pagamento ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_pagamento ADD PRIMARY KEY (id);
ALTER TABLE vendas.tb_pagamento ADD CONSTRAINT id_tipo_pagamento FOREIGN KEY (id_tipo_pagamento ) REFERENCES vendas.tb_tipo_pagamento (id);

create sequence vendas.seq_pagamento_id increment by 1 minvalue 1 no maxvalue start with 1;

create index idx_pagamento_id_fornecedor on vendas.tb_pagamento (id_fornecedor);
create index idx_pagamento_id_item_pedido on vendas.tb_pagamento (id_item_pedido);
create index idx_pagamento_id_pedido on vendas.tb_pagamento (id_pedido);
create index idx_pagamento_numero_nf on vendas.tb_pagamento (numero_nf);

insert into vendas.tb_tipo_pagamento values (0, 'FOLHA_PAGAMENTO');
insert into vendas.tb_tipo_pagamento values (1, 'INSUMO');
insert into vendas.tb_tipo_pagamento values (2, 'DESPESAS_FIXAS');
insert into vendas.tb_tipo_pagamento values (3, 'ACAO_JUDICIAL');
insert into vendas.tb_tipo_pagamento values (4, 'EQUIPAMENTO');
insert into vendas.tb_tipo_pagamento values (5, 'EVENTUALIDADE');
