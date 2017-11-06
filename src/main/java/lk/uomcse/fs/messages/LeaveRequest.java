package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;

// length LEAVE IP_address port_no
public class LeaveRequest extends Message implements IRequest {
    public static final String ID = "LEAVE";
    private Node node;

    public static IMessage parse(String data) {
        return null;
    }

    /**
     * Returns ID
     *
     * @return ID of this message type
     */
    @Override
    public String getID() {
        return ID;
    }
}
