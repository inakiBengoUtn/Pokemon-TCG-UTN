import { TestBed } from '@angular/core/testing';
import { DeckEditService } from './deck-edit.service';
import { Card } from '../models/card.model';

describe('DeckEditService', () => {
  let service: DeckEditService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DeckEditService],
    });
    service = TestBed.inject(DeckEditService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should seed initial cards on loadDeck', () => {
    service.loadDeck('1');
    expect(service.deckInfo()).toBeTruthy();
    expect(service.deckInfo()?.id).toBe('1');
    expect(service.deckCards().length).toBeGreaterThan(0);
    // Charizard deck card count check
    expect(service.cardCount()).toBe(11); // 2 Charizard + 4 Fire Energy + 3 Prof Research + 2 Nest Ball
  });

  it('should increment card count when adding a card already in the deck', () => {
    service.loadDeck('1');
    const existingCards = service.deckCards();
    const targetCard = existingCards[0];
    const initialCount = targetCard.count;

    service.addCard(targetCard);

    const updatedCard = service.deckCards().find((c) => c.id === targetCard.id);
    expect(updatedCard?.count).toBe(initialCount + 1);
  });

  it('should enforce the maximum limit of 4 copies per card', () => {
    service.loadDeck('1');
    const fireEnergy = service.deckCards().find((c) => c.id === 'c12');
    expect(fireEnergy?.count).toBe(4); // already at 4

    // Attempt to add a 5th copy
    service.addCard(fireEnergy as Card);

    const updatedFireEnergy = service.deckCards().find((c) => c.id === 'c12');
    expect(updatedFireEnergy?.count).toBe(4); // should still be 4
  });

  it('should decrement card count when removing a card', () => {
    service.loadDeck('1');
    const existingCards = service.deckCards();
    const targetCard = existingCards[0];
    const initialCount = targetCard.count; // should be 2 for Charizard

    service.removeCard(targetCard.id);

    const updatedCard = service.deckCards().find((c) => c.id === targetCard.id);
    expect(updatedCard?.count).toBe(initialCount - 1);
  });

  it('should completely remove card from deck if count decrements below 1', () => {
    service.loadDeck('3'); // Mewtwo deck seeds Boss's Orders with count 1
    const bossOrders = service.deckCards().find((c) => c.id === 'c9');
    expect(bossOrders?.count).toBe(1);

    service.removeCard('c9');

    const updatedBossOrders = service.deckCards().find((c) => c.id === 'c9');
    expect(updatedBossOrders).toBeUndefined();
  });

  it('should reactively update deck name', () => {
    service.loadDeck('1');
    service.updateDeckName('Fire Blast Elite');
    expect(service.deckInfo()?.name).toBe('Fire Blast Elite');
  });

  it('should filter catalog cards reactively by search query', () => {
    service.setSearchTerm('Pikachu');
    const filtered = service.filteredCatalogCards();
    expect(filtered.length).toBe(1);
    expect(filtered[0].name).toBe('Pikachu');
  });

  it('should filter catalog cards reactively by card type', () => {
    service.setSelectedType('Energy');
    const filtered = service.filteredCatalogCards();
    // All 5 basic energies
    expect(filtered.every((c) => c.type === 'Energy')).toBe(true);
    expect(filtered.length).toBe(5);
  });
});
