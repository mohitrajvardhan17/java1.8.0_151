package javax.smartcardio;

public class CardException
  extends Exception
{
  private static final long serialVersionUID = 7787607144922050628L;
  
  public CardException(String paramString)
  {
    super(paramString);
  }
  
  public CardException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public CardException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CardException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */