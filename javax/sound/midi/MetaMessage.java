package javax.sound.midi;

public class MetaMessage
  extends MidiMessage
{
  public static final int META = 255;
  private int dataLength = 0;
  private static final long mask = 127L;
  
  public MetaMessage()
  {
    this(new byte[] { -1, 0 });
  }
  
  public MetaMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws InvalidMidiDataException
  {
    super(null);
    setMessage(paramInt1, paramArrayOfByte, paramInt2);
  }
  
  protected MetaMessage(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
    if (paramArrayOfByte.length >= 3)
    {
      dataLength = (paramArrayOfByte.length - 3);
      for (int i = 2; (i < paramArrayOfByte.length) && ((paramArrayOfByte[i] & 0x80) != 0); i++) {
        dataLength -= 1;
      }
    }
  }
  
  public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws InvalidMidiDataException
  {
    if ((paramInt1 >= 128) || (paramInt1 < 0)) {
      throw new InvalidMidiDataException("Invalid meta event with type " + paramInt1);
    }
    if (((paramInt2 > 0) && (paramInt2 > paramArrayOfByte.length)) || (paramInt2 < 0)) {
      throw new InvalidMidiDataException("length out of bounds: " + paramInt2);
    }
    length = (2 + getVarIntLength(paramInt2) + paramInt2);
    dataLength = paramInt2;
    data = new byte[length];
    data[0] = -1;
    data[1] = ((byte)paramInt1);
    writeVarInt(data, 2, paramInt2);
    if (paramInt2 > 0) {
      System.arraycopy(paramArrayOfByte, 0, data, length - dataLength, dataLength);
    }
  }
  
  public int getType()
  {
    if (length >= 2) {
      return data[1] & 0xFF;
    }
    return 0;
  }
  
  public byte[] getData()
  {
    byte[] arrayOfByte = new byte[dataLength];
    System.arraycopy(data, length - dataLength, arrayOfByte, 0, dataLength);
    return arrayOfByte;
  }
  
  public Object clone()
  {
    byte[] arrayOfByte = new byte[length];
    System.arraycopy(data, 0, arrayOfByte, 0, arrayOfByte.length);
    MetaMessage localMetaMessage = new MetaMessage(arrayOfByte);
    return localMetaMessage;
  }
  
  private int getVarIntLength(long paramLong)
  {
    int i = 0;
    do
    {
      paramLong >>= 7;
      i++;
    } while (paramLong > 0L);
    return i;
  }
  
  private void writeVarInt(byte[] paramArrayOfByte, int paramInt, long paramLong)
  {
    for (int i = 63; (i > 0) && ((paramLong & 127L << i) == 0L); i -= 7) {}
    while (i > 0)
    {
      paramArrayOfByte[(paramInt++)] = ((byte)(int)((paramLong & 127L << i) >> i | 0x80));
      i -= 7;
    }
    paramArrayOfByte[paramInt] = ((byte)(int)(paramLong & 0x7F));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\MetaMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */