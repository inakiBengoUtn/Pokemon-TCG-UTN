import { Component } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { NavHeader } from "./components/nav-header.component";

@Component({
    selector: 'lobby-layout',
    imports: [RouterOutlet, NavHeader],
    templateUrl: './lobby-layout.component.html',
    styleUrl: './lobby-layout.component.css',
})
export class LobbyLayout {

}