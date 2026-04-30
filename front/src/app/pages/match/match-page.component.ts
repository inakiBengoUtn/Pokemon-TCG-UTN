import { Component, inject } from '@angular/core';
import { StompService } from '../../shared/services/stomp.service';
import { Router } from '@angular/router';
import { GameSessionService } from '../../shared/services/game-session.service';

@Component({
  selector: 'match-page',
  templateUrl: './match-page.component.html',
  styleUrls: ['./match-page.component.css'],
  imports: [],
})
export class MatchPage {
  private stompService = inject(StompService);
  private router = inject(Router);
  private gameSessionService = inject(GameSessionService);

  ngOnInit() {
    this.stompService.onConnect((frame) => {
      this.stompService.suscribe('/user/queue/match', (data: Game) => {
        this.gameSessionService.saveSession(data.match_id);
        this.router.navigate(['game']);
      });
    });
  }
}
