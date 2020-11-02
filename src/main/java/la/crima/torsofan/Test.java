package la.crima.torsofan;

import la.crima.torsofan.annotations.SocketBody;
import la.crima.torsofan.annotations.SocketController;
import la.crima.torsofan.annotations.SocketMapping;
import la.crima.torsofan.config.WebSocketConfigure;
import la.crima.torsofan.socket.SocketClient;

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
