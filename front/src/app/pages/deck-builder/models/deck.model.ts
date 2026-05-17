import { CardResponse } from './card.model';

export interface DeckCardResponse {
  card: CardResponse;
  quantity: number;
}

export interface DeckSummaryResponse {
  id: string;
  name: string;
  totalCards: number;
  valid: boolean;
  updatedAt: string;
}

export interface DeckResponse {
  id: string;
  name: string;
  ownerUsername: string;
  totalCards: number;
  cards: DeckCardResponse[];
  validation: DeckValidationResponse;
  createdAt: string;
  updatedAt: string;
}

export interface DeckValidationResponse {
  valid: boolean;
  totalCards: number;
  errors: string[];
}

export interface SaveDeckRequest {
  name: string;
  cards: { cardId: string; quantity: number }[];
}

// Estado local del builder (antes de guardar)
export interface DeckEntry {
  card: CardResponse;
  quantity: number;
}
