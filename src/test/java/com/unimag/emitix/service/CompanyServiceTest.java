package com.unimag.emitix.service;

import com.unimag.emitix.dto.CompanyRequest;
import com.unimag.emitix.dto.CompanyResponse;
import com.unimag.emitix.entity.Company;
import com.unimag.emitix.exception.ResourceNotFoundException;
import com.unimag.emitix.mapper.CompanyMapper;
import com.unimag.emitix.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    // CompanyService usa nombres de campo con mayúscula — inyectamos por nombre
    @Mock private CompanyRepository CompanyRepository;
    @Mock private CompanyMapper CompanyMapper;

    @InjectMocks private CompanyService companyService;

    private Company company;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .documentNumber("900123456-7")
                .legalName("Demo S.A.S")
                .address("Calle 1 # 2-3")
                .city("Bogotá")
                .build();
        company.setId(UUID.randomUUID());
    }

    // ── getCompany ────────────────────────────────────────────────────────────

    @Test
    void getCompany_found_returnsResponse() {
        CompanyResponse response = new CompanyResponse(
                company.getId(), company.getDocumentNumber(), company.getLegalName(),
                null, null, null, "CO", null, null, null);

        when(CompanyRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(company));
        when(CompanyMapper.toResponse(company)).thenReturn(response);

        CompanyResponse result = companyService.getCompany();

        assertNotNull(result);
        assertEquals("900123456-7", result.documentNumber());
        verify(CompanyRepository).findFirstByOrderByCreatedAtAsc();
    }

    @Test
    void getCompany_notFound_throwsResourceNotFoundException() {
        when(CompanyRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompany());
    }

    // ── updateCompany ─────────────────────────────────────────────────────────

    @Test
    void updateCompany_successful() {
        CompanyRequest req = new CompanyRequest("900123456-7", "Demo Actualizada S.A.S",  // documentNumber
                "Carrera 10 # 5-20", "Medellín", "Antioquia", "CO",
                "+57 604 111 2222", "info@demo.com", null);

        CompanyResponse response = new CompanyResponse(
                company.getId(), "900123456-7", "Demo Actualizada S.A.S",
                null, null, null, "CO", null, null, null);

        when(CompanyRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(company));
        doNothing().when(CompanyMapper).updateFromRequest(req, company);
        when(CompanyRepository.save(company)).thenReturn(company);
        when(CompanyMapper.toResponse(company)).thenReturn(response);

        CompanyResponse result = companyService.updateCompany(req);

        assertNotNull(result);
        verify(CompanyRepository).save(company);
        verify(CompanyMapper).updateFromRequest(req, company);
    }

    @Test
    void updateCompany_notFound_throwsResourceNotFoundException() {
        CompanyRequest req = new CompanyRequest("000", "X", null, null,  // documentNumber
                null, null, null, null, null);
        when(CompanyRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> companyService.updateCompany(req));
        verify(CompanyRepository, never()).save(any());
    }
}
