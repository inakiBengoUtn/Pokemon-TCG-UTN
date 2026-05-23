import { Component, OnInit, inject } from '@angular/core';
import { DeckService, Deck } from './services/deck.service';
import { DeckComponent } from './components/deck/deck.component';
import { DeckCreateButtonComponent } from './components/deck-create-button/deck-create-button.component';

@Component({
  selector: 'decks-page',
  templateUrl: './deck-page.component.html',
  styleUrl: './deck-page.component.css',
  imports: [DeckComponent, DeckCreateButtonComponent],
})
export class DecksPage implements OnInit {
  // Inyección de servicios usando el inyector moderno de Angular 21 (SRP)
  private deckService = inject(DeckService);

  // Exposición de señales del servicio a la vista
  decks = this.deckService.decks;
  selectedDeckId = this.deckService.selectedDeckId;

  constructor() {
    this.deckService.getDecks();
  }

  ngOnInit(): void {
    this.deckService.loadDecks();
  }

  handleCreateDeck(): void {
    const deckName = prompt('Introduce el nombre del nuevo mazo:');
    if (deckName && deckName.trim()) {
      this.deckService.createDeck(deckName.trim()).then(() => {
        console.log('Mazo creado con éxito');
      }).catch(err => {
        alert('Error al crear mazo: ' + err.message);
      });
    }
  }

  handleSelectDeck(id: string): void {
    this.deckService.selectDeck(id).catch(err => {
      alert('Error al seleccionar mazo: ' + err.message);
    });
  }

  handleEditDeck(deck: Deck): void {
    const newName = prompt('Editar nombre del mazo:', deck.name);
    if (newName && newName.trim() && newName.trim() !== deck.name) {
      this.deckService.updateDeck(deck.id, newName.trim()).then(() => {
        console.log('Mazo actualizado con éxito');
      }).catch(err => {
        alert('Error al actualizar mazo: ' + err.message);
      });
    }
  }

  handleDeleteDeck(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar este mazo?')) {
      this.deckService.deleteDeck(id).then(() => {
        console.log('Mazo eliminado con éxito');
      }).catch(err => {
        alert('Error al eliminar mazo: ' + err.message);
      });
    }
  }
}