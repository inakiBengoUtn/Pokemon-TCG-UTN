export interface Card {
  id: string;
  name: string;
  type: 'Pokemon' | 'Trainer' | 'Energy';
  rarity: string;
  imageUrl: string;
  subtype?: string; // e.g., 'Basic', 'Stage 1', 'Supporter', 'Item'
  element?: 'Fire' | 'Water' | 'Lightning' | 'Psychic' | 'Grass' | 'Colorless' | 'None';
}

export interface DeckCard extends Card {
  count: number; // 1 to 4
}
