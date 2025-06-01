-- V4__create_document_diffs_and_snapshots.sql

-- Ensure the uuid-ossp extension is enabled for UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table to store per-document diffs
CREATE TABLE document_diffs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    diff_content TEXT NOT NULL,
    previous_diff_id UUID NULL,
    diff_processed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_document_diffs_document
      FOREIGN KEY (document_id)
      REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_diffs_previous_diff
      FOREIGN KEY (previous_diff_id)
      REFERENCES document_diffs(id) ON DELETE SET NULL
);

-- Table to store document snapshots
CREATE TABLE document_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL,
    snapshot_path TEXT NOT NULL,
    applied_diff_id UUID NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snapshots_document
      FOREIGN KEY (document_id)
      REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_snapshots_applied_diff
      FOREIGN KEY (applied_diff_id)
      REFERENCES document_diffs(id) ON DELETE SET NULL
);
