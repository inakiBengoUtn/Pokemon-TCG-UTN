import { Component, inject, OnInit } from '@angular/core';
import { HeaderDeck } from "./components/heder-deck/header-deck.component";
import { DeckService } from '../services/deck.service'
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'deck-edit-page',
  templateUrl: './deck-edit-page.component.html',
  styleUrl: './deck-edit-page.component.css',
  imports: [HeaderDeck],
})
export class DeckEditPage implements OnInit {
  deckService = inject(DeckService)
  private route = inject(ActivatedRoute)

  ngOnInit(): void {
    this.route.paramMap.subscribe(param => {
      const id = param.get("id");
      this.deckService.getCurrentDeck(id!)
    })
  }

}