package lugas.league;

import la.crima.torsofan.SpringSocketIO;
import la.crima.torsofan.annotations.*;
import la.crima.torsofan.config.WebSocketConfigure;
import la.crima.torsofan.socket.SocketClient;

@SocketIOController
public class Test {

    public static void main(String[] args) throws Exception {
        WebSocketConfigure configure = webSocketConfigure();
        SpringSocketIO.run(Test.class, configure);
    }

    private static WebSocketConfigure webSocketConfigure() {
        return new WebSocketConfigure(8080);
    }

    @SocketConnectEvent
    public void connect(SocketClient client) {
        System.out.println("Connected: " + client.getSessionId());
    }

    @SocketDisconnectEvent
    public void disconnected(SocketClient client) {
        System.out.println("Disconnected: " + client.getSessionId());
    }

    @SocketMapping("joinRoom")
    public void test(@SocketBody String text, SocketClient client) {
        client.joinRoom(text);
    }

    @SocketMapping("send")
    public void send(@SocketBody Request request, SocketClient client) {
        client.sendToRoom(request.getRoom(), "receive", request.getText());
    }

}
