CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL, -- Путь к зашифрованному файлу с документом (md)
    owner_id INT NOT NULL,   -- Владелец документа
    access_level VARCHAR(50) NULL DEFAULT 'PRIVATE', -- PRIVATE, READ_ONLY, EDITABLE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE document_permissions (
    id SERIAL PRIMARY KEY,
    document_id UUID NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(50) NOT NULL, -- READER, EDITOR
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (document_id, user_id)
);

CREATE TABLE document_files (
    id SERIAL PRIMARY KEY,
    document_id UUID NOT NULL,
    file_path TEXT NOT NULL, -- Путь к файлу (изображение, вложение)
    encrypted BOOLEAN DEFAULT TRUE, -- Файл зашифрован?
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);
