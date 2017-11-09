package lk.uomcse.fs.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

/**
 * Describes the Heartbeat Message Format {@code {@link HeartbeatPulse}}.
 *
 * @author Dulanjaya
 * @since 10/23/2017
 */
@JsonIgnoreProperties(value = {"receivedTime"}, ignoreUnknown = true)
public class HeartbeatPulse extends Message implements IMessage {
    public static final String ID = "HBPULSE";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" HBPULSE");
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }

    /**
     * Parses Heartbeat pulse response message
     *
     * @param reply reply in string
     * @return Heartbeat pulse message
     */
    public static HeartbeatPulse parse(String reply) throws InvalidFormatException {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length != 2)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        return new HeartbeatPulse();
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
