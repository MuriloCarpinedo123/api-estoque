package com.controleestoque.api_estoque.controller;

import com.controleestoque.api_estoque.model.Venda;
import com.controleestoque.api_estoque.repository.VendaRepository;
import com.controleestoque.api_estoque.service.VendaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService service;
    private final VendaRepository repo;

    public VendaController(VendaService service, VendaRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping
    public Venda registrar(@RequestBody JsonNode json) {
        return service.registrarVenda(json);
    }

    @GetMapping
    public List<Venda> listar() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Venda buscar(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }
}