package com.unimag.emitix.controller;

import com.unimag.emitix.dto.CompanyRequest;
import com.unimag.emitix.dto.CompanyResponse;
import com.unimag.emitix.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService CompanyService;

    @GetMapping
    public ResponseEntity<CompanyResponse> getCompany() {
        return ResponseEntity.ok(CompanyService.getCompany());
    }

    @PutMapping
    public ResponseEntity<CompanyResponse> updateCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(CompanyService.updateCompany(request));
    }
}
