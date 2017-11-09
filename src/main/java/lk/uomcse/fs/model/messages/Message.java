package lk.uomcse.fs.model.messages;

import lk.uomcse.fs.model.entity.Node;

public abstract class Message implements IMessage {
    private Node sender;

    private long receivedTime;

    public Message() {
        sender = null;
        receivedTime = 0;
    }

    /**
     * Sets sender
     *
     * @param sender sender of the message
     */
    @Override
    public void setSender(Node sender) {
        this.sender = sender;
    }


    /**
     * Sets received time
     *
     * @param receivedTime received time to set
     */
    @Override
    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * Gets message sender
     *
     * @return sender
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Gets message received time
     *
     * @return received time
     */
    public long getReceivedTime() {
        return receivedTime;
    }
}
