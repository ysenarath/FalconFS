package lk.uomcse.fs.messages;

import lk.uomcse.fs.utils.InvalidFormatException;

// length JOINOK value
public class JoinResponse implements IResponse {
    public static final String ID = "JOINOK";

    // True: Success; False: Failed
    private boolean success;

    /**
     * Constructor
     *
     * @param success corresponding request success state
     */
    private JoinResponse(boolean success) {
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
     * @param reply reply in string
     * @return Unregister response message
     */
    public static JoinResponse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException("Parsing failed due to not having correct type of message.");
        boolean success = response[2].equals("0");
        return new JoinResponse(success);
    }
}
