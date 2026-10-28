package com.app.api_gestao_atendimento_cliente.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.api_gestao_atendimento_cliente.dto.ChamadoRequestDTO;
import com.app.api_gestao_atendimento_cliente.dto.ChamadoResponseDTO;
import com.app.api_gestao_atendimento_cliente.dto.DashboardChamadosDTO;
import com.app.api_gestao_atendimento_cliente.entity.Chamado;
import com.app.api_gestao_atendimento_cliente.entity.Cliente;
import com.app.api_gestao_atendimento_cliente.entity.Prioridade;
import com.app.api_gestao_atendimento_cliente.entity.StatusChamado;
import com.app.api_gestao_atendimento_cliente.exceptions.RecursoNaoEncontradoException;
import com.app.api_gestao_atendimento_cliente.exceptions.RegraNegocioException;
import com.app.api_gestao_atendimento_cliente.repository.ChamadoRepository;
import com.app.api_gestao_atendimento_cliente.repository.ClienteRepository;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final ClienteRepository clienteRepository;

    public ChamadoService(
            ChamadoRepository chamadoRepository,
            ClienteRepository clienteRepository) {

        this.chamadoRepository = chamadoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ChamadoResponseDTO abrirChamado(
            ChamadoRequestDTO dados) {

        Cliente cliente = clienteRepository
                .findByIdAndAtivoTrue(dados.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado ou está inativo."));

        Chamado chamado = new Chamado();

        chamado.setCliente(cliente);
        chamado.setAssunto(dados.assunto().trim());
        chamado.setDescricao(dados.descricao().trim());
        chamado.setPrioridade(dados.prioridade());

        Chamado chamadoSalvo = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoSalvo);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponseDTO> consultarFila() {

        return chamadoRepository
                .buscarFilaPorStatus(StatusChamado.AGUARDANDO)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ChamadoResponseDTO toResponseDTO(
            Chamado chamado) {

        return new ChamadoResponseDTO(
                chamado.getId(),
                chamado.getCliente().getId(),
                chamado.getCliente().getNome(),
                chamado.getAssunto(),
                chamado.getDescricao(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getDataAbertura(),
                chamado.getDataInicioAtendimento(),
                chamado.getDataResolucao(),
                chamado.getDataFechamento(),
                chamado.getDataAtualizacao());
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponseDTO> listarTodos(Pageable pageable) {

        return chamadoRepository
                .findAllByOrderByDataAberturaDesc(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional
    public ChamadoResponseDTO assumirProximoChamado() {

        List<Chamado> chamados = chamadoRepository.buscarProximoChamado(
                StatusChamado.AGUARDANDO);

        if (chamados.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                    "Não existem chamados aguardando atendimento.");
        }

        Chamado chamado = chamados.get(0);

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        chamado.setDataInicioAtendimento(agora);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional
    public ChamadoResponseDTO resolverChamado(UUID id) {

        Chamado chamado = chamadoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {
            throw new RegraNegocioException(
                    "Somente chamados em atendimento podem ser resolvidos.");
        }

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.RESOLVIDO);
        chamado.setDataResolucao(agora);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional
    public ChamadoResponseDTO fecharChamado(UUID id) {

        Chamado chamado = chamadoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.RESOLVIDO) {
            throw new RegraNegocioException(
                    "Somente chamados resolvidos podem ser fechados.");
        }

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.FECHADO);
        chamado.setDataFechamento(agora);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional
    public ChamadoResponseDTO aguardarCliente(UUID id) {

        Chamado chamado = chamadoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {
            throw new RegraNegocioException(
                    "Somente chamados em atendimento podem aguardar o cliente.");
        }

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.AGUARDANDO_CLIENTE);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional
    public ChamadoResponseDTO retomarAtendimento(UUID id) {

        Chamado chamado = chamadoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.AGUARDANDO_CLIENTE) {
            throw new RegraNegocioException(
                    "Somente chamados aguardando o cliente podem ter o atendimento retomado.");
        }

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.EM_ATENDIMENTO);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponseDTO> listarPorStatus(
            StatusChamado status) {

        return chamadoRepository
                .findByStatusOrderByDataAberturaDesc(status)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponseDTO> listarPorPrioridade(
            Prioridade prioridade) {

        return chamadoRepository
                .findByPrioridadeOrderByDataAberturaDesc(prioridade)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponseDTO> listarPorCliente(
            UUID clienteId,
            Pageable pageable) {

        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNaoEncontradoException(
                    "Cliente não encontrado.");
        }

        return chamadoRepository
                .findByCliente_IdOrderByDataAberturaDesc(
                        clienteId,
                        pageable)
                .map(this::toResponseDTO);
    }

    @Transactional
    public ChamadoResponseDTO cancelarChamado(UUID id) {

        Chamado chamado = chamadoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado não encontrado."));

        if (chamado.getStatus() != StatusChamado.AGUARDANDO
                && chamado.getStatus() != StatusChamado.EM_ATENDIMENTO) {

            throw new RegraNegocioException(
                    "Somente chamados aguardando ou em atendimento podem ser cancelados.");
        }

        LocalDateTime agora = LocalDateTime.now();

        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataAtualizacao(agora);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        return toResponseDTO(chamadoAtualizado);
    }

    @Transactional(readOnly = true)
    public DashboardChamadosDTO consultarDashboard() {

        long aguardando = chamadoRepository.countByStatus(StatusChamado.AGUARDANDO);

        long emAtendimento = chamadoRepository.countByStatus(StatusChamado.EM_ATENDIMENTO);

        long aguardandoCliente = chamadoRepository.countByStatus(StatusChamado.AGUARDANDO_CLIENTE);

        long resolvidos = chamadoRepository.countByStatus(StatusChamado.RESOLVIDO);

        long fechados = chamadoRepository.countByStatus(StatusChamado.FECHADO);

        long cancelados = chamadoRepository.countByStatus(StatusChamado.CANCELADO);

        long total = aguardando
                + emAtendimento
                + aguardandoCliente
                + resolvidos
                + fechados
                + cancelados;

        return new DashboardChamadosDTO(
                total,
                aguardando,
                emAtendimento,
                aguardandoCliente,
                resolvidos,
                fechados,
                cancelados);
    }

}
