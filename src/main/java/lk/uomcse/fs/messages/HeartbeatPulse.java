package lk.uomcse.fs.messages;

import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

/**
 * Describes the Heartbeat Message Format {@code {@link HeartbeatPulse}}.
 *
 * @author Dulanjaya
 * @since 10/23/2017
 */
public class HeartbeatPulse implements IMessage {
    public static final String ID = "HBPULSE";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" HBPULSE");
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }

    /**
     * Parses unregister response message
     *
     * @param reply reply in string
     * @return Unregister response message
     */
    public static HeartbeatPulse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        return new HeartbeatPulse();
    }
}
