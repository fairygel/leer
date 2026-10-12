CREATE TABLE "translations"
(
    "id"          UUID PRIMARY KEY,
    "word_id"     UUID        NOT NULL,
    "translation" varchar     NOT NULL,
    "language"    varchar     NOT NULL,
    "created_at"  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);