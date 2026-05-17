export type Supertype = 'POKEMON' | 'ENERGY' | 'TRAINER';

export interface CardResponse {
  id: string;
  name: string;
  supertype: Supertype;
  subtypes: string[];
  hp: number | null;
  types: string[];
  evolvesFrom: string | null;
  retreatCost: number;
  weaknessType: string | null;
  weaknessValue: string | null;
  resistanceType: string | null;
  resistanceValue: string | null;
  attacksJson: string | null;
  abilitiesJson: string | null;
  imageUrlSmall: string | null;
  imageUrlLarge: string | null;
  setId: string;
  setName: string;
  number: string;
  rarity: string;
  isAceTactico: boolean;
  isBasicEnergy: boolean;
}

export interface Attack {
  name: string;
  cost: string[];
  convertedEnergyCost: number;
  damage: string;
  text: string;
}
