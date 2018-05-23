package javax.smartcardio;

public class CardNotPresentException
  extends CardException
{
  private static final long serialVersionUID = 1346879911706545215L;
  
  public CardNotPresentException(String paramString)
  {
    super(paramString);
  }
  
  public CardNotPresentException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public CardNotPresentException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CardNotPresentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */