-- Corrige los hashes BCrypt del seed (V6 tenía hashes inválidos que no coincidían con las contraseñas)
-- admin123
UPDATE users SET password = '$2a$10$SZpIM72KKPDicniboe363.Tsol29nWToF7nbmyLN6HnFrlLBdQQZ6' WHERE username = 'ADMIN';
-- operador123
UPDATE users SET password = '$2a$10$BC7AY.TKNse8KEEKZzjVRewlIh0/fVNc7U3C52DPyw5Q3DcH5rYAO' WHERE username = 'ACCOUNTANT';
