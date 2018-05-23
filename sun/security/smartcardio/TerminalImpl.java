package sun.security.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardPermission;
import javax.smartcardio.CardTerminal;

final class TerminalImpl
  extends CardTerminal
{
  final long contextId;
  final String name;
  private CardImpl card;
  
  TerminalImpl(long paramLong, String paramString)
  {
    contextId = paramLong;
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public synchronized Card connect(String paramString)
    throws CardException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new CardPermission(name, "connect"));
    }
    if (card != null)
    {
      if (card.isValid())
      {
        String str = card.getProtocol();
        if ((paramString.equals("*")) || (paramString.equalsIgnoreCase(str))) {
          return card;
        }
        throw new CardException("Cannot connect using " + paramString + ", connection already established using " + str);
      }
      card = null;
    }
    try
    {
      card = new CardImpl(this, paramString);
      return card;
    }
    catch (PCSCException localPCSCException)
    {
      if ((code == -2146434967) || (code == -2146435060)) {
        throw new CardNotPresentException("No card present", localPCSCException);
      }
      throw new CardException("connect() failed", localPCSCException);
    }
  }
  
  public boolean isCardPresent()
    throws CardException
  {
    try
    {
      int[] arrayOfInt = PCSC.SCardGetStatusChange(contextId, 0L, new int[] { 0 }, new String[] { name });
      return (arrayOfInt[0] & 0x20) != 0;
    }
    catch (PCSCException localPCSCException)
    {
      throw new CardException("isCardPresent() failed", localPCSCException);
    }
  }
  
  private boolean waitForCard(boolean paramBoolean, long paramLong)
    throws CardException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("timeout must not be negative");
    }
    if (paramLong == 0L) {
      paramLong = -1L;
    }
    int[] arrayOfInt = { 0 };
    String[] arrayOfString = { name };
    try
    {
      arrayOfInt = PCSC.SCardGetStatusChange(contextId, 0L, arrayOfInt, arrayOfString);
      boolean bool = (arrayOfInt[0] & 0x20) != 0;
      if (paramBoolean == bool) {
        return true;
      }
      long l = System.currentTimeMillis() + paramLong;
      while ((paramBoolean != bool) && (paramLong != 0L))
      {
        if (paramLong != -1L) {
          paramLong = Math.max(l - System.currentTimeMillis(), 0L);
        }
        arrayOfInt = PCSC.SCardGetStatusChange(contextId, paramLong, arrayOfInt, arrayOfString);
        bool = (arrayOfInt[0] & 0x20) != 0;
      }
      return paramBoolean == bool;
    }
    catch (PCSCException localPCSCException)
    {
      if (code == -2146435062) {
        return false;
      }
      throw new CardException("waitForCard() failed", localPCSCException);
    }
  }
  
  public boolean waitForCardPresent(long paramLong)
    throws CardException
  {
    return waitForCard(true, paramLong);
  }
  
  public boolean waitForCardAbsent(long paramLong)
    throws CardException
  {
    return waitForCard(false, paramLong);
  }
  
  public String toString()
  {
    return "PC/SC terminal " + name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\smartcardio\TerminalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */