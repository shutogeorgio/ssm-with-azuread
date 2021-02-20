CREATE TABLE IF NOT EXISTS tags(
    id uuid PRIMARY KEY,
    item_id uuid,
    type VARCHAR(100)
);