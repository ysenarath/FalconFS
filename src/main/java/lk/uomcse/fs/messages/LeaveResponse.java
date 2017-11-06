package lk.uomcse.fs.messages;

// length LEAVEOK value
public class LeaveResponse extends Message implements IResponse {
    public static final String ID = "LEAVEOK";
    int value;

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
