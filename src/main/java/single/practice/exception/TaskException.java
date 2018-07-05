package single.practice.exception;

public class TaskException extends Exception{
    static final long serialVersionUID = 7818375128146090155L;

    public TaskException(){
        super();
    }

    public TaskException(String message)
    {
        super(message);
    }

    public TaskException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public TaskException(Throwable cause)
    {
        super(cause);
    }
}
