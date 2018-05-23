package javax.swing.plaf.nimbus;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class DerivedColor
  extends Color
{
  private final String uiDefaultParentName;
  private final float hOffset;
  private final float sOffset;
  private final float bOffset;
  private final int aOffset;
  private int argbValue;
  
  DerivedColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
  {
    super(0);
    uiDefaultParentName = paramString;
    hOffset = paramFloat1;
    sOffset = paramFloat2;
    bOffset = paramFloat3;
    aOffset = paramInt;
  }
  
  public String getUiDefaultParentName()
  {
    return uiDefaultParentName;
  }
  
  public float getHueOffset()
  {
    return hOffset;
  }
  
  public float getSaturationOffset()
  {
    return sOffset;
  }
  
  public float getBrightnessOffset()
  {
    return bOffset;
  }
  
  public int getAlphaOffset()
  {
    return aOffset;
  }
  
  public void rederiveColor()
  {
    Color localColor = UIManager.getColor(uiDefaultParentName);
    float[] arrayOfFloat;
    int i;
    if (localColor != null)
    {
      arrayOfFloat = Color.RGBtoHSB(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), null);
      arrayOfFloat[0] = clamp(arrayOfFloat[0] + hOffset);
      arrayOfFloat[1] = clamp(arrayOfFloat[1] + sOffset);
      arrayOfFloat[2] = clamp(arrayOfFloat[2] + bOffset);
      i = clamp(localColor.getAlpha() + aOffset);
      argbValue = (Color.HSBtoRGB(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]) & 0xFFFFFF | i << 24);
    }
    else
    {
      arrayOfFloat = new float[3];
      arrayOfFloat[0] = clamp(hOffset);
      arrayOfFloat[1] = clamp(sOffset);
      arrayOfFloat[2] = clamp(bOffset);
      i = clamp(aOffset);
      argbValue = (Color.HSBtoRGB(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]) & 0xFFFFFF | i << 24);
    }
  }
  
  public int getRGB()
  {
    return argbValue;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DerivedColor)) {
      return false;
    }
    DerivedColor localDerivedColor = (DerivedColor)paramObject;
    if (aOffset != aOffset) {
      return false;
    }
    if (Float.compare(bOffset, bOffset) != 0) {
      return false;
    }
    if (Float.compare(hOffset, hOffset) != 0) {
      return false;
    }
    if (Float.compare(sOffset, sOffset) != 0) {
      return false;
    }
    return uiDefaultParentName.equals(uiDefaultParentName);
  }
  
  public int hashCode()
  {
    int i = uiDefaultParentName.hashCode();
    i = 31 * i + hOffset != 0.0F ? Float.floatToIntBits(hOffset) : 0;
    i = 31 * i + sOffset != 0.0F ? Float.floatToIntBits(sOffset) : 0;
    i = 31 * i + bOffset != 0.0F ? Float.floatToIntBits(bOffset) : 0;
    i = 31 * i + aOffset;
    return i;
  }
  
  private float clamp(float paramFloat)
  {
    if (paramFloat < 0.0F) {
      paramFloat = 0.0F;
    } else if (paramFloat > 1.0F) {
      paramFloat = 1.0F;
    }
    return paramFloat;
  }
  
  private int clamp(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    } else if (paramInt > 255) {
      paramInt = 255;
    }
    return paramInt;
  }
  
  public String toString()
  {
    Color localColor = UIManager.getColor(uiDefaultParentName);
    String str = "DerivedColor(color=" + getRed() + "," + getGreen() + "," + getBlue() + " parent=" + uiDefaultParentName + " offsets=" + getHueOffset() + "," + getSaturationOffset() + "," + getBrightnessOffset() + "," + getAlphaOffset();
    return str + " pColor=" + localColor.getRed() + "," + localColor.getGreen() + "," + localColor.getBlue();
  }
  
  static class UIResource
    extends DerivedColor
    implements UIResource
  {
    UIResource(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt)
    {
      super(paramFloat1, paramFloat2, paramFloat3, paramInt);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof UIResource)) && (super.equals(paramObject));
    }
    
    public int hashCode()
    {
      return super.hashCode() + 7;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\DerivedColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */