CREATE TABLE buyers
(
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    nit             VARCHAR(20)  NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    address         VARCHAR(255),
    document_type   VARCHAR(10)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_buyers PRIMARY KEY (id),
    CONSTRAINT uq_buyers_nit UNIQUE (nit)
);

CREATE INDEX idx_buyers_nit ON buyers (nit);
