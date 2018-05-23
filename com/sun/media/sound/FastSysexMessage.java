package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

final class FastSysexMessage
  extends SysexMessage
{
  FastSysexMessage(byte[] paramArrayOfByte)
    throws InvalidMidiDataException
  {
    super(paramArrayOfByte);
    if ((paramArrayOfByte.length == 0) || (((paramArrayOfByte[0] & 0xFF) != 240) && ((paramArrayOfByte[0] & 0xFF) != 247))) {
      super.setMessage(paramArrayOfByte, paramArrayOfByte.length);
    }
  }
  
  byte[] getReadOnlyMessage()
  {
    return data;
  }
  
  public void setMessage(byte[] paramArrayOfByte, int paramInt)
    throws InvalidMidiDataException
  {
    if ((paramArrayOfByte.length == 0) || (((paramArrayOfByte[0] & 0xFF) != 240) && ((paramArrayOfByte[0] & 0xFF) != 247))) {
      super.setMessage(paramArrayOfByte, paramArrayOfByte.length);
    }
    length = paramInt;
    data = new byte[length];
    System.arraycopy(paramArrayOfByte, 0, data, 0, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\FastSysexMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */