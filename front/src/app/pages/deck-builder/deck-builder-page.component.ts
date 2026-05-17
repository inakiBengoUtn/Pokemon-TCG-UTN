import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardApiService } from './services/card-api.service';
import { DeckApiService } from './services/deck-api.service';
import { CardResponse, Supertype } from './models/card.model';
import { DeckEntry, DeckSummaryResponse, DeckValidationResponse } from './models/deck.model';

@Component({
  selector: 'app-deck-builder-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './deck-builder-page.component.html',
  styleUrl: './deck-builder-page.component.css'
})
export class DeckBuilderPageComponent implements OnInit {
  private cardApi = inject(CardApiService);
  private deckApi = inject(DeckApiService);

  // --- estado de búsqueda ---
  searchName = '';
  selectedSupertype: string = '';
  cards = signal<CardResponse[]>([]);
  loadingCards = signal(false);
  syncing = signal(false);
  cacheReady = signal(false);

  // --- estado del deck actual ---
  deckName = '';
  deckEntries = signal<DeckEntry[]>([]);
  editingDeckId: string | null = null;
  saving = signal(false);

  // --- lista de mazos guardados ---
  savedDecks = signal<DeckSummaryResponse[]>([]);

  // --- validación reactiva ---
  validation = computed<DeckValidationResponse>(() => {
    const entries = this.deckEntries();
    const total = entries.reduce((s, e) => s + e.quantity, 0);
    const errors: string[] = [];

    if (total !== 60) {
      errors.push(`El mazo debe tener exactamente 60 cartas (tiene ${total}).`);
    }

    // max 4 copias (excepto Energía Básica)
    for (const entry of entries) {
      if (!entry.card.isBasicEnergy && entry.quantity > 4) {
        errors.push(`Máximo 4 copias de "${entry.card.name}" (tiene ${entry.quantity}).`);
      }
    }

    // max 1 AS TÁCTICO
    const aceTacticoTotal = entries
      .filter(e => e.card.isAceTactico)
      .reduce((s, e) => s + e.quantity, 0);
    if (aceTacticoTotal > 1) {
      errors.push('Solo puede haber 1 carta de AS TÁCTICO en el mazo.');
    }

    // al menos 1 Pokémon Básico
    const hasBasic = entries.some(
      e => e.card.supertype === 'POKEMON' && e.card.subtypes?.includes('Basic')
    );
    if (!hasBasic && total > 0) {
      errors.push('El mazo debe contener al menos 1 Pokémon Básico.');
    }

    return { valid: errors.length === 0, totalCards: total, errors };
  });

  totalCards = computed(() => this.deckEntries().reduce((s, e) => s + e.quantity, 0));

  ngOnInit() {
    this.checkCacheAndLoad();
    this.loadSavedDecks();
  }

  checkCacheAndLoad() {
    this.cardApi.getCacheStatus().subscribe(status => {
      if (status.cached) {
        this.cacheReady.set(true);
        this.searchCards();
      }
    });
  }

  syncCards() {
    this.syncing.set(true);
    this.cardApi.syncSet().subscribe({
      next: () => {
        this.cacheReady.set(true);
        this.syncing.set(false);
        this.searchCards();
      },
      error: () => this.syncing.set(false)
    });
  }

  searchCards() {
    this.loadingCards.set(true);
    this.cardApi.search('xy1', this.searchName, this.selectedSupertype).subscribe({
      next: cards => {
        this.cards.set(cards);
        this.loadingCards.set(false);
      },
      error: () => this.loadingCards.set(false)
    });
  }

  addCard(card: CardResponse) {
    const entries = this.deckEntries();
    const existing = entries.find(e => e.card.id === card.id);

    if (existing) {
      const maxQty = card.isBasicEnergy ? 999 : card.isAceTactico ? 1 : 4;
      if (existing.quantity >= maxQty) return;
      this.deckEntries.set(entries.map(e =>
        e.card.id === card.id ? { ...e, quantity: e.quantity + 1 } : e
      ));
    } else {
      this.deckEntries.set([...entries, { card, quantity: 1 }]);
    }
  }

  removeCard(cardId: string) {
    const entries = this.deckEntries();
    const existing = entries.find(e => e.card.id === cardId);
    if (!existing) return;

    if (existing.quantity <= 1) {
      this.deckEntries.set(entries.filter(e => e.card.id !== cardId));
    } else {
      this.deckEntries.set(entries.map(e =>
        e.card.id === cardId ? { ...e, quantity: e.quantity - 1 } : e
      ));
    }
  }

  removeAllOfCard(cardId: string) {
    this.deckEntries.set(this.deckEntries().filter(e => e.card.id !== cardId));
  }

  saveDeck() {
    if (!this.deckName.trim()) return;
    this.saving.set(true);

    const request = {
      name: this.deckName.trim(),
      cards: this.deckEntries().map(e => ({ cardId: e.card.id, quantity: e.quantity }))
    };

    const obs = this.editingDeckId
      ? this.deckApi.updateDeck(this.editingDeckId, request)
      : this.deckApi.createDeck(request);

    obs.subscribe({
      next: () => {
        this.saving.set(false);
        this.newDeck();
        this.loadSavedDecks();
      },
      error: () => this.saving.set(false)
    });
  }

  loadDeck(id: string) {
    this.deckApi.getDeck(id).subscribe(deck => {
      this.editingDeckId = deck.id;
      this.deckName = deck.name;
      this.deckEntries.set(deck.cards.map(dc => ({ card: dc.card, quantity: dc.quantity })));
    });
  }

  deleteDeck(id: string) {
    this.deckApi.deleteDeck(id).subscribe(() => {
      if (this.editingDeckId === id) this.newDeck();
      this.loadSavedDecks();
    });
  }

  newDeck() {
    this.editingDeckId = null;
    this.deckName = '';
    this.deckEntries.set([]);
  }

  private loadSavedDecks() {
    this.deckApi.getDecks().subscribe(decks => this.savedDecks.set(decks));
  }

  cardQuantityInDeck(cardId: string): number {
    return this.deckEntries().find(e => e.card.id === cardId)?.quantity ?? 0;
  }

  maxReached(card: CardResponse): boolean {
    const qty = this.cardQuantityInDeck(card.id);
    if (card.isBasicEnergy) return false;
    if (card.isAceTactico) return qty >= 1;
    return qty >= 4;
  }

  getEnergyTypeClass(type: string): string {
    const map: Record<string, string> = {
      Fire: 'energy-fire', Water: 'energy-water', Grass: 'energy-grass',
      Lightning: 'energy-lightning', Psychic: 'energy-psychic', Fighting: 'energy-fighting',
      Darkness: 'energy-darkness', Metal: 'energy-metal', Fairy: 'energy-fairy',
      Dragon: 'energy-dragon', Colorless: 'energy-colorless'
    };
    return map[type] ?? '';
  }
}
