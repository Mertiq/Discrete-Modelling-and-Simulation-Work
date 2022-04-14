import java.text.DecimalFormat;
import java.util.*;

public class Program {

    private final PriorityQueue<Event> futureEventsList = new PriorityQueue<>(new EventComparator());
    private final PriorityQueue<Event> queue = new PriorityQueue<>(new EventComparator());
    private final ArrayList<Integer> queueLengths = new ArrayList<>();
    private final ArrayList<Integer> felLengths = new ArrayList<>();
    private boolean isInspectorBusy = false;

    private int time = 0;

    private static int id = 0;

    private int failedPartCounter = 0;

    public boolean isFinished = false;

    Random random = new Random();

    public static void main(String[] args) {

        Program program = new Program ();
        program.start_simulation();

    }

    public void start_simulation(){

        initialization_function();
        while (!isFinished) {
            Event.EventType eventType = time_advance_function();
            event_handling_function(eventType);
        }
        generate_report_function();

    }

    public void initialization_function(){
        time = 0;

        System.out.println("**********");
        System.out.println("time: " + time);
        System.out.println("System initialized");

        Event event = new Event(id, time, time + 5, Event.EventType.Arrive);

        System.out.println("initial arrival event ("+id+") generated and scheduled for t: " + (time+5));

        id++;
        futureEventsList.add(event);

        System.out.println("**********");
    }

    public Event.EventType time_advance_function(){
        assert futureEventsList.peek() != null;
        time = futureEventsList.peek().endTime;
        return futureEventsList.peek().eventType;
    }

    public void event_handling_function (Event.EventType eventType){
        switch (eventType){
            case Arrive:
                ArriveEvent();
                break;
            case Inspect:
                InspectEvent();
                break;
        }
    }

    public void ArriveEvent(){
        System.out.println("**********");
        System.out.println("time: " + time);

        int felID = Objects.requireNonNull(futureEventsList.peek()).id;

        System.out.println("part ("+felID+") arrived for inspection");

        queue.add(futureEventsList.poll());

        if(!isInspectorBusy){

            System.out.println("inspection starts");

            queue.poll();

            System.out.println("queue length: " + queue.size());
            queueLengths.add(queue.size());

            int inspectTime= random.nextInt (9) +2;
            int endTime = time + inspectTime;
            Event event = new Event(felID,time,endTime, Event.EventType.Inspect);

            System.out.println("part ("+felID+") scheduled to leave system at t: " + endTime);

            isInspectorBusy = true;
            futureEventsList.add(event);
        }

        Event event = new Event(id, time, time + 5, Event.EventType.Arrive);

        System.out.println("new arrival  ("+id+") generated and scheduled for: " + (time + 5));

        id++;
        futureEventsList.add(event);
        felLengths.add(futureEventsList.size());

        ContentsOfFel();

        System.out.println("**********");
    }

    public void InspectEvent(){

        System.out.println("**********");
        System.out.println("time: " + time);
        System.out.println("inspection completed");

        int felID = Objects.requireNonNull(futureEventsList.poll()).id;
        isInspectorBusy = false;

        int i = random.nextInt(100);
        if(i > 10){

            System.out.println("part ("+ felID +") is success.");

        }else{
            failedPartCounter++;

            System.out.println("part ("+ felID +") is faulty. Total: "+failedPartCounter);

            if(failedPartCounter >= 100){
                isFinished = !isFinished;
            }
        }

        if(!queue.isEmpty()){
            felID = queue.poll().id;

            int inspectTime= random.nextInt (9) +2;
            int endTime = time + inspectTime;
            Event event = new Event(felID,time,endTime, Event.EventType.Inspect);

            System.out.println("queue length: " + queue.size());

            queueLengths.add(queue.size());

            System.out.println("part ("+felID+") scheduled to leave system at t: " + endTime);

            isInspectorBusy = true;
            futureEventsList.add(event);
            felLengths.add(futureEventsList.size());
        }else{
            System.out.println("queue is empty");
        }

        System.out.println("**********");

    }
    public void generate_report_function(){

        int totalQueueLengths = 0;
        int maxQueueLength = 0;
        for (int i:queueLengths) {
            totalQueueLengths += i;
            if(maxQueueLength < i){
                maxQueueLength = i;
            }
        }
        int totalFelLength = 0;
        int maxFelLength = 0;
        for (int i:felLengths) {
            totalFelLength += i;
            if(maxFelLength < i){
                maxFelLength = i;
            }
        }

        System.out.println();
        System.out.println("\t\t\t\t\t\t\t\t\tREPORT\n");
        System.out.println("Total simulation time: " + time + "\n");
        System.out.println("Length of FEL when simulation ends: " + futureEventsList.size() + "\n");
        ContentsOfFel();
        System.out.println("Average fel length: "+ new DecimalFormat("##.##").format((float) totalFelLength/felLengths.size()).replace(',','.'));
        System.out.println("Maximum fel length: "+ maxFelLength + "\n");


        System.out.println("Length of queue when simulation ends: " + queue.size() + "\n");
        ContentsOfQueue();
        System.out.println("Average queue length: "+ new DecimalFormat("##.##").format((float) totalQueueLengths/queueLengths.size()).replace(',','.'));
        System.out.println("Maximum queue length: "+ maxQueueLength + "\n");
    }
    public void ContentsOfFel(){
        System.out.println("-------------");
        System.out.println("Future Event List\n");
        for (Event e:futureEventsList) {
            System.out.println("id: " + e.id + " | start time: " + e.startTime + " | end time: " + e.endTime + " | event type: " + e.eventType);
        }
        System.out.println("-------------\n");
    }
    public void ContentsOfQueue(){
        System.out.println("-------------");
        System.out.println("Queue\n");
        for (Event e:queue) {
            System.out.println("id: " + e.id + " | start time: " + e.startTime + " | end time: " + e.endTime + " | event type: " + e.eventType);
        }
        System.out.println("-------------\n");
    }
}


class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        if (e1.endTime > e2.endTime)
            return 1;
        else if (e1.endTime < e2.endTime)
            return -1;
        return 0;
    }
}