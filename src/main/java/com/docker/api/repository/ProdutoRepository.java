package com.docker.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docker.api.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
