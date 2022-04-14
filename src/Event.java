public class Event implements Comparable{

    public enum EventType{
        Arrive,
        Inspect
    }

    public int id;
    public int startTime;
    public int endTime;
    public EventType eventType;

    public Event (int id,int startTime,int endTime, EventType eventType) {

        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
