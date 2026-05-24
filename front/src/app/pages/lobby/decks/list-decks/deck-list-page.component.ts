import { Component, OnInit, inject, signal } from '@angular/core';
import { DeckService, Deck } from '../services/deck.service';
import { DeckComponent } from '../components/deck/deck.component';
import { DeckCreateButtonComponent } from '../components/deck-create-button/deck-create-button.component';

@Component({
  selector: 'decks-page',
  templateUrl: './deck-list-page.component.html',
  styleUrl: './deck-list-page.component.css',
  imports: [DeckComponent, DeckCreateButtonComponent],
})
export class DecksListPage implements OnInit {
  // Inyección de servicios usando el inyector moderno de Angular 21 (SRP)
  private deckService = inject(DeckService);
  decks = this.deckService.decks;
  selectedDeckId = this.deckService.selectedDeckId;

  ngOnInit(): void {
    this.deckService.loadDecks();
  }

  handleCreateDeck() {
  }

  handleSelectDeck(event: String) {
  }

  handleEditDeck(event: Deck) {
  }

  handleDeleteDeck(event: String) {
  }
}