package exceptions;

@SuppressWarnings("serial")
public class WrongLastNameException extends Exception {
    private String str;
    public WrongLastNameException(String str){
        super(str);
        this.str = str;
    }
    public String getMessage(){return str;}

}
