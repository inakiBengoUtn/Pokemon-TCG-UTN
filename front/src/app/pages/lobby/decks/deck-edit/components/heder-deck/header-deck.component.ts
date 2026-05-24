import { Component, Input } from "@angular/core";

@Component({
    selector: 'header-deck',
    templateUrl: './header-deck.component.html',
    styleUrl: './header-deck.component.css',
})
export class HeaderDeck {
    @Input() nameDeck: string = '';
    @Input() countCards: number = 0;
}