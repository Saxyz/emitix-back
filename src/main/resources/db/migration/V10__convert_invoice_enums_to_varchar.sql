-- Convert PostgreSQL native enum columns to VARCHAR so Hibernate EnumType.STRING works.
-- Also normalizes existing values from lowercase (postgres enum) to UPPERCASE (Java enum names).

-- ── invoices.status ──────────────────────────────────────────────────────────
ALTER TABLE invoices ALTER COLUMN status TYPE VARCHAR(20) USING status::text;
UPDATE invoices SET status = UPPER(status);
ALTER TABLE invoices ALTER COLUMN status SET NOT NULL;
ALTER TABLE invoices
    ADD CONSTRAINT chk_invoice_status CHECK (
        status IN ('DRAFT','ISSUED','SENT','ACCEPTED','REJECTED','CANCELLED')
    );

-- ── invoices.invoice_type ────────────────────────────────────────────────────
ALTER TABLE invoices ALTER COLUMN invoice_type TYPE VARCHAR(20) USING invoice_type::text;
UPDATE invoices SET invoice_type = CASE invoice_type
    WHEN 'sale'        THEN 'SALE'
    WHEN 'credit_note' THEN 'CREDIT_NOTE'
    WHEN 'debit_note'  THEN 'DEBIT_NOTE'
    ELSE UPPER(invoice_type)
END;
ALTER TABLE invoices ALTER COLUMN invoice_type SET NOT NULL;
ALTER TABLE invoices
    ADD CONSTRAINT chk_invoice_type CHECK (
        invoice_type IN ('SALE','CREDIT_NOTE','DEBIT_NOTE')
    );

-- Drop the now-unused PostgreSQL enum types
DROP TYPE IF EXISTS invoice_status;
DROP TYPE IF EXISTS invoice_type;
