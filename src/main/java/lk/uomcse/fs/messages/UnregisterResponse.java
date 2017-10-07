package lk.uomcse.fs.messages;

import lk.uomcse.fs.utils.InvalidFormatException;

// length UNROK value
public class UnregisterResponse {
    // True: Success; False: Failed
    private boolean success;

    private UnregisterResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public static UnregisterResponse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals("UNROK"))
            throw new InvalidFormatException("Parsing failed due to not having correct type of message.");
        boolean success = response[2].equals("0");
        return new UnregisterResponse(success);
    }
}
