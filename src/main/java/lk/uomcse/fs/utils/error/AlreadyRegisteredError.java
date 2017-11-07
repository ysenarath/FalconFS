package lk.uomcse.fs.utils.error;

import org.apache.log4j.Logger;

/**
 * Created by anuradha on 10/24/17.
 */
public class AlreadyRegisteredError extends BootstrapError {

    private final static Logger LOGGER = Logger.getLogger(AlreadyRegisteredError.class.getName());

    private AlreadyRegisteredError(Builder builder){
        this.ErrorCode = builder.errorCode;
        this.ErrorMessage = builder.error;
    }

    @Override
    public void handleError() {

        LOGGER.error(getErrorMessage());
    }

    public int getErrorCode(){
        return this.ErrorCode;
    }

    public String getErrorMessage(){
        return this.ErrorMessage;
    }


    public static class Builder{

        private int errorCode;
        private String error;

        public Builder(int errorCode){
            this.errorCode = errorCode;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public AlreadyRegisteredError build(){
            return new AlreadyRegisteredError(this);
        }
    }
}
