CREATE TABLE "decks"
(
    "id"             UUID PRIMARY KEY,
    "name"           varchar NOT NULL,
    "description"    varchar,
    "user_id"        UUID NOT NULL,
    "last_viewed_at" TIMESTAMPTZ,
    "updated_at"     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "created_at"     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);