package lk.uomcse.fs.utils.error;

import lk.uomcse.fs.messages.IResponse;

public abstract class Error implements IResponse {

    protected int ErrorCode;
    protected String ErrorMessage;

    public abstract void handleError();

}
