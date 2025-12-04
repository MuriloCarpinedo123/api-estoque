package com.controleestoque.api_estoque.controller;

import org.springframework.web.bind.annotation.*;

import com.controleestoque.api_estoque.model.Cliente;
import com.controleestoque.api_estoque.repository.ClienteRepository;

import java.util.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repo;

    public ClienteController(ClienteRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Cliente criar(@RequestBody Cliente c) { return repo.save(c); }

    @GetMapping
    public List<Cliente> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente novo) {
        Cliente c = repo.findById(id).orElse(null);
        if (c != null) {
            c.setNome(novo.getNome());
            c.setEmail(novo.getEmail());
            c.setTelefone(novo.getTelefone());
            return repo.save(c);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) { repo.deleteById(id); }
}