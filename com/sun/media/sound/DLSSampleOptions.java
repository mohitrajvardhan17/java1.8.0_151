package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSSampleOptions
{
  int unitynote;
  short finetune;
  int attenuation;
  long options;
  List<DLSSampleLoop> loops = new ArrayList();
  
  public DLSSampleOptions() {}
  
  public int getAttenuation()
  {
    return attenuation;
  }
  
  public void setAttenuation(int paramInt)
  {
    attenuation = paramInt;
  }
  
  public short getFinetune()
  {
    return finetune;
  }
  
  public void setFinetune(short paramShort)
  {
    finetune = paramShort;
  }
  
  public List<DLSSampleLoop> getLoops()
  {
    return loops;
  }
  
  public long getOptions()
  {
    return options;
  }
  
  public void setOptions(long paramLong)
  {
    options = paramLong;
  }
  
  public int getUnitynote()
  {
    return unitynote;
  }
  
  public void setUnitynote(int paramInt)
  {
    unitynote = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSSampleOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */