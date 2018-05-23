package javax.sound.midi;

public abstract class MidiMessage
  implements Cloneable
{
  protected byte[] data;
  protected int length = 0;
  
  protected MidiMessage(byte[] paramArrayOfByte)
  {
    data = paramArrayOfByte;
    if (paramArrayOfByte != null) {
      length = paramArrayOfByte.length;
    }
  }
  
  protected void setMessage(byte[] paramArrayOfByte, int paramInt)
    throws InvalidMidiDataException
  {
    if ((paramInt < 0) || ((paramInt > 0) && (paramInt > paramArrayOfByte.length))) {
      throw new IndexOutOfBoundsException("length out of bounds: " + paramInt);
    }
    length = paramInt;
    if ((data == null) || (data.length < length)) {
      data = new byte[length];
    }
    System.arraycopy(paramArrayOfByte, 0, data, 0, paramInt);
  }
  
  public byte[] getMessage()
  {
    byte[] arrayOfByte = new byte[length];
    System.arraycopy(data, 0, arrayOfByte, 0, length);
    return arrayOfByte;
  }
  
  public int getStatus()
  {
    if (length > 0) {
      return data[0] & 0xFF;
    }
    return 0;
  }
  
  public int getLength()
  {
    return length;
  }
  
  public abstract Object clone();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\MidiMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */