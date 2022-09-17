package exceptions;

@SuppressWarnings("serial")
public class WrongNameException extends Exception{
	private String s;
	public WrongNameException(String s){
        super(s);
        this.s = s;
    }
    public String getMessage(){return s;}
}
