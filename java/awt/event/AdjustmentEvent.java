package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Adjustable;

public class AdjustmentEvent
  extends AWTEvent
{
  public static final int ADJUSTMENT_FIRST = 601;
  public static final int ADJUSTMENT_LAST = 601;
  public static final int ADJUSTMENT_VALUE_CHANGED = 601;
  public static final int UNIT_INCREMENT = 1;
  public static final int UNIT_DECREMENT = 2;
  public static final int BLOCK_DECREMENT = 3;
  public static final int BLOCK_INCREMENT = 4;
  public static final int TRACK = 5;
  Adjustable adjustable;
  int value;
  int adjustmentType;
  boolean isAdjusting;
  private static final long serialVersionUID = 5700290645205279921L;
  
  public AdjustmentEvent(Adjustable paramAdjustable, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramAdjustable, paramInt1, paramInt2, paramInt3, false);
  }
  
  public AdjustmentEvent(Adjustable paramAdjustable, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    super(paramAdjustable, paramInt1);
    adjustable = paramAdjustable;
    adjustmentType = paramInt2;
    value = paramInt3;
    isAdjusting = paramBoolean;
  }
  
  public Adjustable getAdjustable()
  {
    return adjustable;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public int getAdjustmentType()
  {
    return adjustmentType;
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public String paramString()
  {
    String str1;
    switch (id)
    {
    case 601: 
      str1 = "ADJUSTMENT_VALUE_CHANGED";
      break;
    default: 
      str1 = "unknown type";
    }
    String str2;
    switch (adjustmentType)
    {
    case 1: 
      str2 = "UNIT_INCREMENT";
      break;
    case 2: 
      str2 = "UNIT_DECREMENT";
      break;
    case 4: 
      str2 = "BLOCK_INCREMENT";
      break;
    case 3: 
      str2 = "BLOCK_DECREMENT";
      break;
    case 5: 
      str2 = "TRACK";
      break;
    default: 
      str2 = "unknown type";
    }
    return str1 + ",adjType=" + str2 + ",value=" + value + ",isAdjusting=" + isAdjusting;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\AdjustmentEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */