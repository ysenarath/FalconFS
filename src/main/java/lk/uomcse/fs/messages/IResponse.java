package lk.uomcse.fs.messages;

public interface IResponse {
    /**
     * Returns whether response is about successful request
     *
     * @return whether response is about successful request
     */
    default boolean isSuccess() {
        return false;
    }
}
