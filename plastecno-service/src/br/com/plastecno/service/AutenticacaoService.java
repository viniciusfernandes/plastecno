package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.AutenticacaoException;
import br.com.plastecno.service.exception.CriptografiaException;

@Local
public interface AutenticacaoService {
    Usuario autenticar(String email, String senha) throws AutenticacaoException;

    String criptografar(String paramentro) throws CriptografiaException;

    String decriptografar(String parametro) throws CriptografiaException;
}
