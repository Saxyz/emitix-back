-- Seed admin user: password = admin123 (BCrypt)
INSERT INTO users (id, username, password, email, full_name, phone, role, is_active)
VALUES (gen_random_uuid(),
        'ADMIN',
        '$2a$10$SZpIM72KKPDicniboe363.Tsol29nWToF7nbmyLN6HnFrlLBdQQZ6',
        'admin@emitix.com',
        'Administrador del Sistema',
        '+57 300 000 0000',
        'ADMIN',
        TRUE);

-- Seed operador user: password = operador123 (BCrypt)
INSERT INTO users (id, username, password, email, full_name, phone, role, is_active)
VALUES (gen_random_uuid(),
        'ACCOUNTANT',
        '$2a$10$BC7AY.TKNse8KEEKZzjVRewlIh0/fVNc7U3C52DPyw5Q3DcH5rYAO',
        'operador@emitix.com',
        'Operador de Facturación',
        '+57 300 111 1111',
        'ACCOUNTANT',
        TRUE);

-- Seed default company
INSERT INTO companies (id, nit, legal_name, address, city, logo_url)
VALUES (gen_random_uuid(),
        '900123456-7',
        'Empresa Demo S.A.S.',
        'Calle 123 # 45-67',
        'Bogotá D.C.',
        'https://logo.com/demo.png');

-- Seed sample buyer
INSERT INTO buyers (id, nit, full_name, email, address, document_type)
VALUES (gen_random_uuid(),
        '800987654-3',
        'Cliente Ejemplo Ltda.',
        'compras@clienteejemplo.com',
        'Carrera 10 # 20-30, Medellín',
        'NIT');
