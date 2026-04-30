import { Component, inject } from '@angular/core';
import { GameScene } from './components/game-board/game-scene/game-scene.component';
import { GameBoard } from './components/game-board/game-board.component';
import { StompService } from '../../shared/services/stomp.service';

@Component({
  selector: 'game-page',
  templateUrl: './game-page.component.html',
  styleUrls: ['./game-page.component.css'],
  imports: [GameScene, GameBoard],
})
export class GamePage {
  stompService = inject(StompService);
}
