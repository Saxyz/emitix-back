-- ============================================================
-- V9: Agregar organization_type y document_type a companies.
--     Idempotente: funciona en instancias nuevas (DDL v3.3 ya
--     incluye estas columnas y constraints) y en instancias
--     previas que aún no las tengan.
-- ============================================================

ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS organization_type VARCHAR(10),
    ADD COLUMN IF NOT EXISTS document_type     VARCHAR(10);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_companies_org_type'
    ) THEN
        ALTER TABLE companies
            ADD CONSTRAINT chk_companies_org_type
                CHECK (organization_type IS NULL OR organization_type IN ('JURIDICA', 'NATURAL'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_companies_doc_type'
    ) THEN
        ALTER TABLE companies
            ADD CONSTRAINT chk_companies_doc_type
                CHECK (document_type IS NULL OR document_type IN ('NIT', 'CC', 'CE', 'PA'));
    END IF;
END $$;
