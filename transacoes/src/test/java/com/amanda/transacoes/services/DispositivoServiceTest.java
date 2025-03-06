package com.amanda.transacoes.services;

import com.amanda.transacoes.dtos.DispositivoDto;
import com.amanda.transacoes.models.DispositivoModel;
import com.amanda.transacoes.repositories.DispositivoRepository;
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
    void create_CreateValico() {
        when(clienteService.existsById(clienteId)).thenReturn(true);
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoModel result = dispositivoService.create(dispositivoDto);
        assertNotNull(result);
        assertEquals("Dispositivo Teste", result.getDescricao());
        assertEquals(clienteId, result.getClienteId());

        verify(clienteService, times(1)).existsById(clienteId);
        verify(dispositivoRepository, times(1)).save(any(DispositivoModel.class));
    }

    @Test
    void create_ExcessaoClienteNaoEncontrado() {
        when(clienteService.existsById(clienteId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.create(dispositivoDto);
        });

        assertEquals("Cliente não encontrado.", exception.getReason());
        verify(dispositivoRepository, never()).save(any(DispositivoModel.class));
    }

    @Test
    void create_ExcessaoDescricaoVazia() {
        dispositivoDto.setDescricao("");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.create(dispositivoDto);
        });

        assertEquals("A descrição não deve ser vazia.", exception.getReason());
    }
    

    @Test
    void create_ExcessaoDescricaoNula() {
        dispositivoDto.setDescricao(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.create(dispositivoDto);
        });

        assertEquals("A descrição não deve ser nula.", exception.getReason());
    }

    @Test
    void create_ExcessaoClienteIdNulo() {
        dispositivoDto.setClienteId(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.create(dispositivoDto);
        });

        assertEquals("O clienteID não deve ser nulo.", exception.getReason());
    }


    @Test
    void getAll() {
        List<DispositivoModel> lista = Arrays.asList(dispositivoModel);
        when(dispositivoRepository.findAll()).thenReturn(lista);

        List<DispositivoModel> result = dispositivoService.getAll();

        assertEquals(1, result.size());
        assertEquals(dispositivoModel, result.get(0));
    }

    @Test
    void getById_Valido() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));

        Optional<DispositivoModel> result = dispositivoService.getById(dispositivoId);

        assertTrue(result.isPresent());
        assertEquals(dispositivoModel, result.get());
    }

    @Test
    void getById_NaoEncontrado() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        Optional<DispositivoModel> result = dispositivoService.getById(dispositivoId);

        assertFalse(result.isPresent());
    }

    @Test
    void update_DescricaoEClienteId() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(clienteService.existsById(clienteId)).thenReturn(true);
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(clienteId, "Nova descricao");
        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals("Nova descricao", result.getDescricao());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    }

    
    @Test
    void update_UpdateDescricao() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(null, "Nova Descrição");

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals("Nova Descrição", result.getDescricao());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    } 

    @Test
    void update_UpdateClienteId() {
        UUID novoClienteId = UUID.randomUUID();
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(clienteService.existsById(novoClienteId)).thenReturn(true);
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoDto updateDto = new DispositivoDto(novoClienteId, null);

        DispositivoModel result = dispositivoService.update(updateDto, dispositivoId);

        assertEquals(novoClienteId, result.getClienteId());
        verify(dispositivoRepository, times(1)).save(dispositivoModel);
    }

    @Test
    void update_DispositivoNaoEncontrado() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.update(dispositivoDto, dispositivoId);
        });

        assertEquals("Dispositivo não encontrado.", exception.getReason());
    }

    @Test
    void update_ExcessaoClienteIdNaoEncontrado() {
        UUID novoClienteId = UUID.randomUUID();
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(clienteService.existsById(novoClienteId)).thenReturn(false);

        DispositivoDto updateDto = new DispositivoDto(novoClienteId, null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.update(updateDto, dispositivoId);
        });

        assertEquals("Cliente não encontrado.", exception.getReason());
    }

    @Test
    void deleteById_Valido() {
        when(dispositivoRepository.existsById(dispositivoId)).thenReturn(true);
        doNothing().when(dispositivoRepository).deleteById(dispositivoId);

        dispositivoService.deleteById(dispositivoId);

        verify(dispositivoRepository, times(1)).deleteById(dispositivoId);
    }

    @Test
    void deleteById_DispositivoNaoEncontrado() {
        when(dispositivoRepository.existsById(dispositivoId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.deleteById(dispositivoId);
        });

        assertEquals("Dispositivo não encontrado.", exception.getReason());
        verify(dispositivoRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void ativar_Valido() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));
        when(dispositivoRepository.save(any(DispositivoModel.class))).thenReturn(dispositivoModel);

        DispositivoModel result = dispositivoService.ativar(dispositivoId, false);

        assertFalse(result.getAtivo());
        verify(dispositivoRepository, times(1)).save(any(DispositivoModel.class));
    }

    @Test
    void ativar_DispositivoNaoEncontrado() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.ativar(dispositivoId, false);
        });

        assertEquals("Dispositivo não encontrado.", exception.getReason());
    }

    @Test
    void deleteByClienteId_Valido() {
        doNothing().when(dispositivoRepository).deleteByClienteId(clienteId);

        dispositivoService.deleteByClienteId(clienteId);

        verify(dispositivoRepository, times(1)).deleteByClienteId(clienteId);
    }

    @Test
    void existsById() {
        UUID id = UUID.randomUUID();
        when(dispositivoRepository.existsById(id)).thenReturn(true);

        assertTrue(dispositivoService.existsById(id));
    }

    @Test
    void isDispositivoAtivo_DispositivoExiste() {
        dispositivoModel.setAtivo(true);
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.of(dispositivoModel));

        boolean result = dispositivoService.isDispositivoAtivo(dispositivoId);

        assertTrue(result);
    }

    @Test
    void isDispositivoAtivo_ExcessaoDispositivoNaoEncontrado() {
        when(dispositivoRepository.findById(dispositivoId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            dispositivoService.isDispositivoAtivo(dispositivoId);
        });

        assertEquals("Dispositivo não encontrado", exception.getReason());
    }
}
