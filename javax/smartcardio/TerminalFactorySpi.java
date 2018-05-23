package javax.smartcardio;

public abstract class TerminalFactorySpi
{
  protected TerminalFactorySpi() {}
  
  protected abstract CardTerminals engineTerminals();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\TerminalFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */