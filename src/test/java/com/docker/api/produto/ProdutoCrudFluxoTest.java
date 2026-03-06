package com.docker.api.produto;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProdutoCrudFluxoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final AtomicInteger passosOk = new AtomicInteger(0);
    private Long produtoCriadoId;

    @Test
    @Order(1)
    @DisplayName("Passo 1: cadastrar produto")
    void passo1_cadastrarProduto() throws Exception {
        Map<String, Object> body = Map.of(
                "nome", "Mouse Gamer",
                "qtd", 12,
                "valor", new BigDecimal("149.90"));

        MvcResult result = mockMvc.perform(post("/produtos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Mouse Gamer"))
                .andExpect(jsonPath("$.qtd").value(12))
                .andExpect(jsonPath("$.valor").value(149.90))
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        produtoCriadoId = response.get("id").asLong();
        passosOk.incrementAndGet();
    }

    @Test
    @Order(2)
    @DisplayName("Passo 2: listar produtos")
    void passo2_listarProdutos() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[*].id").value(hasItem(produtoCriadoId.intValue())))
                .andExpect(jsonPath("$[*].nome").value(hasItem("Produto Seed")))
                .andExpect(jsonPath("$[*].nome").value(hasItem("Mouse Gamer")));

        passosOk.incrementAndGet();
    }

    @Test
    @Order(3)
    @DisplayName("Passo 3: contabilizar passos concluidos")
    void passo3_contabilizarPassosConcluidos() {
        org.junit.jupiter.api.Assertions.assertEquals(2, passosOk.get(),
                "Os 2 passos implementados (cadastrar e listar) devem passar.");
    }
}
