package lugas.league;

public class Request {
    private String room;
    private String name;
    private String text;

    public Request() {
    }

    public Request(String room, String name, String text) {
        this.room = room;
        this.name = name;
        this.text = text;
    }

    public String getRoom() {
        return room;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Request{" +
                "room='" + room + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
