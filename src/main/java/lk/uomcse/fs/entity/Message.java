package lk.uomcse.fs.entity;


public abstract class Message {

    private long receivedTime;
    Node receiverNode;

    Message() {
        this.receivedTime = System.currentTimeMillis();
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public Node getReceiverNode() {
        return this.receiverNode;
    }

    public abstract String getMessage();
}
