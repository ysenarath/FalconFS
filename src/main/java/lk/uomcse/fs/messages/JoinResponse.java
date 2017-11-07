package lk.uomcse.fs.messages;

import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length JOINOK value
public class JoinResponse extends Message implements IResponse {
    public static final String ID = "JOINOK";

    // True: Success; False: Failed
    private boolean success;

    /**
     * Constructor
     *
     * @param success corresponding request success state
     */
    public JoinResponse(boolean success) {
        this.success = success;
    }

    /**
     * Returns whether response is about successful request
     *
     * @return whether response is about successful request
     */
    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * Parses join response message
     *
     * @param data reply in string form
     * @return Join response message
     */
    public static JoinResponse parse(String data) {
        if (data == null)
            throw new NullPointerException();
        String[] response = data.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        boolean success = response[2].equals("0");
        return new JoinResponse(success);
    }

    /**
     * Returns string representation of the message according to standards provided
     *
     * @return string representation of this request
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" JOINOK ");
        if (success)
            sb.append("0");
        else
            sb.append("9999");
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
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
