package java.awt.font;

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;

public class FontRenderContext
{
  private transient AffineTransform tx;
  private transient Object aaHintValue;
  private transient Object fmHintValue;
  private transient boolean defaulting;
  
  protected FontRenderContext()
  {
    aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
    defaulting = true;
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramAffineTransform != null) && (!paramAffineTransform.isIdentity())) {
      tx = new AffineTransform(paramAffineTransform);
    }
    if (paramBoolean1) {
      aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    } else {
      aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    }
    if (paramBoolean2) {
      fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
    } else {
      fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2)
  {
    if ((paramAffineTransform != null) && (!paramAffineTransform.isIdentity())) {
      tx = new AffineTransform(paramAffineTransform);
    }
    try
    {
      if (RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(paramObject1)) {
        aaHintValue = paramObject1;
      } else {
        throw new IllegalArgumentException("AA hint:" + paramObject1);
      }
    }
    catch (Exception localException1)
    {
      throw new IllegalArgumentException("AA hint:" + paramObject1);
    }
    try
    {
      if (RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(paramObject2)) {
        fmHintValue = paramObject2;
      } else {
        throw new IllegalArgumentException("FM hint:" + paramObject2);
      }
    }
    catch (Exception localException2)
    {
      throw new IllegalArgumentException("FM hint:" + paramObject2);
    }
  }
  
  public boolean isTransformed()
  {
    if (!defaulting) {
      return tx != null;
    }
    return !getTransform().isIdentity();
  }
  
  public int getTransformType()
  {
    if (!defaulting)
    {
      if (tx == null) {
        return 0;
      }
      return tx.getType();
    }
    return getTransform().getType();
  }
  
  public AffineTransform getTransform()
  {
    return tx == null ? new AffineTransform() : new AffineTransform(tx);
  }
  
  public boolean isAntiAliased()
  {
    return (aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) && (aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
  }
  
  public boolean usesFractionalMetrics()
  {
    return (fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_OFF) && (fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
  }
  
  public Object getAntiAliasingHint()
  {
    if (defaulting)
    {
      if (isAntiAliased()) {
        return RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
      }
      return RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    }
    return aaHintValue;
  }
  
  public Object getFractionalMetricsHint()
  {
    if (defaulting)
    {
      if (usesFractionalMetrics()) {
        return RenderingHints.VALUE_FRACTIONALMETRICS_ON;
      }
      return RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }
    return fmHintValue;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      return equals((FontRenderContext)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public boolean equals(FontRenderContext paramFontRenderContext)
  {
    if (this == paramFontRenderContext) {
      return true;
    }
    if (paramFontRenderContext == null) {
      return false;
    }
    if ((!defaulting) && (!defaulting))
    {
      if ((aaHintValue == aaHintValue) && (fmHintValue == fmHintValue)) {
        return tx == null ? false : tx == null ? true : tx.equals(tx);
      }
      return false;
    }
    return (paramFontRenderContext.getAntiAliasingHint() == getAntiAliasingHint()) && (paramFontRenderContext.getFractionalMetricsHint() == getFractionalMetricsHint()) && (paramFontRenderContext.getTransform().equals(getTransform()));
  }
  
  public int hashCode()
  {
    int i = tx == null ? 0 : tx.hashCode();
    if (defaulting)
    {
      i += getAntiAliasingHint().hashCode();
      i += getFractionalMetricsHint().hashCode();
    }
    else
    {
      i += aaHintValue.hashCode();
      i += fmHintValue.hashCode();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\FontRenderContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */