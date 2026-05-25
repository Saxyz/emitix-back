-- ============================================================
-- V7: Extender tablas existentes + crear products, resolutions
--     y activity_logs alineado con DDL_emitix_database_v3.2
--     Multi-empresa, role enum, CHECK constraints completos
-- ============================================================

-- ------------------------------------------------------------
-- 1. Extender companies (campos fiscales faltantes + CHECK constraint)
-- ------------------------------------------------------------
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS phone      VARCHAR(30),
    ADD COLUMN IF NOT EXISTS email      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS department VARCHAR(100),
    ADD COLUMN IF NOT EXISTS country    VARCHAR(2) NOT NULL DEFAULT 'CO';

-- Agregar CHECK constraint de country (DDL v3.2)
ALTER TABLE companies
    ADD CONSTRAINT IF NOT EXISTS chk_companies_country CHECK (country ~ '^[A-Z]{2}$');

-- ------------------------------------------------------------
-- 2. Extender buyers (company_id + campos UBL 2.1)
-- ------------------------------------------------------------
ALTER TABLE buyers
    ADD COLUMN IF NOT EXISTS company_id        UUID,
    ADD COLUMN IF NOT EXISTS organization_type CHAR(1)      NOT NULL DEFAULT '1',
    ADD COLUMN IF NOT EXISTS fiscal_regime     VARCHAR(10),
    ADD COLUMN IF NOT EXISTS city              VARCHAR(100),
    ADD COLUMN IF NOT EXISTS department        VARCHAR(100),
    ADD COLUMN IF NOT EXISTS postal_code       VARCHAR(10),
    ADD COLUMN IF NOT EXISTS country           VARCHAR(2)   NOT NULL DEFAULT 'CO';

-- Asignar company_id a los compradores del seed (la única empresa que existe)
UPDATE buyers SET company_id = (SELECT id FROM companies LIMIT 1) WHERE company_id IS NULL;

-- Corregir organization_type: quitar DEFAULT '1', usar VARCHAR(10) con valores válidos
ALTER TABLE buyers ALTER COLUMN organization_type TYPE VARCHAR(10);
ALTER TABLE buyers ALTER COLUMN organization_type DROP DEFAULT;

-- Ahora aplicar NOT NULL y FK
ALTER TABLE buyers ALTER COLUMN company_id SET NOT NULL;
ALTER TABLE buyers
    ADD CONSTRAINT fk_buyers_company FOREIGN KEY (company_id) REFERENCES companies (id);

-- Reemplazar nit → document_number (si no existe la columna, crearla)
ALTER TABLE buyers
    ADD COLUMN IF NOT EXISTS document_number VARCHAR(20);

-- Copiar nit a document_number si existen datos
UPDATE buyers SET document_number = nit WHERE document_number IS NULL AND nit IS NOT NULL;

-- Hacer document_number NOT NULL
ALTER TABLE buyers ALTER COLUMN document_number SET NOT NULL;

-- Reemplazar UNIQUE global de nit → UNIQUE por empresa con document_number
ALTER TABLE buyers DROP CONSTRAINT IF EXISTS uq_buyers_nit;
ALTER TABLE buyers DROP CONSTRAINT IF EXISTS uq_buyer_per_company;
ALTER TABLE buyers
    ADD CONSTRAINT uq_buyer_per_company UNIQUE (company_id, document_number);

-- Agregar CHECK constraints para buyers (DDL v3.2)
ALTER TABLE buyers
    ADD CONSTRAINT IF NOT EXISTS chk_buyer_org_type CHECK (organization_type IN ('JURIDICA', 'NATURAL')),
    ADD CONSTRAINT IF NOT EXISTS chk_buyer_doc_type CHECK (document_type IN ('NIT', 'CC', 'CE', 'PA', 'TI', 'RC')),
    ADD CONSTRAINT IF NOT EXISTS chk_buyer_fiscal CHECK (fiscal_regime IS NULL OR fiscal_regime IN ('RES', 'NRES')),
    ADD CONSTRAINT IF NOT EXISTS chk_buyer_country CHECK (country ~ '^[A-Z]{2}$');

-- ------------------------------------------------------------
-- 3. Extender invoice_items (product_id, line_number, tax_total, discount_pct, tax_type)
--     DDL v3.2: cambio discount → discount_pct, agrega tax_type, tax_rate, tax_total
-- ------------------------------------------------------------
ALTER TABLE invoice_items
    ADD COLUMN IF NOT EXISTS product_id  UUID,
    ADD COLUMN IF NOT EXISTS line_number INT            NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS discount_pct DECIMAL(5, 2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS tax_type    VARCHAR(10),
    ADD COLUMN IF NOT EXISTS tax_total   DECIMAL(18, 2) NOT NULL DEFAULT 0;

-- Agregar CHECK constraints en invoice_items (DDL v3.2)
ALTER TABLE invoice_items
    ADD CONSTRAINT IF NOT EXISTS chk_quantity CHECK (quantity > 0),
    ADD CONSTRAINT IF NOT EXISTS chk_unit_price CHECK (unit_price >= 0),
    ADD CONSTRAINT IF NOT EXISTS chk_discount_pct CHECK (discount_pct BETWEEN 0 AND 100),
    ADD CONSTRAINT IF NOT EXISTS chk_item_tax CHECK (tax_rate BETWEEN 0 AND 100);

-- ------------------------------------------------------------
-- 4. Crear tabla products (catálogo interno por empresa)
-- ------------------------------------------------------------
CREATE TABLE products
(
    id             UUID            NOT NULL DEFAULT gen_random_uuid(),
    company_id     UUID            NOT NULL,
    internal_code  VARCHAR(100)    NOT NULL,
    description    VARCHAR(300)    NOT NULL,
    unspsc_code    VARCHAR(8),
    unit           VARCHAR(10)     NOT NULL DEFAULT 'UND',
    unit_price     DECIMAL(18, 2)  NOT NULL,
    currency       VARCHAR(3)      NOT NULL DEFAULT 'COP',
    tax_rate       DECIMAL(5, 2)   NOT NULL DEFAULT 19.00,
    is_iva_excluded BOOLEAN        NOT NULL DEFAULT FALSE,
    is_service     BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT uq_product_code UNIQUE (company_id, internal_code),
    CONSTRAINT chk_product_price CHECK (unit_price >= 0),
    CONSTRAINT chk_product_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_product_tax CHECK (tax_rate BETWEEN 0 AND 100)
);

CREATE INDEX idx_products_company ON products (company_id);
CREATE INDEX idx_products_code    ON products (internal_code);
CREATE INDEX idx_products_unspsc  ON products (unspsc_code);

-- FK invoice_items → products (ahora que la tabla existe)
ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_product FOREIGN KEY (product_id) REFERENCES products (id);

CREATE INDEX idx_invoice_items_product ON invoice_items (product_id);

-- ------------------------------------------------------------
-- 5. Crear tabla resolutions (resoluciones DIAN por empresa)
-- ------------------------------------------------------------
CREATE TABLE resolutions
(
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    company_id        UUID         NOT NULL,
    resolution_number VARCHAR(50),
    resolution_date   DATE,
    prefix            VARCHAR(10)  NOT NULL,
    range_from        BIGINT       NOT NULL,
    range_to          BIGINT       NOT NULL,
    current_number    BIGINT       NOT NULL DEFAULT 0,
    valid_from        DATE         NOT NULL,
    valid_until       DATE         NOT NULL,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_resolutions PRIMARY KEY (id),
    CONSTRAINT fk_resolutions_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE,
    CONSTRAINT chk_range   CHECK (range_to >= range_from),
    CONSTRAINT chk_current CHECK (current_number >= 0)
);

CREATE INDEX idx_resolutions_company ON resolutions (company_id);

-- FK invoices → resolutions (la columna resolution_id ya existe desde V4)
ALTER TABLE invoices
    ADD CONSTRAINT fk_invoices_resolution FOREIGN KEY (resolution_id) REFERENCES resolutions (id);

-- Nuevos campos en invoices requeridos por DDL v3.2/UBL 2.1
ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS currency       VARCHAR(3)  DEFAULT 'COP',
    ADD COLUMN IF NOT EXISTS due_date       DATE,
    ADD COLUMN IF NOT EXISTS cufe           VARCHAR(96) UNIQUE,
    ADD COLUMN IF NOT EXISTS qr_url         TEXT,
    ADD COLUMN IF NOT EXISTS notes          TEXT,
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20),
    ADD COLUMN IF NOT EXISTS issued_at      TIMESTAMPTZ;

-- Agregar CHECK constraints en invoices (DDL v3.2)
ALTER TABLE invoices
    ADD CONSTRAINT IF NOT EXISTS chk_invoice_currency CHECK (currency ~ '^[A-Z]{3}$'),
    ADD CONSTRAINT IF NOT EXISTS chk_payment_method CHECK (
        payment_method IS NULL OR
        payment_method IN ('CASH', 'TRANSFER', 'CARD', 'CREDIT')
    );

-- ------------------------------------------------------------
-- 6. Crear tabla activity_logs (auditoría de operaciones — DDL v3.2)
-- entity_id y entity son NOT NULL; metadata es JSONB
-- ------------------------------------------------------------
CREATE TABLE activity_logs
(
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id      UUID,
    username     VARCHAR(50),
    actor_type   VARCHAR(20)  NOT NULL DEFAULT 'USUARIO',
    action       VARCHAR(100) NOT NULL,
    entity       VARCHAR(50)  NOT NULL,
    entity_id    VARCHAR(36)  NOT NULL,
    entity_ref   VARCHAR(100),
    description  TEXT,
    result       VARCHAR(20)  NOT NULL DEFAULT 'EXITOSO',
    error_detail TEXT,
    ip_address   VARCHAR(45),
    user_agent   TEXT,
    metadata     JSONB,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_activity_logs PRIMARY KEY (id),
    CONSTRAINT fk_activity_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT chk_actor_type CHECK (actor_type IN ('USUARIO', 'SISTEMA')),
    CONSTRAINT chk_result CHECK (result IN ('EXITOSO', 'FALLIDO', 'PARCIAL')),
    CONSTRAINT chk_entity CHECK (entity IN (
        'FACTURA', 'COMPRADOR', 'PRODUCTO', 'USUARIO',
        'EMPRESA', 'RESOLUCION', 'AUTH'
    ))
);

CREATE INDEX idx_activity_logs_user       ON activity_logs (user_id);
CREATE INDEX idx_activity_logs_action     ON activity_logs (action);
CREATE INDEX idx_activity_logs_entity     ON activity_logs (entity, entity_id);
CREATE INDEX idx_activity_logs_created_at ON activity_logs (created_at DESC);

-- ------------------------------------------------------------
-- 7. Seed data
-- ------------------------------------------------------------

-- Resolución de numeración demo (asociada a la empresa seed)
INSERT INTO resolutions (id, company_id, prefix, resolution_number, resolution_date,
                         range_from, range_to, current_number, valid_from, valid_until, is_active)
VALUES (gen_random_uuid(),
        (SELECT id FROM companies LIMIT 1),
        'FE-',
        '18764060073461',
        CURRENT_DATE,
        1, 5000, 0,
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '2 years',
        TRUE);

-- Actualizar el resolution_id del seed de invoices si existiera alguno
-- (no hay facturas seed en V6, se omite)

-- Productos de ejemplo asociados a la empresa demo
INSERT INTO products (id, company_id, internal_code, description, unspsc_code,
                      unit, unit_price, currency, tax_rate, is_service, is_active)
VALUES
    (gen_random_uuid(),
     (SELECT id FROM companies LIMIT 1),
     'SERV-DEV-001',
     'Desarrollo de software a medida',
     '81112200',
     'HRA', 500000.00, 'COP', 19.00, TRUE, TRUE),
    (gen_random_uuid(),
     (SELECT id FROM companies LIMIT 1),
     'SERV-CON-001',
     'Consultoría tecnológica',
     '81112100',
     'HRA', 250000.00, 'COP', 19.00, TRUE, TRUE),
    (gen_random_uuid(),
     (SELECT id FROM companies LIMIT 1),
     'PROD-LIC-001',
     'Licencia de software empresarial',
     '43230000',
     'UND', 1200000.00, 'COP', 19.00, FALSE, TRUE);
