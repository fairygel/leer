ALTER TABLE cards
    ADD CONSTRAINT fk_cards_decks
        FOREIGN KEY (deck_id)
            REFERENCES decks (id)
            ON DELETE CASCADE;