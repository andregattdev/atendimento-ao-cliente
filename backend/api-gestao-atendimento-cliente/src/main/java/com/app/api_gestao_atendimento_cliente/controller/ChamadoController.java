package com.app.api_gestao_atendimento_cliente.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api_gestao_atendimento_cliente.dto.ChamadoRequestDTO;
import com.app.api_gestao_atendimento_cliente.dto.ChamadoResponseDTO;
import com.app.api_gestao_atendimento_cliente.dto.DashboardChamadosDTO;
import com.app.api_gestao_atendimento_cliente.entity.Prioridade;
import com.app.api_gestao_atendimento_cliente.entity.StatusChamado;
import com.app.api_gestao_atendimento_cliente.exceptions.RegraNegocioException;
import com.app.api_gestao_atendimento_cliente.service.ChamadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    // Abrir chamado
    @PostMapping
    public ResponseEntity<ChamadoResponseDTO> abrirChamado(
            @Valid @RequestBody ChamadoRequestDTO dados) {

        ChamadoResponseDTO chamado = chamadoService.abrirChamado(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chamado);
    }

    @GetMapping
    public ResponseEntity<Page<ChamadoResponseDTO>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new RegraNegocioException(
                    "A página não pode ser menor que zero.");
        }

        if (size < 1 || size > 50) {
            throw new RegraNegocioException(
                    "O tamanho da página deve estar entre 1 e 50.");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ChamadoResponseDTO> chamados = chamadoService.listarTodos(pageable);

        return ResponseEntity.ok(chamados);
    }

    @PostMapping("/fila/proximo")
    public ResponseEntity<ChamadoResponseDTO> assumirProximoChamado() {

        ChamadoResponseDTO chamado = chamadoService.assumirProximoChamado();

        return ResponseEntity.ok(chamado);
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<ChamadoResponseDTO> resolverChamado(
            @PathVariable UUID id) {

        ChamadoResponseDTO chamado = chamadoService.resolverChamado(id);

        return ResponseEntity.ok(chamado);
    }

    @PatchMapping("/{id}/aguardar-cliente")
    public ResponseEntity<ChamadoResponseDTO> aguardarCliente(
            @PathVariable UUID id) {

        ChamadoResponseDTO chamado = chamadoService.aguardarCliente(id);

        return ResponseEntity.ok(chamado);
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<ChamadoResponseDTO> fecharChamado(
            @PathVariable UUID id) {

        ChamadoResponseDTO chamado = chamadoService.fecharChamado(id);

        return ResponseEntity.ok(chamado);
    }

    @PatchMapping("/{id}/retomar")
    public ResponseEntity<ChamadoResponseDTO> retomarAtendimento(
            @PathVariable UUID id) {

        ChamadoResponseDTO chamado = chamadoService.retomarAtendimento(id);

        return ResponseEntity.ok(chamado);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ChamadoResponseDTO>> listarPorStatus(
            @RequestParam StatusChamado status) {

        List<ChamadoResponseDTO> chamados = chamadoService.listarPorStatus(status);

        return ResponseEntity.ok(chamados);
    }

    @GetMapping("/prioridade")
    public ResponseEntity<List<ChamadoResponseDTO>> listarPorPrioridade(
            @RequestParam Prioridade prioridade) {

        List<ChamadoResponseDTO> chamados = chamadoService.listarPorPrioridade(prioridade);

        return ResponseEntity.ok(chamados);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<ChamadoResponseDTO>> listarPorCliente(
            @PathVariable UUID clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new RegraNegocioException(
                    "A página não pode ser menor que zero.");
        }

        if (size < 1 || size > 50) {
            throw new RegraNegocioException(
                    "O tamanho da página deve estar entre 1 e 50.");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ChamadoResponseDTO> chamados = chamadoService.listarPorCliente(clienteId, pageable);

        return ResponseEntity.ok(chamados);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ChamadoResponseDTO> cancelarChamado(
            @PathVariable UUID id) {

        ChamadoResponseDTO chamado = chamadoService.cancelarChamado(id);

        return ResponseEntity.ok(chamado);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardChamadosDTO> consultarDashboard() {

        DashboardChamadosDTO dashboard = chamadoService.consultarDashboard();

        return ResponseEntity.ok(dashboard);
    }

}