package lk.uomcse.fs.messages;

import lk.uomcse.fs.utils.InvalidFormatException;

// length UNROK value
public class UnregisterResponse implements IResponse {
    public static final String ID = "UNROK";

    // True: Success; False: Failed
    private boolean success;

    /**
     * Constructor
     *
     * @param success corresponding request success state
     */
    private UnregisterResponse(boolean success) {
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
     * Parses unregister response message
     *
     * @param reply reply in string
     * @return Unregister response message
     */
    public static UnregisterResponse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        boolean success = response[2].equals("0");
        return new UnregisterResponse(success);
    }
}
