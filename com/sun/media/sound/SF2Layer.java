package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.SoundbankResource;

public final class SF2Layer
  extends SoundbankResource
{
  String name = "";
  SF2GlobalRegion globalregion = null;
  List<SF2LayerRegion> regions = new ArrayList();
  
  public SF2Layer(SF2Soundbank paramSF2Soundbank)
  {
    super(paramSF2Soundbank, null, null);
  }
  
  public SF2Layer()
  {
    super(null, null, null);
  }
  
  public Object getData()
  {
    return null;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public List<SF2LayerRegion> getRegions()
  {
    return regions;
  }
  
  public SF2GlobalRegion getGlobalRegion()
  {
    return globalregion;
  }
  
  public void setGlobalZone(SF2GlobalRegion paramSF2GlobalRegion)
  {
    globalregion = paramSF2GlobalRegion;
  }
  
  public String toString()
  {
    return "Layer: " + name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2Layer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */