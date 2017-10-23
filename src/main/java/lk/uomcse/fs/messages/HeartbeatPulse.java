package lk.uomcse.fs.messages;

/**
 * Describes the Heartbeat Message Format {@code {@link HeartbeatPulse}}.
 *
 * @author Dulanjaya
 * @since 10/23/2017
 */
public class HeartbeatPulse implements IMessage{
    public static final String ID = "HBPULSE";

    @Override
    public String toString() {
        return ID;
    }
}
