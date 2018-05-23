package com.sun.media.sound;

public final class SF2InstrumentRegion
  extends SF2Region
{
  SF2Layer layer;
  
  public SF2InstrumentRegion() {}
  
  public SF2Layer getLayer()
  {
    return layer;
  }
  
  public void setLayer(SF2Layer paramSF2Layer)
  {
    layer = paramSF2Layer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2InstrumentRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */