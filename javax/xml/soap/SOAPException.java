package javax.xml.soap;

public class SOAPException
  extends Exception
{
  private Throwable cause;
  
  public SOAPException()
  {
    cause = null;
  }
  
  public SOAPException(String paramString)
  {
    super(paramString);
    cause = null;
  }
  
  public SOAPException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SOAPException(Throwable paramThrowable)
  {
    super(paramThrowable.toString());
    initCause(paramThrowable);
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (cause != null)) {
      return cause.getMessage();
    }
    return str;
  }
  
  public Throwable getCause()
  {
    return cause;
  }
  
  public synchronized Throwable initCause(Throwable paramThrowable)
  {
    if (cause != null) {
      throw new IllegalStateException("Can't override cause");
    }
    if (paramThrowable == this) {
      throw new IllegalArgumentException("Self-causation not permitted");
    }
    cause = paramThrowable;
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */