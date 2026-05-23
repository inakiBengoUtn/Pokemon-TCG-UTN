import { Component, Input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Deck } from '../../services/deck.service';

@Component({
  selector: 'deck',
  templateUrl: './deck.component.html',
  styleUrl: './deck.component.css',
  imports: [MatButtonModule, MatIcon],
})
export class DeckComponent {
  // Inputs
  @Input({ required: true }) deck!: Deck;
  @Input() isSelected = false;

  // Outputs reactivos (Angular 21)
  onSelect = output<string>();
  onEdit = output<Deck>();
  onDelete = output<string>();

  select(event: Event): void {
    // Previene que se disparen eventos de los botones internos
    const target = event.target as HTMLElement;
    if (target.closest('.action-btn')) {
      return;
    }
    this.onSelect.emit(this.deck.id);
  }

  edit(event: Event): void {
    event.stopPropagation();
    this.onEdit.emit(this.deck);
  }

  delete(event: Event): void {
    event.stopPropagation();
    this.onDelete.emit(this.deck.id);
  }
}