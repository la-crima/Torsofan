package lugas.league.spring_websocket.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocketServer extends SocketIOServer{

    public SocketServer(Configuration configuration) {
        super(configuration);
    }

    public <T> void addEvent(String eventName, Class<T> requestBody, Method method) {
        addEventListener(eventName, requestBody,
                (client, data, ackSender) -> {
                    List<Object> parameters = new ArrayList<>();
                    for (Class<?> parameterClass : method.getParameterTypes()) {
                        if (parameterClass.equals(SocketClient.class))
                            parameters.add(client);
                        else if (parameterClass.equals(requestBody))
                            parameters.add(data);
                        else
                            parameters.add(null);
                    }

                    method.invoke(method.getDeclaringClass().newInstance(), data, 5, null);
                });
    }

}
