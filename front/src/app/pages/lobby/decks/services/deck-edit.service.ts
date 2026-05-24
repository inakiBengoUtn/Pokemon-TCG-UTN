import { Injectable, signal, computed } from '@angular/core';
import { Card, DeckCard } from '../models/card.model';
import { Deck } from './deck.service';

@Injectable({
  providedIn: 'root',
})
export class DeckEditService {
  // Catalog Cards Database (immutable list)
  private _catalogCards = signal<Card[]>([
    {
      id: 'c1',
      name: 'Charizard ex',
      type: 'Pokemon',
      element: 'Fire',
      rarity: 'Double Rare',
      imageUrl: 'https://images.pokemontcg.io/sv3/125_hires.png',
      subtype: 'Stage 2',
    },
    {
      id: 'c2',
      name: 'Pikachu',
      type: 'Pokemon',
      element: 'Lightning',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/cel25/2_hires.png',
      subtype: 'Basic',
    },
    {
      id: 'c3',
      name: 'Mewtwo ex',
      type: 'Pokemon',
      element: 'Psychic',
      rarity: 'Ultra Rare',
      imageUrl: 'https://images.pokemontcg.io/sv4pt5/205_hires.png',
      subtype: 'Basic',
    },
    {
      id: 'c4',
      name: 'Blastoise ex',
      type: 'Pokemon',
      element: 'Water',
      rarity: 'Double Rare',
      imageUrl: 'https://images.pokemontcg.io/sv3p/5_hires.png',
      subtype: 'Stage 2',
    },
    {
      id: 'c5',
      name: 'Venusaur ex',
      type: 'Pokemon',
      element: 'Grass',
      rarity: 'Double Rare',
      imageUrl: 'https://images.pokemontcg.io/sv3p/2_hires.png',
      subtype: 'Stage 2',
    },
    {
      id: 'c6',
      name: 'Gardevoir ex',
      type: 'Pokemon',
      element: 'Psychic',
      rarity: 'Double Rare',
      imageUrl: 'https://images.pokemontcg.io/sv1/86_hires.png',
      subtype: 'Stage 2',
    },
    {
      id: 'c7',
      name: 'Lugia V',
      type: 'Pokemon',
      element: 'Colorless',
      rarity: 'Rare Holo',
      imageUrl: 'https://images.pokemontcg.io/sit/138_hires.png',
      subtype: 'Basic',
    },
    {
      id: 'c8',
      name: "Professor's Research",
      type: 'Trainer',
      element: 'None',
      rarity: 'Uncommon',
      imageUrl: 'https://images.pokemontcg.io/sv1/190_hires.png',
      subtype: 'Supporter',
    },
    {
      id: 'c9',
      name: "Boss's Orders",
      type: 'Trainer',
      element: 'None',
      rarity: 'Rare',
      imageUrl: 'https://images.pokemontcg.io/sv2/172_hires.png',
      subtype: 'Supporter',
    },
    {
      id: 'c10',
      name: 'Nest Ball',
      type: 'Trainer',
      element: 'None',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sv1/181_hires.png',
      subtype: 'Item',
    },
    {
      id: 'c11',
      name: 'Ultra Ball',
      type: 'Trainer',
      element: 'None',
      rarity: 'Uncommon',
      imageUrl: 'https://images.pokemontcg.io/sv1/196_hires.png',
      subtype: 'Item',
    },
    {
      id: 'c12',
      name: 'Basic Fire Energy',
      type: 'Energy',
      element: 'Fire',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sve/2_hires.png',
      subtype: 'Basic Energy',
    },
    {
      id: 'c13',
      name: 'Basic Lightning Energy',
      type: 'Energy',
      element: 'Lightning',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sve/4_hires.png',
      subtype: 'Basic Energy',
    },
    {
      id: 'c14',
      name: 'Basic Psychic Energy',
      type: 'Energy',
      element: 'Psychic',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sve/5_hires.png',
      subtype: 'Basic Energy',
    },
    {
      id: 'c15',
      name: 'Basic Water Energy',
      type: 'Energy',
      element: 'Water',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sve/3_hires.png',
      subtype: 'Basic Energy',
    },
    {
      id: 'c16',
      name: 'Basic Grass Energy',
      type: 'Energy',
      element: 'Grass',
      rarity: 'Common',
      imageUrl: 'https://images.pokemontcg.io/sve/1_hires.png',
      subtype: 'Basic Energy',
    },
  ]);

  // Reactive State signals
  private _deckInfo = signal<Deck | null>(null);
  private _deckCards = signal<DeckCard[]>([]);
  private _searchTerm = signal<string>('');
  private _selectedType = signal<string>('All');

  // Read-only public exposures
  catalogCards = this._catalogCards.asReadonly();
  deckInfo = this._deckInfo.asReadonly();
  deckCards = this._deckCards.asReadonly();
  searchTerm = this._searchTerm.asReadonly();
  selectedType = this._selectedType.asReadonly();

  // Computed signals
  cardCount = computed(() => {
    return this._deckCards().reduce((acc, card) => acc + card.count, 0);
  });

  filteredCatalogCards = computed(() => {
    const query = this._searchTerm().toLowerCase().trim();
    const type = this._selectedType();

    return this._catalogCards().filter((card) => {
      const matchesSearch =
        card.name.toLowerCase().includes(query) ||
        (card.subtype && card.subtype.toLowerCase().includes(query));
      const matchesType = type === 'All' || card.type === type;
      return matchesSearch && matchesType;
    });
  });

  /**
   * Load the active deck, generating rich mock values matching the ID.
   */
  loadDeck(deckId: string): void {
    // Generate beautiful mock deck covers based on deckId or standard
    let name = 'Charizard Master Deck';
    let coverImage = 'https://images.pokemontcg.io/sv3/125_hires.png';

    if (deckId === '2') {
      name = 'Lightning Spark Pikachu';
      coverImage = 'https://images.pokemontcg.io/cel25/2_hires.png';
    } else if (deckId === '3') {
      name = 'Psychic Nexus Mewtwo';
      coverImage = 'https://images.pokemontcg.io/sv4pt5/205_hires.png';
    }

    this._deckInfo.set({
      id: deckId,
      name,
      card_count: 0,
      cover_image: coverImage,
    });

    // Seed some initial cards into the deck for premium initial presentation
    const initialCards: DeckCard[] = [];
    const catalog = this._catalogCards();

    if (deckId === '1') {
      // Seed Charizard deck
      const charizard = catalog.find((c) => c.id === 'c1');
      const fireEnergy = catalog.find((c) => c.id === 'c12');
      const profResearch = catalog.find((c) => c.id === 'c8');
      const nestBall = catalog.find((c) => c.id === 'c10');

      if (charizard) initialCards.push({ ...charizard, count: 2 });
      if (fireEnergy) initialCards.push({ ...fireEnergy, count: 4 });
      if (profResearch) initialCards.push({ ...profResearch, count: 3 });
      if (nestBall) initialCards.push({ ...nestBall, count: 2 });
    } else if (deckId === '2') {
      // Seed Pikachu deck
      const pikachu = catalog.find((c) => c.id === 'c2');
      const electricEnergy = catalog.find((c) => c.id === 'c13');
      const ultraBall = catalog.find((c) => c.id === 'c11');

      if (pikachu) initialCards.push({ ...pikachu, count: 3 });
      if (electricEnergy) initialCards.push({ ...electricEnergy, count: 4 });
      if (ultraBall) initialCards.push({ ...ultraBall, count: 2 });
    } else {
      // Seed Mewtwo deck
      const mewtwo = catalog.find((c) => c.id === 'c3');
      const psychicEnergy = catalog.find((c) => c.id === 'c14');
      const bossOrders = catalog.find((c) => c.id === 'c9');

      if (mewtwo) initialCards.push({ ...mewtwo, count: 2 });
      if (psychicEnergy) initialCards.push({ ...psychicEnergy, count: 4 });
      if (bossOrders) initialCards.push({ ...bossOrders, count: 1 });
    }

    this._deckCards.set(initialCards);
  }

  /**
   * Add a card to the deck. Retains 4-copy rule limit.
   */
  addCard(card: Card): void {
    this._deckCards.update((currentCards) => {
      const existing = currentCards.find((c) => c.id === card.id);
      if (existing) {
        if (existing.count >= 4) {
          // Rule limit met. Return early.
          return currentCards;
        }
        return currentCards.map((c) =>
          c.id === card.id ? { ...c, count: c.count + 1 } : c
        );
      } else {
        return [...currentCards, { ...card, count: 1 }];
      }
    });

    this.updateDeckCountInInfo();
  }

  /**
   * Remove a card from the deck or decrement its count.
   */
  removeCard(cardId: string): void {
    this._deckCards.update((currentCards) => {
      const existing = currentCards.find((c) => c.id === cardId);
      if (!existing) return currentCards;

      if (existing.count <= 1) {
        return currentCards.filter((c) => c.id !== cardId);
      } else {
        return currentCards.map((c) =>
          c.id === cardId ? { ...c, count: c.count - 1 } : c
        );
      }
    });

    this.updateDeckCountInInfo();
  }

  /**
   * Reactively update deck name
   */
  updateDeckName(name: string): void {
    const currentInfo = this._deckInfo();
    if (currentInfo) {
      this._deckInfo.set({
        ...currentInfo,
        name,
      });
    }
  }

  /**
   * Search query updater
   */
  setSearchTerm(term: string): void {
    this._searchTerm.set(term);
  }

  /**
   * Selected card type filter updater
   */
  setSelectedType(type: string): void {
    this._selectedType.set(type);
  }

  /**
   * Keeps deck count in sync inside the deckInfo signal
   */
  private updateDeckCountInInfo(): void {
    const currentInfo = this._deckInfo();
    if (currentInfo) {
      this._deckInfo.set({
        ...currentInfo,
        card_count: this.cardCount(),
      });
    }
  }
}
