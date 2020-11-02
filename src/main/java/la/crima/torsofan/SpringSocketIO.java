package la.crima.torsofan;

import la.crima.torsofan.config.WebSocketConfigure;
import la.crima.torsofan.socket.SocketServer;

public class SpringSocketIO {

    public static SocketServer run(Class<?> mainClass, WebSocketConfigure configure) throws Exception {
        System.out.println("asd");
        return configure.start(mainClass);
    }

}
