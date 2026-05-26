-- ============================================================
-- V9: Renombrar nit → document_number; agregar organization_type
--     y document_type a companies. Alineado con DDL v3.3.
-- ============================================================

-- Renombrar columna (consistencia con buyers.document_number)
ALTER TABLE companies RENAME COLUMN nit TO document_number;

ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS organization_type VARCHAR(10),
    ADD COLUMN IF NOT EXISTS document_type     VARCHAR(10);

ALTER TABLE companies
    ADD CONSTRAINT IF NOT EXISTS chk_companies_org_type
        CHECK (organization_type IS NULL OR organization_type IN ('JURIDICA', 'NATURAL'));

ALTER TABLE companies
    ADD CONSTRAINT IF NOT EXISTS chk_companies_doc_type
        CHECK (document_type IS NULL OR document_type IN ('NIT', 'CC', 'CE', 'PA'));
