import { Injectable } from '@angular/core';
import { Client, IFrame, Message } from '@stomp/stompjs';
import { BehaviorSubject, filter, firstValueFrom, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StompService {
  private client: Client;
  private connectionState$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      //debug: (str) => console.log(str), // solo para debugear
    });

    this.client.activate();
  }

  /**
   * Se suscribe a un topic y ejecuta un callback cuando hay un mensaje.
   * @param topic El topic al que se quiere suscribir.
   * @param callback La función que se ejecutará cuando haya un mensaje.
   */
  suscribe(topic: string, callback: (message: any) => void): void {
    this.client.subscribe(topic, (message) => {
      callback(JSON.parse(message.body));
    });
  }

  /**
   * Envía un mensaje a un topic.
   * @param topic El topic al que se quiere enviar el mensaje.
   * @param message El mensaje que se quiere enviar.
   */
  send(topic: string, message: any): void {
    this.client.publish({ destination: topic, body: JSON.stringify(message) });
  }

  /**
   * Desconecta el cliente del servidor.
   */
  desconect() {
    this.client.deactivate();
  }

  /**
   * Se ejecuta cuando el cliente se conecta al servidor.
   * @param callback La función que se ejecutará cuando el cliente se conecte al servidor.
   */
  onConnect(callback: (frame: IFrame) => void) {
    this.client.onConnect = (f: IFrame) => {
      callback(f);
    };
  }

  /**
   * Se ejecuta cuando el cliente recibe un error.
   * @param callback La función que se ejecutará cuando el cliente reciba un error.
   */
  onError(callback: (frame: IFrame) => void) {
    this.client.onWebSocketError = (f: IFrame) => {
      callback(f);
    };
  }

  /**
   * Se ejecuta cuando el cliente se desconecta del servidor.
   * @param callback La función que se ejecutará cuando el cliente se desconecte del servidor.
   */
  onDisconnect(callback: (frame: IFrame) => void) {
    this.client.onDisconnect = (f: IFrame) => {
      callback(f);
    };
  }

  /**
   * Devuelve un observable que emite true cuando el cliente está conectado.
   * @returns Un observable que emite true cuando el cliente está conectado.
   */
  isConnected(): Observable<boolean> {
    return this.connectionState$.asObservable();
  }
}
