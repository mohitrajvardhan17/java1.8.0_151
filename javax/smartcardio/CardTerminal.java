package javax.smartcardio;

public abstract class CardTerminal
{
  protected CardTerminal() {}
  
  public abstract String getName();
  
  public abstract Card connect(String paramString)
    throws CardException;
  
  public abstract boolean isCardPresent()
    throws CardException;
  
  public abstract boolean waitForCardPresent(long paramLong)
    throws CardException;
  
  public abstract boolean waitForCardAbsent(long paramLong)
    throws CardException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CardTerminal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */