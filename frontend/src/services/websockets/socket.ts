/*
 Step.1 
 This class defines the websocket functionalities (subscribe/publish/connect...etc)
*/

import { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { REACT_APP_HOST } from "../api";
import { Player } from "@src/types";

export class WebSocketService {
    // Instance of the WebSocket client used for managing the connection
    private client: Client;

    // Tracks whether the WebSocket connection is active
    private connected = false;

    // Stores active subscriptions with their topics and corresponding StompSubscription objects
    private subscriptions: { topic: string; subscription: StompSubscription }[] = [];

    constructor() {
        this.client = new Client({
            webSocketFactory: () => new SockJS(`${REACT_APP_HOST}/websocket`),
            connectHeaders: {},
            onConnect: this.onConnect,
            onDisconnect: this.onDisconnect,
        });
    }

    // Callback executed when WebSocket connection is successfully established
    private onConnect = () => {
        console.log("WebSocket connected");
        this.connected = true;
    };

    private onDisconnect = () => {
        console.log("WebSocket disconnected");
        this.connected = false;
    };

    // Initiates the WebSocket connection
    async connect(): Promise<WebSocketService> {
        return new Promise((resolve, reject) => {
            // If not already connected, set up onConnect and activate the client
            if (!this.connected) {
                this.client.onConnect = () => {
                    this.onConnect(); // Call the onConnect handler
                    resolve(this); // Resolve the promise once connected
                };
                this.client.onStompError = (frame) => {
                    console.error("Broker reported error: " + frame.headers["message"]);
                    console.error("Additional details: " + frame.body);
                };
                this.client.activate(); // Activate the WebSocket connection
            }
        });
    }

    /* 
        When player joins the game, we need to reconnect the sockets including the player id in headers,
        so in backend we can identify what sockets belong to which player.
    */
    addPlayer(player: Player) {
        this.client.connectHeaders = {
            id: String(player.id),
        };

        if (this.connected) {
            this.client.deactivate(); // Disconnect the current connection
            this.connected = false; // Update the connection state
        }

        this.connect(); // Reconnect with the updated headers
    }

    showSubscriptions() {
        this.subscriptions.forEach((sub) => {
            console.log(`Subscribed to ${sub.topic}`);
        });
    }

    public async subscribe(topic: string, callback: (message: IMessage) => void) {
        if (!this.connected) await this.connect();

        // Check if already subscribed to the topic
        const subscriptionIndex = this.subscriptions.findIndex((sub) => sub.topic === topic);
        if (subscriptionIndex != -1) {
            console.log(`Subscribe: Already subscribed to ${topic}`);
            return;
        }

        // Subscribe to the topic
        const subscription = this.client.subscribe(topic, callback);

        // Add the subscription to the list
        this.subscriptions.push({ topic, subscription });
        console.log(`DEBUG: Subscribed to ${topic}`);
    }

    public async unsubscribe(topic: string) {
        if (!this.connected) await this.connect();

        // Check if already subscribed to the topic
        const subscriptionIndex = this.subscriptions.findIndex((sub) => sub.topic === topic);
        if (subscriptionIndex === -1) {
            console.log(`DEBUG: Not subscribed to ${topic}`);
            return;
        }

        // Unsubscribe from the topic
        const { subscription } = this.subscriptions[subscriptionIndex];
        subscription.unsubscribe();

        // Removing the subscription from the list
        this.subscriptions.splice(subscriptionIndex, 1);
        console.log(`DEBUG: Unsubscribed from ${topic}`);
    }

    // Sends a message to the specified destination
    public async publish({
        destination,
        message,
    }: {
        destination: string;
        message: string | object;
    }) {
        if (!this.connected) await this.connect();

        console.log(`DEBUG: Publishing to ${destination}`);
        console.log(`DEBUG: PayLoad: ${JSON.stringify(message)}`);

        this.client.publish({
            destination: destination,
            body: JSON.stringify(message),
        });
    }

    public getClient() {
        return this.client;
    }


    public async updatePlayer(player: Player) {
        await this.publish({
            destination: `/app/game/${player.gameId}/player/update`,
            message: player
        });
        console.log("Player updated:", player);
    }
}

const instance = new WebSocketService();
export default instance;
