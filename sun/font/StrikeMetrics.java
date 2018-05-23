package sun.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Float;

public final class StrikeMetrics
{
  public float ascentX;
  public float ascentY;
  public float descentX;
  public float descentY;
  public float baselineX;
  public float baselineY;
  public float leadingX;
  public float leadingY;
  public float maxAdvanceX;
  public float maxAdvanceY;
  
  StrikeMetrics()
  {
    ascentX = (ascentY = 2.14748365E9F);
    descentX = (descentY = leadingX = leadingY = -2.14748365E9F);
    baselineX = (baselineX = maxAdvanceX = maxAdvanceY = -2.14748365E9F);
  }
  
  StrikeMetrics(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10)
  {
    ascentX = paramFloat1;
    ascentY = paramFloat2;
    descentX = paramFloat3;
    descentY = paramFloat4;
    baselineX = paramFloat5;
    baselineY = paramFloat6;
    leadingX = paramFloat7;
    leadingY = paramFloat8;
    maxAdvanceX = paramFloat9;
    maxAdvanceY = paramFloat10;
  }
  
  public float getAscent()
  {
    return -ascentY;
  }
  
  public float getDescent()
  {
    return descentY;
  }
  
  public float getLeading()
  {
    return leadingY;
  }
  
  public float getMaxAdvance()
  {
    return maxAdvanceX;
  }
  
  void merge(StrikeMetrics paramStrikeMetrics)
  {
    if (paramStrikeMetrics == null) {
      return;
    }
    if (ascentX < ascentX) {
      ascentX = ascentX;
    }
    if (ascentY < ascentY) {
      ascentY = ascentY;
    }
    if (descentX > descentX) {
      descentX = descentX;
    }
    if (descentY > descentY) {
      descentY = descentY;
    }
    if (baselineX > baselineX) {
      baselineX = baselineX;
    }
    if (baselineY > baselineY) {
      baselineY = baselineY;
    }
    if (leadingX > leadingX) {
      leadingX = leadingX;
    }
    if (leadingY > leadingY) {
      leadingY = leadingY;
    }
    if (maxAdvanceX > maxAdvanceX) {
      maxAdvanceX = maxAdvanceX;
    }
    if (maxAdvanceY > maxAdvanceY) {
      maxAdvanceY = maxAdvanceY;
    }
  }
  
  void convertToUserSpace(AffineTransform paramAffineTransform)
  {
    Point2D.Float localFloat = new Point2D.Float();
    x = ascentX;
    y = ascentY;
    paramAffineTransform.deltaTransform(localFloat, localFloat);
    ascentX = x;
    ascentY = y;
    x = descentX;
    y = descentY;
    paramAffineTransform.deltaTransform(localFloat, localFloat);
    descentX = x;
    descentY = y;
    x = baselineX;
    y = baselineY;
    paramAffineTransform.deltaTransform(localFloat, localFloat);
    baselineX = x;
    baselineY = y;
    x = leadingX;
    y = leadingY;
    paramAffineTransform.deltaTransform(localFloat, localFloat);
    leadingX = x;
    leadingY = y;
    x = maxAdvanceX;
    y = maxAdvanceY;
    paramAffineTransform.deltaTransform(localFloat, localFloat);
    maxAdvanceX = x;
    maxAdvanceY = y;
  }
  
  public String toString()
  {
    return "ascent:x=" + ascentX + " y=" + ascentY + " descent:x=" + descentX + " y=" + descentY + " baseline:x=" + baselineX + " y=" + baselineY + " leading:x=" + leadingX + " y=" + leadingY + " maxAdvance:x=" + maxAdvanceX + " y=" + maxAdvanceY;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\StrikeMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */