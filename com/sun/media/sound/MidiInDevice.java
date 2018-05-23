package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

final class MidiInDevice
  extends AbstractMidiDevice
  implements Runnable
{
  private Thread midiInThread = null;
  
  MidiInDevice(AbstractMidiDeviceProvider.Info paramInfo)
  {
    super(paramInfo);
  }
  
  protected synchronized void implOpen()
    throws MidiUnavailableException
  {
    int i = ((MidiInDeviceProvider.MidiInDeviceInfo)getDeviceInfo()).getIndex();
    id = nOpen(i);
    if (id == 0L) {
      throw new MidiUnavailableException("Unable to open native device");
    }
    if (midiInThread == null) {
      midiInThread = JSSecurityManager.createThread(this, "Java Sound MidiInDevice Thread", false, -1, true);
    }
    nStart(id);
  }
  
  protected synchronized void implClose()
  {
    long l = id;
    id = 0L;
    super.implClose();
    nStop(l);
    if (midiInThread != null) {
      try
      {
        midiInThread.join(1000L);
      }
      catch (InterruptedException localInterruptedException) {}
    }
    nClose(l);
  }
  
  public long getMicrosecondPosition()
  {
    long l = -1L;
    if (isOpen()) {
      l = nGetTimeStamp(id);
    }
    return l;
  }
  
  protected boolean hasTransmitters()
  {
    return true;
  }
  
  protected Transmitter createTransmitter()
  {
    return new MidiInTransmitter(null);
  }
  
  public void run()
  {
    while (id != 0L)
    {
      nGetMessages(id);
      if (id != 0L) {
        try
        {
          Thread.sleep(1L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    midiInThread = null;
  }
  
  void callbackShortMessage(int paramInt, long paramLong)
  {
    if ((paramInt == 0) || (id == 0L)) {
      return;
    }
    getTransmitterList().sendMessage(paramInt, paramLong);
  }
  
  void callbackLongMessage(byte[] paramArrayOfByte, long paramLong)
  {
    if ((id == 0L) || (paramArrayOfByte == null)) {
      return;
    }
    getTransmitterList().sendMessage(paramArrayOfByte, paramLong);
  }
  
  private native long nOpen(int paramInt)
    throws MidiUnavailableException;
  
  private native void nClose(long paramLong);
  
  private native void nStart(long paramLong)
    throws MidiUnavailableException;
  
  private native void nStop(long paramLong);
  
  private native long nGetTimeStamp(long paramLong);
  
  private native void nGetMessages(long paramLong);
  
  private final class MidiInTransmitter
    extends AbstractMidiDevice.BasicTransmitter
  {
    private MidiInTransmitter()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\MidiInDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */