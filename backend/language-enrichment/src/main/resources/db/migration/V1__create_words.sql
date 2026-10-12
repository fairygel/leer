CREATE TABLE "words"
(
    "id"         UUID PRIMARY KEY,
    "text"       varchar     NOT NULL,
    "language"   varchar     NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
