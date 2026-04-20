import { Component } from '@angular/core';
import { GameScene } from './game-scene/game-scene.component';
import { GameSlots } from '../game-slots/game-slots.component';

@Component({
  selector: 'game-board',
  templateUrl: './game-board.component.html',
  styleUrls: ['./game-board.component.css'],
  imports: [GameScene, GameSlots],
})
export class GameBoard {}
