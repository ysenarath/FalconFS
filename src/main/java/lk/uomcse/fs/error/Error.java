package lk.uomcse.fs.error;

import lk.uomcse.fs.messages.IResponse;

public abstract class Error implements IResponse {

    protected int ErrorCode;
    protected String ErrorMessage;

    public abstract void handleError();

}
