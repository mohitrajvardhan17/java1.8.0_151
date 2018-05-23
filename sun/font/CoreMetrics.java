package sun.font;

import java.awt.font.LineMetrics;

public final class CoreMetrics
{
  public final float ascent;
  public final float descent;
  public final float leading;
  public final float height;
  public final int baselineIndex;
  public final float[] baselineOffsets;
  public final float strikethroughOffset;
  public final float strikethroughThickness;
  public final float underlineOffset;
  public final float underlineThickness;
  public final float ssOffset;
  public final float italicAngle;
  
  public CoreMetrics(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt, float[] paramArrayOfFloat, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10)
  {
    ascent = paramFloat1;
    descent = paramFloat2;
    leading = paramFloat3;
    height = paramFloat4;
    baselineIndex = paramInt;
    baselineOffsets = paramArrayOfFloat;
    strikethroughOffset = paramFloat5;
    strikethroughThickness = paramFloat6;
    underlineOffset = paramFloat7;
    underlineThickness = paramFloat8;
    ssOffset = paramFloat9;
    italicAngle = paramFloat10;
  }
  
  public static CoreMetrics get(LineMetrics paramLineMetrics)
  {
    return cm;
  }
  
  public final int hashCode()
  {
    return Float.floatToIntBits(ascent + ssOffset);
  }
  
  public final boolean equals(Object paramObject)
  {
    try
    {
      return equals((CoreMetrics)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public final boolean equals(CoreMetrics paramCoreMetrics)
  {
    if (paramCoreMetrics != null)
    {
      if (this == paramCoreMetrics) {
        return true;
      }
      return (ascent == ascent) && (descent == descent) && (leading == leading) && (baselineIndex == baselineIndex) && (baselineOffsets[0] == baselineOffsets[0]) && (baselineOffsets[1] == baselineOffsets[1]) && (baselineOffsets[2] == baselineOffsets[2]) && (strikethroughOffset == strikethroughOffset) && (strikethroughThickness == strikethroughThickness) && (underlineOffset == underlineOffset) && (underlineThickness == underlineThickness) && (ssOffset == ssOffset) && (italicAngle == italicAngle);
    }
    return false;
  }
  
  public final float effectiveBaselineOffset(float[] paramArrayOfFloat)
  {
    switch (baselineIndex)
    {
    case -1: 
      return paramArrayOfFloat[4] + ascent;
    case -2: 
      return paramArrayOfFloat[3] - descent;
    }
    return paramArrayOfFloat[baselineIndex];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CoreMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */