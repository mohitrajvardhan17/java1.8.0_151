package javax.swing.text;

import java.io.Serializable;

public class TabStop
  implements Serializable
{
  public static final int ALIGN_LEFT = 0;
  public static final int ALIGN_RIGHT = 1;
  public static final int ALIGN_CENTER = 2;
  public static final int ALIGN_DECIMAL = 4;
  public static final int ALIGN_BAR = 5;
  public static final int LEAD_NONE = 0;
  public static final int LEAD_DOTS = 1;
  public static final int LEAD_HYPHENS = 2;
  public static final int LEAD_UNDERLINE = 3;
  public static final int LEAD_THICKLINE = 4;
  public static final int LEAD_EQUALS = 5;
  private int alignment;
  private float position;
  private int leader;
  
  public TabStop(float paramFloat)
  {
    this(paramFloat, 0, 0);
  }
  
  public TabStop(float paramFloat, int paramInt1, int paramInt2)
  {
    alignment = paramInt1;
    leader = paramInt2;
    position = paramFloat;
  }
  
  public float getPosition()
  {
    return position;
  }
  
  public int getAlignment()
  {
    return alignment;
  }
  
  public int getLeader()
  {
    return leader;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof TabStop))
    {
      TabStop localTabStop = (TabStop)paramObject;
      return (alignment == alignment) && (leader == leader) && (position == position);
    }
    return false;
  }
  
  public int hashCode()
  {
    return alignment ^ leader ^ Math.round(position);
  }
  
  public String toString()
  {
    switch (alignment)
    {
    case 0: 
    case 3: 
    default: 
      str = "";
      break;
    case 1: 
      str = "right ";
      break;
    case 2: 
      str = "center ";
      break;
    case 4: 
      str = "decimal ";
      break;
    case 5: 
      str = "bar ";
    }
    String str = str + "tab @" + String.valueOf(position);
    if (leader != 0) {
      str = str + " (w/leaders)";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\TabStop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */