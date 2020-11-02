package la.crima.torsofan;

import la.crima.torsofan.config.WebSocketConfigure;

public class SpringSocketIO {

    public static void run(Class<?> mainClass, WebSocketConfigure configure) throws Exception {
        configure.start(mainClass);
    }

}
