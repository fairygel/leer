package me.fairygel.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeckController {
    @GetMapping("/api/v1/decks")
    public String getDecks() {
        return "decks";
    }
}
