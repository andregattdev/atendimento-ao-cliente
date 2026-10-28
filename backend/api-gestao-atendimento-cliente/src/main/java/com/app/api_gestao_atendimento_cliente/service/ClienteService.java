package com.app.api_gestao_atendimento_cliente.service;

import com.app.api_gestao_atendimento_cliente.dto.ClienteRequestDTO;
import com.app.api_gestao_atendimento_cliente.dto.ClienteResponseDTO;
import com.app.api_gestao_atendimento_cliente.entity.Cliente;
import com.app.api_gestao_atendimento_cliente.exceptions.RegraNegocioException;
import com.app.api_gestao_atendimento_cliente.exceptions.RecursoNaoEncontradoException;
import com.app.api_gestao_atendimento_cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dados) {

        String email = dados.email().trim();

        if (clienteRepository.existsByEmail(email)) {
            throw new RegraNegocioException(
                    "Já existe um cliente cadastrado com este e-mail.");
        }

        Cliente cliente = new Cliente();

        cliente.setNome(dados.nome().trim());
        cliente.setEmail(email);
        cliente.setTelefone(
                dados.telefone() != null
                        ? dados.telefone().trim()
                        : null);

        cliente.setAtivo(true);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return toResponseDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {

        return clienteRepository.findAllByAtivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(UUID id) {

        Cliente cliente = clienteRepository
                .findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado."));

        return toResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> pesquisarPorNome(String nome) {

        return clienteRepository
                .findByNomeContainingIgnoreCaseAndAtivoTrue(nome.trim())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ClienteResponseDTO atualizar(
            UUID id,
            ClienteRequestDTO dados) {

        Cliente cliente = clienteRepository
                .findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado."));

        String email = dados.email().trim();

        if (clienteRepository.existsByEmailAndIdNot(email, id)) {

            throw new RegraNegocioException(
                    "Este e-mail já está cadastrado para outro cliente.");
        }

        cliente.setNome(dados.nome().trim());
        cliente.setEmail(email);

        cliente.setTelefone(
                dados.telefone() != null
                        ? dados.telefone().trim()
                        : null);

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return toResponseDTO(clienteAtualizado);
    }

    @Transactional
    public void desativar(UUID id) {

        Cliente cliente = clienteRepository
                .findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado."));

        cliente.setAtivo(false);

        clienteRepository.save(cliente);
    }

    @Transactional
    public ClienteResponseDTO reativar(UUID id) {

        Cliente cliente = clienteRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado."));

        if (cliente.isAtivo()) {
            throw new RegraNegocioException(
                    "O cliente já está ativo.");
        }

        cliente.setAtivo(true);

        Cliente clienteReativado = clienteRepository.save(cliente);

        return toResponseDTO(clienteReativado);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataCadastro(),
                cliente.getDataAtualizacao(),
                cliente.isAtivo());
    }
}