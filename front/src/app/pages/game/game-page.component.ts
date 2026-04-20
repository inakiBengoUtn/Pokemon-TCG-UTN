import { Component } from '@angular/core';
import { GameScene } from './components/game-board/game-scene/game-scene.component';
import { GameBoard } from './components/game-board/game-board.component';

@Component({
  selector: 'game-page',
  templateUrl: './game-page.component.html',
  styleUrls: ['./game-page.component.css'],
  imports: [GameScene, GameBoard],
})
export class GamePage {}
