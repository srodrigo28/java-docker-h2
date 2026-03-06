package com.docker.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequestDTO(
        @NotBlank(message = "nome e obrigatorio")
        String nome,
        @NotNull(message = "qtd e obrigatorio")
        @Min(value = 0, message = "qtd deve ser maior ou igual a 0")
        Integer qtd,
        @NotNull(message = "valor e obrigatorio")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que 0")
        BigDecimal valor
) {
}
