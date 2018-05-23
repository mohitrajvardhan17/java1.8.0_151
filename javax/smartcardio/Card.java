package javax.smartcardio;

public abstract class Card
{
  protected Card() {}
  
  public abstract ATR getATR();
  
  public abstract String getProtocol();
  
  public abstract CardChannel getBasicChannel();
  
  public abstract CardChannel openLogicalChannel()
    throws CardException;
  
  public abstract void beginExclusive()
    throws CardException;
  
  public abstract void endExclusive()
    throws CardException;
  
  public abstract byte[] transmitControlCommand(int paramInt, byte[] paramArrayOfByte)
    throws CardException;
  
  public abstract void disconnect(boolean paramBoolean)
    throws CardException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\Card.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */