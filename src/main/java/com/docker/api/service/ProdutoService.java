package com.docker.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docker.api.dto.ProdutoRequestDTO;
import com.docker.api.model.Produto;
import com.docker.api.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrar(ProdutoRequestDTO request) {
        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setQtd(request.qtd());
        produto.setValor(request.valor());
        return produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }
}
