CREATE TYPE invoice_status AS ENUM ('draft', 'issued', 'sent', 'accepted', 'rejected', 'cancelled');
CREATE TYPE invoice_type   AS ENUM ('sale', 'credit_note', 'debit_note');

CREATE TABLE invoices
(
    id               UUID           NOT NULL DEFAULT gen_random_uuid(),
    company_id       UUID           NOT NULL,
    buyer_id         UUID           NOT NULL,
    resolution_id    UUID,
    created_by       UUID           NOT NULL,
    number           VARCHAR(20)    NOT NULL,
    prefix           VARCHAR(10)    NOT NULL,
    status           invoice_status NOT NULL DEFAULT 'draft',
    invoice_type     invoice_type   NOT NULL DEFAULT 'sale',
    subtotal         NUMERIC(18, 2) NOT NULL DEFAULT 0,
    tax_total        NUMERIC(18, 2) NOT NULL DEFAULT 0,
    total            NUMERIC(18, 2) NOT NULL DEFAULT 0,
    pdf_url          TEXT,
    xml_url          TEXT,
    issued_at        TIMESTAMPTZ,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_invoices PRIMARY KEY (id),
    CONSTRAINT uq_invoice_number UNIQUE (company_id, prefix, number),
    CONSTRAINT fk_invoices_buyer FOREIGN KEY (buyer_id) REFERENCES buyers (id),
    CONSTRAINT fk_invoices_company FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT fk_invoices_user FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_invoices_company     ON invoices(company_id);
CREATE INDEX idx_invoices_buyer       ON invoices(buyer_id);
CREATE INDEX idx_invoices_status      ON invoices(status);
CREATE INDEX idx_invoices_issued_at   ON invoices(issued_at);
