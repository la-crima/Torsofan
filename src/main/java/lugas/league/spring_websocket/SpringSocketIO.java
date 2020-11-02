package lugas.league.spring_websocket;

import lugas.league.spring_websocket.config.WebSocketConfigure;

public class SpringSocketIO {

    public static void run(Class<?> mainClass, WebSocketConfigure configure) throws Exception {
        configure.start(mainClass);
    }

}
