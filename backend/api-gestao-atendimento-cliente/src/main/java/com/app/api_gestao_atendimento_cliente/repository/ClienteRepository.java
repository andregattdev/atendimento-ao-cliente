package com.app.api_gestao_atendimento_cliente.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api_gestao_atendimento_cliente.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    // Busca um cliente pelo e-mail
    Optional<Cliente> findByEmail(String email);

    // Verifica se já existe um cliente com esse e-mail
    boolean existsByEmail(String email);

    // Lista clientes cujo nome contenha o texto informado
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findAllByAtivoTrue();

    Optional<Cliente> findByIdAndAtivoTrue(UUID id);

    boolean existsByEmailAndAtivoTrue(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

}
