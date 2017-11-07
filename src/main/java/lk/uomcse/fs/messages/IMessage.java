package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;

public interface IMessage {
    /**
     * Returns string representation of the message according to standards provided
     *
     * @return string representation of this request
     */
    String toString();

    /**
     * Sets sender
     *
     * @param sender sender of the message
     */
    void setSender(Node sender);

    /**
     * Sets received time
     *
     * @param receivedTime received time to set
     */
    void setReceivedTime(long receivedTime);

    /**
     * Returns ID of the message
     *
     * @return
     */
    String getID();

    /**
     * Gets message sender
     *
     * @return sender
     */
    Node getSender();

    /**
     * Gets message received time
     *
     * @return received time
     */
    long getReceivedTime();
}
