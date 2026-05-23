import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { RouterLinkActive } from "@angular/router";

@Component({
    selector: 'nav-header',
    standalone: true,
    imports: [RouterLink, RouterLinkActive],
    templateUrl: './nav-header.component.html',
    styleUrl: './nav-header.component.css'
})
export class NavHeader { }