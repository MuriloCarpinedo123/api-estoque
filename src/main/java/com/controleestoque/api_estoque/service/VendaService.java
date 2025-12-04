package com.controleestoque.api_estoque.service;

import com.controleestoque.api_estoque.exception.EstoqueInsuficienteException;
import com.controleestoque.api_estoque.model.*;
import com.controleestoque.api_estoque.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public VendaService(VendaRepository vendaRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository) {

        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Venda registrarVenda(JsonNode json) {

        System.out.println("=== DEBUG: JSON Recebido ===");
        System.out.println(json.toString());
        System.out.println("===========================");

        // 1. Validar campos obrigatórios do JSON raiz
        if (!json.has("clienteId") || json.get("clienteId").isNull()) {
            throw new IllegalArgumentException("Campo 'clienteId' é obrigatório");
        }

        if (!json.has("itens") || !json.get("itens").isArray() || json.get("itens").size() == 0) {
            throw new IllegalArgumentException("Campo 'itens' é obrigatório e deve ser um array não vazio");
        }

        Long clienteId = json.get("clienteId").asLong();
        System.out.println("DEBUG: Cliente ID = " + clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));

        List<ItemVenda> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        JsonNode itensJson = json.get("itens");

        // 2. Validar estoque de todos os itens
        for (JsonNode item : itensJson) {
            // Validar campos de cada item
            if (!item.has("produtoId") || item.get("produtoId").isNull()) {
                throw new IllegalArgumentException("Cada item deve ter um 'produtoId'");
            }

            if (!item.has("quantidade") || item.get("quantidade").isNull()) {
                throw new IllegalArgumentException("Cada item deve ter uma 'quantidade'");
            }

            Long produtoId = item.get("produtoId").asLong();
            int quantidadeDesejada = item.get("quantidade").asInt();

            System.out.println("DEBUG: Processando produto ID = " + produtoId + ", quantidade = " + quantidadeDesejada);

            if (quantidadeDesejada <= 0) {
                throw new RuntimeException("Quantidade precisa ser maior que zero");
            }

            // Verificar se produto existe
            System.out.println("DEBUG: Buscando produto ID " + produtoId + " no banco...");
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> {
                        System.out.println("ERRO: Produto ID " + produtoId + " não encontrado!");
                        return new RuntimeException("Produto não encontrado com ID: " + produtoId);
                    });

            System.out.println("DEBUG: Produto encontrado: " + produto.getNome());

            Estoque estoque = estoqueRepository.findByProduto_Id(produtoId)
                    .orElseThrow(() -> new RuntimeException("Estoque não encontrado para o produto ID: " + produtoId));

            if (estoque.getQuantidade() < quantidadeDesejada) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + estoque.getQuantidade() +
                                ", Solicitado: " + quantidadeDesejada);
            }
        }

        // 3. Baixa de estoque e criação da venda
        Venda venda = new Venda();
        venda.setCliente(cliente);

        for (JsonNode item : itensJson) {
            Long produtoId = item.get("produtoId").asLong();
            int quantidadeVendida = item.get("quantidade").asInt();

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

            Estoque estoque = estoqueRepository.findByProduto_Id(produtoId)
                    .orElseThrow(() -> new RuntimeException("Estoque não encontrado para produto ID: " + produtoId));

            // Baixa no estoque
            estoque.setQuantidade(estoque.getQuantidade() - quantidadeVendida);
            estoqueRepository.save(estoque);

            // Criar item da venda
            ItemVenda it = new ItemVenda();
            it.setProduto(produto);
            it.setQuantidade(quantidadeVendida);
            it.setPrecoUnitario(produto.getPreco());
            it.setVenda(venda);

            itens.add(it);
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(quantidadeVendida)));
        }

        venda.setItens(itens);
        venda.setTotal(total);

        return vendaRepository.save(venda);
    }
}