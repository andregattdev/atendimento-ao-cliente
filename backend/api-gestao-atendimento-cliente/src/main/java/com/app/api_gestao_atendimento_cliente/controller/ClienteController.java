package com.app.api_gestao_atendimento_cliente.controller;

import com.app.api_gestao_atendimento_cliente.dto.ClienteRequestDTO;
import com.app.api_gestao_atendimento_cliente.dto.ClienteResponseDTO;
import com.app.api_gestao_atendimento_cliente.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Cadastrar cliente
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @Valid @RequestBody ClienteRequestDTO dados) {

        ClienteResponseDTO cliente =
                clienteService.cadastrar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cliente);
    }

    // Listar clientes ativos
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {

        List<ClienteResponseDTO> clientes =
                clienteService.listarTodos();

        return ResponseEntity.ok(clientes);
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable UUID id) {

        ClienteResponseDTO cliente =
                clienteService.buscarPorId(id);

        return ResponseEntity.ok(cliente);
    }

    // Pesquisar clientes pelo nome
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> pesquisarPorNome(
            @RequestParam String nome) {

        List<ClienteResponseDTO> clientes =
                clienteService.pesquisarPorNome(nome);

        return ResponseEntity.ok(clientes);
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteRequestDTO dados) {

        ClienteResponseDTO cliente =
                clienteService.atualizar(id, dados);

        return ResponseEntity.ok(cliente);
    }

    // Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @PathVariable UUID id) {

        clienteService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    // Reativar cliente
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ClienteResponseDTO> reativar(
            @PathVariable UUID id) {

        ClienteResponseDTO cliente =
                clienteService.reativar(id);

        return ResponseEntity.ok(cliente);
    }
}