package lugas.league.spring_websocket;

import lugas.league.spring_websocket.annotations.SocketBody;
import lugas.league.spring_websocket.annotations.SocketController;
import lugas.league.spring_websocket.annotations.SocketMapping;
import lugas.league.spring_websocket.config.WebSocketConfigure;
import lugas.league.spring_websocket.socket.SocketClient;

@SocketController
public class Test {

    public static void main(String[] args) throws Exception {
        WebSocketConfigure configure = webSocketConfigure();
        SpringSocketIO.run(Test.class, configure);
    }

    private static WebSocketConfigure webSocketConfigure() {
        return new WebSocketConfigure(8080);
    }

    @SocketMapping("joinRoom")
    public void test(@SocketBody String text, Integer asd, SocketClient client) {
        System.out.println(text);
        System.out.println(asd);
        System.out.println(client);
    }

}
