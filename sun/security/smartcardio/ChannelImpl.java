package sun.security.smartcardio;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AccessController;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import sun.security.action.GetPropertyAction;

final class ChannelImpl
  extends CardChannel
{
  private final CardImpl card;
  private final int channel;
  private volatile boolean isClosed;
  private static final boolean t0GetResponse = getBooleanProperty("sun.security.smartcardio.t0GetResponse", true);
  private static final boolean t1GetResponse = getBooleanProperty("sun.security.smartcardio.t1GetResponse", true);
  private static final boolean t1StripLe = getBooleanProperty("sun.security.smartcardio.t1StripLe", false);
  private static final byte[] B0 = new byte[0];
  
  ChannelImpl(CardImpl paramCardImpl, int paramInt)
  {
    card = paramCardImpl;
    channel = paramInt;
  }
  
  void checkClosed()
  {
    card.checkState();
    if (isClosed) {
      throw new IllegalStateException("Logical channel has been closed");
    }
  }
  
  public Card getCard()
  {
    return card;
  }
  
  public int getChannelNumber()
  {
    checkClosed();
    return channel;
  }
  
  private static void checkManageChannel(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 4) {
      throw new IllegalArgumentException("Command APDU must be at least 4 bytes long");
    }
    if ((paramArrayOfByte[0] >= 0) && (paramArrayOfByte[1] == 112)) {
      throw new IllegalArgumentException("Manage channel command not allowed, use openLogicalChannel()");
    }
  }
  
  public ResponseAPDU transmit(CommandAPDU paramCommandAPDU)
    throws CardException
  {
    checkClosed();
    card.checkExclusive();
    byte[] arrayOfByte1 = paramCommandAPDU.getBytes();
    byte[] arrayOfByte2 = doTransmit(arrayOfByte1);
    return new ResponseAPDU(arrayOfByte2);
  }
  
  public int transmit(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
    throws CardException
  {
    checkClosed();
    card.checkExclusive();
    if ((paramByteBuffer1 == null) || (paramByteBuffer2 == null)) {
      throw new NullPointerException();
    }
    if (paramByteBuffer2.isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    if (paramByteBuffer1 == paramByteBuffer2) {
      throw new IllegalArgumentException("command and response must not be the same object");
    }
    if (paramByteBuffer2.remaining() < 258) {
      throw new IllegalArgumentException("Insufficient space in response buffer");
    }
    byte[] arrayOfByte1 = new byte[paramByteBuffer1.remaining()];
    paramByteBuffer1.get(arrayOfByte1);
    byte[] arrayOfByte2 = doTransmit(arrayOfByte1);
    paramByteBuffer2.put(arrayOfByte2);
    return arrayOfByte2.length;
  }
  
  private static boolean getBooleanProperty(String paramString, boolean paramBoolean)
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null) {
      return paramBoolean;
    }
    if (str.equalsIgnoreCase("true")) {
      return true;
    }
    if (str.equalsIgnoreCase("false")) {
      return false;
    }
    throw new IllegalArgumentException(paramString + " must be either 'true' or 'false'");
  }
  
  private byte[] concat(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
  {
    int i = paramArrayOfByte1.length;
    if ((i == 0) && (paramInt == paramArrayOfByte2.length)) {
      return paramArrayOfByte2;
    }
    byte[] arrayOfByte = new byte[i + paramInt];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, i);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, i, paramInt);
    return arrayOfByte;
  }
  
  private byte[] doTransmit(byte[] paramArrayOfByte)
    throws CardException
  {
    try
    {
      checkManageChannel(paramArrayOfByte);
      setChannel(paramArrayOfByte);
      int i = paramArrayOfByte.length;
      int j = card.protocol == 1 ? 1 : 0;
      int k = card.protocol == 2 ? 1 : 0;
      if ((j != 0) && (i >= 7) && (paramArrayOfByte[4] == 0)) {
        throw new CardException("Extended length forms not supported for T=0");
      }
      if (((j != 0) || ((k != 0) && (t1StripLe))) && (i >= 7))
      {
        m = paramArrayOfByte[4] & 0xFF;
        if (m != 0)
        {
          if (i == m + 6) {
            i--;
          }
        }
        else
        {
          m = (paramArrayOfByte[5] & 0xFF) << 8 | paramArrayOfByte[6] & 0xFF;
          if (i == m + 9) {
            i -= 2;
          }
        }
      }
      int m = ((j != 0) && (t0GetResponse)) || ((k != 0) && (t1GetResponse)) ? 1 : 0;
      int n = 0;
      byte[] arrayOfByte1 = B0;
      byte[] arrayOfByte2;
      int i1;
      for (;;)
      {
        n++;
        if (n >= 32) {
          throw new CardException("Could not obtain response");
        }
        arrayOfByte2 = PCSC.SCardTransmit(card.cardId, card.protocol, paramArrayOfByte, 0, i);
        i1 = arrayOfByte2.length;
        if ((m == 0) || (i1 < 2)) {
          break;
        }
        if ((i1 == 2) && (arrayOfByte2[0] == 108))
        {
          paramArrayOfByte[(i - 1)] = arrayOfByte2[1];
        }
        else
        {
          if (arrayOfByte2[(i1 - 2)] != 97) {
            break;
          }
          if (i1 > 2) {
            arrayOfByte1 = concat(arrayOfByte1, arrayOfByte2, i1 - 2);
          }
          paramArrayOfByte[1] = -64;
          paramArrayOfByte[2] = 0;
          paramArrayOfByte[3] = 0;
          paramArrayOfByte[4] = arrayOfByte2[(i1 - 1)];
          i = 5;
        }
      }
      arrayOfByte1 = concat(arrayOfByte1, arrayOfByte2, i1);
      return arrayOfByte1;
    }
    catch (PCSCException localPCSCException)
    {
      card.handleError(localPCSCException);
      throw new CardException(localPCSCException);
    }
  }
  
  private static int getSW(byte[] paramArrayOfByte)
    throws CardException
  {
    if (paramArrayOfByte.length < 2) {
      throw new CardException("Invalid response length: " + paramArrayOfByte.length);
    }
    int i = paramArrayOfByte[(paramArrayOfByte.length - 2)] & 0xFF;
    int j = paramArrayOfByte[(paramArrayOfByte.length - 1)] & 0xFF;
    return i << 8 | j;
  }
  
  private static boolean isOK(byte[] paramArrayOfByte)
    throws CardException
  {
    return (paramArrayOfByte.length == 2) && (getSW(paramArrayOfByte) == 36864);
  }
  
  private void setChannel(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte[0];
    if (i < 0) {
      return;
    }
    if ((i & 0xE0) == 32) {
      return;
    }
    if (channel <= 3)
    {
      int tmp30_29 = 0;
      byte[] tmp30_28 = paramArrayOfByte;
      tmp30_28[tmp30_29] = ((byte)(tmp30_28[tmp30_29] & 0xBC));
      int tmp40_39 = 0;
      byte[] tmp40_38 = paramArrayOfByte;
      tmp40_38[tmp40_39] = ((byte)(tmp40_38[tmp40_39] | channel));
    }
    else if (channel <= 19)
    {
      int tmp63_62 = 0;
      byte[] tmp63_61 = paramArrayOfByte;
      tmp63_61[tmp63_62] = ((byte)(tmp63_61[tmp63_62] & 0xB0));
      int tmp73_72 = 0;
      byte[] tmp73_71 = paramArrayOfByte;
      tmp73_71[tmp73_72] = ((byte)(tmp73_71[tmp73_72] | 0x40));
      int tmp82_81 = 0;
      byte[] tmp82_80 = paramArrayOfByte;
      tmp82_80[tmp82_81] = ((byte)(tmp82_80[tmp82_81] | channel - 4));
    }
    else
    {
      throw new RuntimeException("Unsupported channel number: " + channel);
    }
  }
  
  public void close()
    throws CardException
  {
    if (getChannelNumber() == 0) {
      throw new IllegalStateException("Cannot close basic logical channel");
    }
    if (isClosed) {
      return;
    }
    card.checkExclusive();
    try
    {
      byte[] arrayOfByte1 = { 0, 112, Byte.MIN_VALUE, 0 };
      arrayOfByte1[3] = ((byte)getChannelNumber());
      setChannel(arrayOfByte1);
      byte[] arrayOfByte2 = PCSC.SCardTransmit(card.cardId, card.protocol, arrayOfByte1, 0, arrayOfByte1.length);
      if (!isOK(arrayOfByte2)) {
        throw new CardException("close() failed: " + PCSC.toString(arrayOfByte2));
      }
    }
    catch (PCSCException localPCSCException)
    {
      card.handleError(localPCSCException);
      throw new CardException("Could not close channel", localPCSCException);
    }
    finally
    {
      isClosed = true;
    }
  }
  
  public String toString()
  {
    return "PC/SC channel " + channel;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\smartcardio\ChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */