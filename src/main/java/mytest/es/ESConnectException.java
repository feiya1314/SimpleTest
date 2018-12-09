package mytest.es;

public class ESConnectException extends RuntimeException {
    public ESConnectException(){
        super();
    }
    public ESConnectException(String message){
        super(message);
    }
}
