package com.unimag.emitix.service;

import com.unimag.emitix.dto.CompanyRequest;
import com.unimag.emitix.dto.CompanyResponse;
import com.unimag.emitix.entity.Company;
import com.unimag.emitix.exception.ResourceNotFoundException;
import com.unimag.emitix.mapper.CompanyMapper;
import com.unimag.emitix.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository CompanyRepository;
    private final CompanyMapper CompanyMapper;

    @Transactional(readOnly = true)
    public CompanyResponse getCompany() {
        Company company = CompanyRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Empresa emisora", "registro", "único"));
        return CompanyMapper.toResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(CompanyRequest request) {
        Company company = CompanyRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Empresa emisora", "registro", "único"));

        CompanyMapper.updateFromRequest(request, company);
        Company saved = CompanyRepository.save(company);
        log.info("Company issuer updated: {}", saved.getNit());
        return CompanyMapper.toResponse(saved);
    }
}
