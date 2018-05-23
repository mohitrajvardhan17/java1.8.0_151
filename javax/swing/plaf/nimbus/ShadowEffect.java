package javax.swing.plaf.nimbus;

import java.awt.Color;

abstract class ShadowEffect
  extends Effect
{
  protected Color color = Color.BLACK;
  protected float opacity = 0.75F;
  protected int angle = 135;
  protected int distance = 5;
  protected int spread = 0;
  protected int size = 5;
  
  ShadowEffect() {}
  
  Color getColor()
  {
    return color;
  }
  
  void setColor(Color paramColor)
  {
    Color localColor = getColor();
    color = paramColor;
  }
  
  float getOpacity()
  {
    return opacity;
  }
  
  void setOpacity(float paramFloat)
  {
    float f = getOpacity();
    opacity = paramFloat;
  }
  
  int getAngle()
  {
    return angle;
  }
  
  void setAngle(int paramInt)
  {
    int i = getAngle();
    angle = paramInt;
  }
  
  int getDistance()
  {
    return distance;
  }
  
  void setDistance(int paramInt)
  {
    int i = getDistance();
    distance = paramInt;
  }
  
  int getSpread()
  {
    return spread;
  }
  
  void setSpread(int paramInt)
  {
    int i = getSpread();
    spread = paramInt;
  }
  
  int getSize()
  {
    return size;
  }
  
  void setSize(int paramInt)
  {
    int i = getSize();
    size = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ShadowEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */