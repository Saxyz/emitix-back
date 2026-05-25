CREATE TABLE companies
(
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    legal_name VARCHAR(255) NOT NULL,
    nit        VARCHAR(20)  NOT NULL,
    address    VARCHAR(255),
    city       VARCHAR(100),
    logo_url   TEXT,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_companies PRIMARY KEY (id),
    CONSTRAINT uq_companies_nit UNIQUE (nit)
);
