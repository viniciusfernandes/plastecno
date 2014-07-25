set search_path to vendas;



create table tb_pedido_tb_logradouro (
	id_pedido integer not null,
	id_logradouro integer not null
);

ALTER TABLE tb_pedido_tb_logradouro ADD CONSTRAINT id_pedido FOREIGN KEY (id_pedido) REFERENCES tb_pedido (id);
ALTER TABLE tb_pedido_tb_logradouro ADD CONSTRAINT id_logradouro FOREIGN KEY (id_logradouro) REFERENCES tb_logradouro (id);

ALTER TABLE tb_representada add comissao numeric(3,2) default 0;  

insert into tb_perfil_acesso values (nextval('vendas.seq_perfil_acesso_id'), 'GERENCIA_VENDAS');
insert into tb_perfil_acesso values (nextval('vendas.seq_perfil_acesso_id'), 'OPERACAO_CONTABIL');

ALTER TABLE tb_pedido add cliente_notificado_venda boolean default false;  
ALTER TABLE tb_item_pedido add aliquota_icms numeric(5,5) ;  