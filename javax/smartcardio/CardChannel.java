package javax.smartcardio;

import java.nio.ByteBuffer;

public abstract class CardChannel
{
  protected CardChannel() {}
  
  public abstract Card getCard();
  
  public abstract int getChannelNumber();
  
  public abstract ResponseAPDU transmit(CommandAPDU paramCommandAPDU)
    throws CardException;
  
  public abstract int transmit(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws CardException;
  
  public abstract void close()
    throws CardException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CardChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */