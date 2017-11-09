package lk.uomcse.fs.utils.error;

public abstract class BootstrapError extends java.lang.Error {

    protected int ErrorCode;

    protected String ErrorMessage;

    public abstract void handleError();

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
