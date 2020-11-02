package la.crima.torsofan.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SocketServer extends SocketIOServer {

    public SocketServer(Configuration configuration) {
        super(configuration);
    }

    public <T> void addEvent(String eventName, Class<T> requestBody, Method method, Object methodInstance) {
        addEventListener(eventName, requestBody,
                (client, data, ackSender) -> {
                    List<Object> parameters = new ArrayList<>();
                    for (Class<?> parameterClass : method.getParameterTypes()) {
                        if (parameterClass.equals(SocketClient.class))
                            parameters.add(new SocketClient(this, client));
                        else if (parameterClass.equals(requestBody))
                            parameters.add(data);
                        else
                            parameters.add(null);
                    }

                    method.invoke(methodInstance, parameters.toArray());
                });
    }

}
