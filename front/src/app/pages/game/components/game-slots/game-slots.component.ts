import { Component } from '@angular/core';
import { BenchSlot } from './bench-slot/bench-slot.component';
import { ActiveSlot } from './active-slot/active-slot.component';

@Component({
  selector: 'game-slots',
  templateUrl: './game-slots.component.html',
  styleUrls: ['./game-slots.component.css'],
  standalone: true,
  imports: [BenchSlot, ActiveSlot],
})
export class GameSlots {}
