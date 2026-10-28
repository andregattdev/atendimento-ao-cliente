package com.app.api_gestao_atendimento_cliente.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.api_gestao_atendimento_cliente.entity.Chamado;
import com.app.api_gestao_atendimento_cliente.entity.Prioridade;
import com.app.api_gestao_atendimento_cliente.entity.StatusChamado;

public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {

     @Query("""
            SELECT c
            FROM Chamado c
            WHERE c.status = :status
            ORDER BY
                CASE c.prioridade
                    WHEN com.app.api_gestao_atendimento_cliente.entity.Prioridade.ALTA THEN 1
                    WHEN com.app.api_gestao_atendimento_cliente.entity.Prioridade.MEDIA THEN 2
                    WHEN com.app.api_gestao_atendimento_cliente.entity.Prioridade.BAIXA THEN 3
                END ASC,
                c.dataAbertura ASC
            """)
    List<Chamado> buscarFilaPorStatus(StatusChamado status);


    @Query("""
            SELECT c
            FROM Chamado c
            WHERE c.status = :status
            ORDER BY
                CASE
                    WHEN c.prioridade = com.app.api_gestao_atendimento_cliente.entity.Prioridade.ALTA THEN 1
                    WHEN c.prioridade = com.app.api_gestao_atendimento_cliente.entity.Prioridade.MEDIA THEN 2
                    WHEN c.prioridade = com.app.api_gestao_atendimento_cliente.entity.Prioridade.BAIXA THEN 3
                END ASC,
                c.dataAbertura ASC
            """)
    List<Chamado> buscarProximoChamado(StatusChamado status);

    Page<Chamado> findAllByOrderByDataAberturaDesc(Pageable pageable);

    List<Chamado> findByStatusOrderByDataAberturaDesc(
        StatusChamado status
);

    List<Chamado> findByPrioridadeOrderByDataAberturaDesc(
        Prioridade prioridade
    );

    Page<Chamado> findByCliente_IdOrderByDataAberturaDesc(
        UUID clienteId,
        Pageable pageable
);

    long countByStatus(StatusChamado status);

}
