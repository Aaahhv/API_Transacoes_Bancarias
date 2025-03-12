package com.amanda.transacoes.services;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.repositories.DispositivoRepository;
import com.amanda.transacoes.validators.DispositivoValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispositivoServiceTests {

    @Mock
    private DispositivoRepository dispositivoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private DispositivoValidator dispositivoValidator;

    @InjectMocks
    private DispositivoService dispositivoService;

    private UUID clienteId;
    private UUID dispositivoId;
    private DispositivoDto dispositivoDto;
    private DispositivoModel dispositivoModel;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        dispositivoId = UUID.randomUUID();
        dispositivoDto = new DispositivoDto(clienteId, "Dispositivo Teste");
        dispositivoModel = new DispositivoModel("Dispositivo Teste", true, clienteId);
        dispositivoModel.setId(dispositivoId);
    }

    
    @Test
    void create_CreateValido_DeveSalvarDispositivo() {
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoModel result = dispositivoService.create(dispositivoDto);

        assertNotNull(result);
        assertEquals("Dispositivo Teste", result.getDescricao());
        assertEquals(clienteId, result.getClienteId());
        verify(dispositivoRepository, times(1)).save(any(DispositivoModel.class));
    }
 
    @Test
    void getAll_Valido_DeveRetornarListaDeDispositivo() {
        List<DispositivoModel> lista = Arrays.asList(dispositivoModel);
        when(dispositivoRepository.findAll()).thenReturn(lista);

        List<DispositivoModel> result = dispositivoService.getAll();

        assertEquals(1, result.size());
        assertEquals(dispositivoModel, result.get(0));
    }

    @Test
    void getById_Valido_DeveRetornarDispositivoModel() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));

        Optional<DispositivoModel> result = dispositivoService.getById(dispositivoId);

        assertTrue(result.isPresent());
        assertEquals(dispositivoModel, result.get());
    }

    @Test
    void getById_NaoEncontrado_DeveRetornarOptionalEmpty() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        Optional<DispositivoModel> result = dispositivoService.getById(dispositivoId);

        assertFalse(result.isPresent());
    }

    @Test
    void getByClienteId_ClienteComDispositivo_DeveRetornarLista() {
        List<DispositivoModel> dispositivos = List.of(dispositivoModel);

        when(dispositivoRepository.findByClienteId(clienteId)).thenReturn(dispositivos);

        List<DispositivoModel> resultado = dispositivoService.getByClienteId(clienteId);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(dispositivoModel, resultado.get(0));

        verify(dispositivoRepository, times(1)).findByClienteId(clienteId);
    }


    @Test
    void update_DescricaoEClienteId_DeveAlterarADescricaoEOClienteId() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);
        DispositivoDto updateDto = new DispositivoDto(clienteId, "Nova descricao");

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals("Nova descricao", result.getDescricao());
        assertEquals(clienteId, result.getClienteId());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    }

        
    @Test
    void update_UpdateApenasDescricao_DeveAlterarApenasADescricao() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(null, "Nova Descrição");

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals("Nova Descrição", result.getDescricao());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    } 

    @Test
    void update_ApenasClienteIdEDescricaoNula_DeveAlterarApenasClienteId() {
        UUID novoClienteId = UUID.randomUUID();
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(novoClienteId, null);

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals(novoClienteId, result.getClienteId());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    }

    @Test
    void update_ApenasClienteIdEDescricaoEmpy_DeveAlterarApenasClienteId() {
        UUID novoClienteId = UUID.randomUUID();
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(novoClienteId, "");

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals(novoClienteId, result.getClienteId());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    }


    @Test
    void update_DispositivoNaoEncontrado_DeveLancarExcessao() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.update(dispositivoDto, dispositivoId);
        });

        assertEquals("404 NOT_FOUND \"Dispositivo não encontrado.\"", exception.getMessage());
    }


    @Test
    void deleteById_Valido_DeveDeletarODispositivo() {
        doNothing().when(dispositivoRepository).deleteById(dispositivoId);

        dispositivoService.deleteById(dispositivoId);

        verify(dispositivoRepository, times(1)).deleteById(dispositivoId);
    }

    @Test
    void ativar_Valido_DeveAtivarODispositivo() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoModel result = dispositivoService.ativar(dispositivoId, false);

        assertFalse(result.getAtivo());
        verify(dispositivoRepository, times(1)).save(any(DispositivoModel.class));
    }

    @Test
    void ativar_DispositivoNaoEncontrado_DeveLancarExcessao() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.ativar(dispositivoId, false);
        });

        assertEquals("404 NOT_FOUND \"Dispositivo não encontrado.\"", exception.getMessage());
    }

    @Test
    void deleteByClienteId_Valido_DeveDeletarDispositivo() {
        doNothing().when(dispositivoRepository).deleteByClienteId(clienteId);

        dispositivoService.deleteByClienteId(clienteId);

        verify(dispositivoRepository, times(1)).deleteByClienteId(clienteId);
    }

    @Test
    void existsById_DispositivoExiste_DeveRetornarTrue() {
        UUID id = UUID.randomUUID();
        when(dispositivoRepository.existsById(id)).thenReturn(true);

        assertTrue(dispositivoService.existsById(id));
    }

    @Test
    void isDispositivoAtivo_DispositivoAtivo_DeveRetornarTrue() {
        dispositivoModel.setAtivo(true);
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));

        boolean result = dispositivoService.isDispositivoAtivo(dispositivoId);

        assertTrue(result);
    }

    @Test
    void isDispositivoAtivo_DispositivoNaoEncontrado_DevelancarExcessao() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.isDispositivoAtivo(dispositivoId);
        });

        assertEquals("404 NOT_FOUND \"Dispositivo não encontrado.\"", exception.getMessage());
    }
}
