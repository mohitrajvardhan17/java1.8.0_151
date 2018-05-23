package java.awt.event;

import java.awt.Component;

public class MouseWheelEvent
  extends MouseEvent
{
  public static final int WHEEL_UNIT_SCROLL = 0;
  public static final int WHEEL_BLOCK_SCROLL = 1;
  int scrollType;
  int scrollAmount;
  int wheelRotation;
  double preciseWheelRotation;
  private static final long serialVersionUID = 6459879390515399677L;
  
  public MouseWheelEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6, int paramInt7, int paramInt8)
  {
    this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, 0, 0, paramInt5, paramBoolean, paramInt6, paramInt7, paramInt8);
  }
  
  public MouseWheelEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8, int paramInt9, int paramInt10)
  {
    this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean, paramInt8, paramInt9, paramInt10, paramInt10);
  }
  
  public MouseWheelEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, int paramInt8, int paramInt9, int paramInt10, double paramDouble)
  {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean, 0);
    scrollType = paramInt8;
    scrollAmount = paramInt9;
    wheelRotation = paramInt10;
    preciseWheelRotation = paramDouble;
  }
  
  public int getScrollType()
  {
    return scrollType;
  }
  
  public int getScrollAmount()
  {
    return scrollAmount;
  }
  
  public int getWheelRotation()
  {
    return wheelRotation;
  }
  
  public double getPreciseWheelRotation()
  {
    return preciseWheelRotation;
  }
  
  public int getUnitsToScroll()
  {
    return scrollAmount * wheelRotation;
  }
  
  public String paramString()
  {
    String str = null;
    if (getScrollType() == 0) {
      str = "WHEEL_UNIT_SCROLL";
    } else if (getScrollType() == 1) {
      str = "WHEEL_BLOCK_SCROLL";
    } else {
      str = "unknown scroll type";
    }
    return super.paramString() + ",scrollType=" + str + ",scrollAmount=" + getScrollAmount() + ",wheelRotation=" + getWheelRotation() + ",preciseWheelRotation=" + getPreciseWheelRotation();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\MouseWheelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */