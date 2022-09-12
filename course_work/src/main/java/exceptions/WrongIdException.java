package exceptions;

@SuppressWarnings("serial")
public class WrongIdException extends Exception {
    private String str;
    public WrongIdException(String str){
        super(str);
        this.str = str;
    }
    public String getMessage(){return str;}
}