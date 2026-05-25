package com.unimag.emitix.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer extends BaseEntity {

    @Column(name = "nit", nullable = false, unique = true, length = 20)
    private String nit;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;
}
