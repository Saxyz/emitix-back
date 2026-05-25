package com.unimag.emitix.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Muchos logs pueden pertenecer a un usuario (nullable: sistema también genera logs)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    // Copia del username para mantener trazabilidad si el usuario es eliminado
    @Column(name = "username", length = 50)
    private String username;

    // USUARIO | SISTEMA | SCHEDULER
    @Column(name = "actor_type", nullable = false, length = 20)
    @Builder.Default
    private String actorType = "USUARIO";

    // Código de acción: FACTURA_CREADA, LOGIN_EXITOSO, USUARIO_CREADO, etc.
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    // Tipo de entidad afectada: FACTURA, COMPRADOR, PRODUCTO, USUARIO, EMPRESA, etc.
    @Column(name = "entity", nullable = false, length = 50)
    private String entity;

    // UUID de la entidad afectada (NOT NULL en DDL v3.2)
    @Column(name = "entity_id", nullable = false, length = 36)
    private String entityId;

    // Referencia legible: ej. número de factura "FE-0001"
    @Column(name = "entity_ref", length = 100)
    private String entityRef;

    // Descripción legible de la acción realizada
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // EXITOSO | FALLIDO | PARCIAL
    @Column(name = "result", nullable = false, length = 20)
    @Builder.Default
    private String result = "EXITOSO";

    // Detalle del error si result = FALLIDO
    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // Datos adicionales en formato JSON string
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
