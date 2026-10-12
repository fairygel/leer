CREATE TABLE "definitions"
(
    "id"         UUID PRIMARY KEY,
    "word_id"    UUID        NOT NULL,
    "definition" varchar     NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);