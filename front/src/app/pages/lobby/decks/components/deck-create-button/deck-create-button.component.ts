import { Component, output } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'deck-create-button',
  templateUrl: './deck-create-button.component.html',
  styleUrl: './deck-create-button.component.css',
  imports: [MatIcon],
})
export class DeckCreateButtonComponent {
  // Output reactivo usando la función output() de Angular moderno
  onCreate = output<void>();

  onClick(): void {
    this.onCreate.emit();
  }
}
