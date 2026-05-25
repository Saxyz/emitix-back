CREATE TABLE invoice_items
(
    id          UUID           NOT NULL DEFAULT gen_random_uuid(),
    invoice_id  UUID           NOT NULL,
    description VARCHAR(500)   NOT NULL,
    unspsc_code VARCHAR(20),
    unit        VARCHAR(10)    NOT NULL,
    quantity    NUMERIC(12, 4) NOT NULL,
    unit_price  NUMERIC(18, 2) NOT NULL,
    discount    NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    tax_type    VARCHAR(10),
    tax_rate    NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    subtotal    NUMERIC(18, 2) NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_invoice_items PRIMARY KEY (id),
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id) ON DELETE CASCADE,
    CONSTRAINT chk_invoice_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_invoice_items_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_invoice_items_discount CHECK (discount BETWEEN 0 AND 100)
);

CREATE INDEX idx_invoice_items_inv ON invoice_items(invoice_id);
