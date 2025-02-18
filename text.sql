CREATE TABLE IF NOT EXISTS document_diffs (
    id UUID,
    document_id UUID,
    diff_content String,
    previous_diff_id UUID,
    diff_processed Boolean DEFAULT 0,
    created_at DateTime DEFAULT now()
) ENGINE = MergeTree()
ORDER BY (document_id, created_at);

CREATE TABLE IF NOT EXISTS document_snapshots (
    id UUID,
    document_id UUID,
    snapshot_path String,
    applied_diff_id UUID,
    created_at DateTime DEFAULT now()
) ENGINE = MergeTree()
ORDER BY (document_id, created_at);
