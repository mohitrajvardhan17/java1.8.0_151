package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

public final class FontLineMetrics
  extends LineMetrics
  implements Cloneable
{
  public int numchars;
  public final CoreMetrics cm;
  public final FontRenderContext frc;
  
  public FontLineMetrics(int paramInt, CoreMetrics paramCoreMetrics, FontRenderContext paramFontRenderContext)
  {
    numchars = paramInt;
    cm = paramCoreMetrics;
    frc = paramFontRenderContext;
  }
  
  public final int getNumChars()
  {
    return numchars;
  }
  
  public final float getAscent()
  {
    return cm.ascent;
  }
  
  public final float getDescent()
  {
    return cm.descent;
  }
  
  public final float getLeading()
  {
    return cm.leading;
  }
  
  public final float getHeight()
  {
    return cm.height;
  }
  
  public final int getBaselineIndex()
  {
    return cm.baselineIndex;
  }
  
  public final float[] getBaselineOffsets()
  {
    return (float[])cm.baselineOffsets.clone();
  }
  
  public final float getStrikethroughOffset()
  {
    return cm.strikethroughOffset;
  }
  
  public final float getStrikethroughThickness()
  {
    return cm.strikethroughThickness;
  }
  
  public final float getUnderlineOffset()
  {
    return cm.underlineOffset;
  }
  
  public final float getUnderlineThickness()
  {
    return cm.underlineThickness;
  }
  
  public final int hashCode()
  {
    return cm.hashCode();
  }
  
  public final boolean equals(Object paramObject)
  {
    try
    {
      return cm.equals(cm);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public final Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontLineMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */