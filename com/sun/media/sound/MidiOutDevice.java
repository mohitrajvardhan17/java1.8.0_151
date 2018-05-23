package com.sun.media.sound;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

final class MidiOutDevice
  extends AbstractMidiDevice
{
  MidiOutDevice(AbstractMidiDeviceProvider.Info paramInfo)
  {
    super(paramInfo);
  }
  
  protected synchronized void implOpen()
    throws MidiUnavailableException
  {
    int i = ((AbstractMidiDeviceProvider.Info)getDeviceInfo()).getIndex();
    id = nOpen(i);
    if (id == 0L) {
      throw new MidiUnavailableException("Unable to open native device");
    }
  }
  
  protected synchronized void implClose()
  {
    long l = id;
    id = 0L;
    super.implClose();
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
  
  protected boolean hasReceivers()
  {
    return true;
  }
  
  protected Receiver createReceiver()
  {
    return new MidiOutReceiver();
  }
  
  private native long nOpen(int paramInt)
    throws MidiUnavailableException;
  
  private native void nClose(long paramLong);
  
  private native void nSendShortMessage(long paramLong1, int paramInt, long paramLong2);
  
  private native void nSendLongMessage(long paramLong1, byte[] paramArrayOfByte, int paramInt, long paramLong2);
  
  private native long nGetTimeStamp(long paramLong);
  
  final class MidiOutReceiver
    extends AbstractMidiDevice.AbstractReceiver
  {
    MidiOutReceiver()
    {
      super();
    }
    
    void implSend(MidiMessage paramMidiMessage, long paramLong)
    {
      int i = paramMidiMessage.getLength();
      int j = paramMidiMessage.getStatus();
      if ((i <= 3) && (j != 240) && (j != 247))
      {
        int k;
        Object localObject;
        if ((paramMidiMessage instanceof ShortMessage))
        {
          if ((paramMidiMessage instanceof FastShortMessage))
          {
            k = ((FastShortMessage)paramMidiMessage).getPackedMsg();
          }
          else
          {
            localObject = (ShortMessage)paramMidiMessage;
            k = j & 0xFF | (((ShortMessage)localObject).getData1() & 0xFF) << 8 | (((ShortMessage)localObject).getData2() & 0xFF) << 16;
          }
        }
        else
        {
          k = 0;
          localObject = paramMidiMessage.getMessage();
          if (i > 0)
          {
            k = localObject[0] & 0xFF;
            if (i > 1)
            {
              if (j == 255) {
                return;
              }
              k |= (localObject[1] & 0xFF) << 8;
              if (i > 2) {
                k |= (localObject[2] & 0xFF) << 16;
              }
            }
          }
        }
        MidiOutDevice.this.nSendShortMessage(id, k, paramLong);
      }
      else
      {
        byte[] arrayOfByte;
        if ((paramMidiMessage instanceof FastSysexMessage)) {
          arrayOfByte = ((FastSysexMessage)paramMidiMessage).getReadOnlyMessage();
        } else {
          arrayOfByte = paramMidiMessage.getMessage();
        }
        int m = Math.min(i, arrayOfByte.length);
        if (m > 0) {
          MidiOutDevice.this.nSendLongMessage(id, arrayOfByte, m, paramLong);
        }
      }
    }
    
    synchronized void sendPackedMidiMessage(int paramInt, long paramLong)
    {
      if ((isOpen()) && (id != 0L)) {
        MidiOutDevice.this.nSendShortMessage(id, paramInt, paramLong);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\MidiOutDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */