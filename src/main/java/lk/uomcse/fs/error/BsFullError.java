package lk.uomcse.fs.error;

/**
 * Created by anuradha on 10/24/17.
 */
public class BsFullError extends Error {

    private BsFullError(Builder builder){
        this.ErrorCode = builder.errorCode;
        this.ErrorMessage = builder.error;
    }

    @Override
    public void handleError() {
        System.out.println(getErrorMessage());
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

        public BsFullError build(){
            return new BsFullError(this);
        }
    }
}
