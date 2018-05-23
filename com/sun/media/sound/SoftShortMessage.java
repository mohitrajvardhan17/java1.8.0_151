package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public final class SoftShortMessage
  extends ShortMessage
{
  int channel = 0;
  
  public SoftShortMessage() {}
  
  public int getChannel()
  {
    return channel;
  }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws InvalidMidiDataException
  {
    channel = paramInt2;
    super.setMessage(paramInt1, paramInt2 & 0xF, paramInt3, paramInt4);
  }
  
  public Object clone()
  {
    SoftShortMessage localSoftShortMessage = new SoftShortMessage();
    try
    {
      localSoftShortMessage.setMessage(getCommand(), getChannel(), getData1(), getData2());
    }
    catch (InvalidMidiDataException localInvalidMidiDataException)
    {
      throw new IllegalArgumentException(localInvalidMidiDataException);
    }
    return localSoftShortMessage;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftShortMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */