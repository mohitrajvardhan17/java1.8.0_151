package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public final class SoftChannelProxy
  implements MidiChannel
{
  private MidiChannel channel = null;
  
  public SoftChannelProxy() {}
  
  public MidiChannel getChannel()
  {
    return channel;
  }
  
  public void setChannel(MidiChannel paramMidiChannel)
  {
    channel = paramMidiChannel;
  }
  
  public void allNotesOff()
  {
    if (channel == null) {
      return;
    }
    channel.allNotesOff();
  }
  
  public void allSoundOff()
  {
    if (channel == null) {
      return;
    }
    channel.allSoundOff();
  }
  
  public void controlChange(int paramInt1, int paramInt2)
  {
    if (channel == null) {
      return;
    }
    channel.controlChange(paramInt1, paramInt2);
  }
  
  public int getChannelPressure()
  {
    if (channel == null) {
      return 0;
    }
    return channel.getChannelPressure();
  }
  
  public int getController(int paramInt)
  {
    if (channel == null) {
      return 0;
    }
    return channel.getController(paramInt);
  }
  
  public boolean getMono()
  {
    if (channel == null) {
      return false;
    }
    return channel.getMono();
  }
  
  public boolean getMute()
  {
    if (channel == null) {
      return false;
    }
    return channel.getMute();
  }
  
  public boolean getOmni()
  {
    if (channel == null) {
      return false;
    }
    return channel.getOmni();
  }
  
  public int getPitchBend()
  {
    if (channel == null) {
      return 8192;
    }
    return channel.getPitchBend();
  }
  
  public int getPolyPressure(int paramInt)
  {
    if (channel == null) {
      return 0;
    }
    return channel.getPolyPressure(paramInt);
  }
  
  public int getProgram()
  {
    if (channel == null) {
      return 0;
    }
    return channel.getProgram();
  }
  
  public boolean getSolo()
  {
    if (channel == null) {
      return false;
    }
    return channel.getSolo();
  }
  
  public boolean localControl(boolean paramBoolean)
  {
    if (channel == null) {
      return false;
    }
    return channel.localControl(paramBoolean);
  }
  
  public void noteOff(int paramInt)
  {
    if (channel == null) {
      return;
    }
    channel.noteOff(paramInt);
  }
  
  public void noteOff(int paramInt1, int paramInt2)
  {
    if (channel == null) {
      return;
    }
    channel.noteOff(paramInt1, paramInt2);
  }
  
  public void noteOn(int paramInt1, int paramInt2)
  {
    if (channel == null) {
      return;
    }
    channel.noteOn(paramInt1, paramInt2);
  }
  
  public void programChange(int paramInt)
  {
    if (channel == null) {
      return;
    }
    channel.programChange(paramInt);
  }
  
  public void programChange(int paramInt1, int paramInt2)
  {
    if (channel == null) {
      return;
    }
    channel.programChange(paramInt1, paramInt2);
  }
  
  public void resetAllControllers()
  {
    if (channel == null) {
      return;
    }
    channel.resetAllControllers();
  }
  
  public void setChannelPressure(int paramInt)
  {
    if (channel == null) {
      return;
    }
    channel.setChannelPressure(paramInt);
  }
  
  public void setMono(boolean paramBoolean)
  {
    if (channel == null) {
      return;
    }
    channel.setMono(paramBoolean);
  }
  
  public void setMute(boolean paramBoolean)
  {
    if (channel == null) {
      return;
    }
    channel.setMute(paramBoolean);
  }
  
  public void setOmni(boolean paramBoolean)
  {
    if (channel == null) {
      return;
    }
    channel.setOmni(paramBoolean);
  }
  
  public void setPitchBend(int paramInt)
  {
    if (channel == null) {
      return;
    }
    channel.setPitchBend(paramInt);
  }
  
  public void setPolyPressure(int paramInt1, int paramInt2)
  {
    if (channel == null) {
      return;
    }
    channel.setPolyPressure(paramInt1, paramInt2);
  }
  
  public void setSolo(boolean paramBoolean)
  {
    if (channel == null) {
      return;
    }
    channel.setSolo(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftChannelProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */