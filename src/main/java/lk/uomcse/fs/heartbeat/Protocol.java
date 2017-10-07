package lk.uomcse.fs.heartbeat;

/**
 * Implements the Heart Beat Protocol.
 *
 * @author Dulanjaya Tennekoon
 * @since Phase1
 */
final class Protocol {
    /**
     * Heartbeat identifier.
     */
    private static final String HB = "HB";

    /**
     * Pulse Identifier.
     */
    private static final String PULSE = "PULSE";

    /**
     * Message format of a Pulse.
     *
     * @return message as a byte array.
     */
    static byte[] getPulseData() {
        return (HB + '_' + PULSE).getBytes();
    }
}
