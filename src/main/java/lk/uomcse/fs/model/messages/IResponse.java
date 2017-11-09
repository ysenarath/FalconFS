package lk.uomcse.fs.model.messages;

public interface IResponse extends IMessage {
    /**
     * Returns whether response is about successful request
     *
     * @return whether response is about successful request
     */
    default boolean isSuccess() {
        return false;
    }
}
