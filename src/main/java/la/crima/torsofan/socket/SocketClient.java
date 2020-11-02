package la.crima.torsofan.socket;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SocketClient {

    private final SocketServer server;
    private final SocketIOClient client;

    public SocketClient(SocketServer server, SocketIOClient client) {
        this.server = server;
        this.client = client;
    }

    public void joinRoom(String roomName) {
        client.joinRoom(roomName);
    }

    public void leaveRoom(String roomName) {
        client.leaveRoom(roomName);
    }

    public void set(String key, Object value) {
        client.set(key, value);
    }

    public <T> T get(String name) {
        return client.get(name);
    }

    public Set<String> getRoomList() {
        return client.getAllRooms().stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public String getRemoteAddress() {
        return client.getRemoteAddress().toString();
    }

    public UUID getSessionId() {
        return client.getSessionId();
    }

    public String getUrlParam(String name) {
        return client.getHandshakeData().getSingleUrlParam(name);
    }

    public String getHeader(String key) {
        return client.getHandshakeData().getHttpHeaders().get(key);
    }

    public void sendToClient(String eventName, Object data) {
        client.sendEvent(eventName, data);
    }

    public void sendToClients(String eventName, Object data) {
        server.getAllClients().forEach(c -> c.sendEvent(eventName, data));
    }

    public void sendToRoom(String roomName, String eventName, Object data) {
        server.getRoomOperations(roomName).sendEvent(eventName, data);
    }

    public void disconnect() {
        client.disconnect();
    }

}
