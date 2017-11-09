package lk.uomcse.fs.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length LEAVEOK value
@JsonIgnoreProperties(value = {"receivedTime"}, ignoreUnknown = true)
public class LeaveResponse extends Message implements IResponse {
    public static final String ID = "LEAVEOK";

    private boolean success;

    /**
     * Used by Jakson
     */
    public LeaveResponse() {
        success = false;
    }

    public LeaveResponse(boolean success) {
        this.success = success;
    }

    /**
     * Parses leave response message
     *
     * @param data reply in string form
     * @return Leave response message
     */

    public static IMessage parse(String data) throws InvalidFormatException {
        if (data == null)
            throw new NullPointerException();
        String[] response = data.split(" ");
        if (response.length != 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        boolean success = response[2].equals("0");
        return new LeaveResponse(success);
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

    /**
     * Returns string representation of the message according to standards provided
     *
     * @return string representation of this request
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" ");
        sb.append(ID).append(" ");
        if (success)
            sb.append("0");
        else
            sb.append("9999");
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }

    /**
     * Whether the reply state is success
     *
     * @return reply state
     */
    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success state
     *
     * @param success success state
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
