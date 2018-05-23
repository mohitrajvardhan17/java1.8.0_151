package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSRegion
{
  public static final int OPTION_SELFNONEXCLUSIVE = 1;
  List<DLSModulator> modulators = new ArrayList();
  int keyfrom;
  int keyto;
  int velfrom;
  int velto;
  int options;
  int exclusiveClass;
  int fusoptions;
  int phasegroup;
  long channel;
  DLSSample sample = null;
  DLSSampleOptions sampleoptions;
  
  public DLSRegion() {}
  
  public List<DLSModulator> getModulators()
  {
    return modulators;
  }
  
  public long getChannel()
  {
    return channel;
  }
  
  public void setChannel(long paramLong)
  {
    channel = paramLong;
  }
  
  public int getExclusiveClass()
  {
    return exclusiveClass;
  }
  
  public void setExclusiveClass(int paramInt)
  {
    exclusiveClass = paramInt;
  }
  
  public int getFusoptions()
  {
    return fusoptions;
  }
  
  public void setFusoptions(int paramInt)
  {
    fusoptions = paramInt;
  }
  
  public int getKeyfrom()
  {
    return keyfrom;
  }
  
  public void setKeyfrom(int paramInt)
  {
    keyfrom = paramInt;
  }
  
  public int getKeyto()
  {
    return keyto;
  }
  
  public void setKeyto(int paramInt)
  {
    keyto = paramInt;
  }
  
  public int getOptions()
  {
    return options;
  }
  
  public void setOptions(int paramInt)
  {
    options = paramInt;
  }
  
  public int getPhasegroup()
  {
    return phasegroup;
  }
  
  public void setPhasegroup(int paramInt)
  {
    phasegroup = paramInt;
  }
  
  public DLSSample getSample()
  {
    return sample;
  }
  
  public void setSample(DLSSample paramDLSSample)
  {
    sample = paramDLSSample;
  }
  
  public int getVelfrom()
  {
    return velfrom;
  }
  
  public void setVelfrom(int paramInt)
  {
    velfrom = paramInt;
  }
  
  public int getVelto()
  {
    return velto;
  }
  
  public void setVelto(int paramInt)
  {
    velto = paramInt;
  }
  
  public void setModulators(List<DLSModulator> paramList)
  {
    modulators = paramList;
  }
  
  public DLSSampleOptions getSampleoptions()
  {
    return sampleoptions;
  }
  
  public void setSampleoptions(DLSSampleOptions paramDLSSampleOptions)
  {
    sampleoptions = paramDLSSampleOptions;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */