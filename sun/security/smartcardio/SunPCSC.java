package sun.security.smartcardio;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

public final class SunPCSC
  extends Provider
{
  private static final long serialVersionUID = 6168388284028876579L;
  
  public SunPCSC()
  {
    super("SunPCSC", 1.8D, "Sun PC/SC provider");
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        put("TerminalFactory.PC/SC", "sun.security.smartcardio.SunPCSC$Factory");
        return null;
      }
    });
  }
  
  public static final class Factory
    extends TerminalFactorySpi
  {
    public Factory(Object paramObject)
      throws PCSCException
    {
      if (paramObject != null) {
        throw new IllegalArgumentException("SunPCSC factory does not use parameters");
      }
      PCSC.checkAvailable();
      PCSCTerminals.initContext();
    }
    
    protected CardTerminals engineTerminals()
    {
      return new PCSCTerminals();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\smartcardio\SunPCSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */