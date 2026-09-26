CREATE TABLE "cards"
(
    "id"             UUID PRIMARY KEY,
    "deck_id"        UUID NOT NULL,
    "question"       text NOT NULL,
    "answer"         text NOT NULL,
    "status"         card_statuses NOT NULL DEFAULT 'NEW',
    "last_viewed_at" TIMESTAMPTZ,
    "updated_at"     TIMESTAMPTZ NOT NULL,
    "created_at"     TIMESTAMPTZ NOT NULL
);