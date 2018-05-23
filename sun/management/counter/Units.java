package sun.management.counter;

import java.io.Serializable;

public class Units
  implements Serializable
{
  private static final int NUNITS = 8;
  private static Units[] map = new Units[8];
  private final String name;
  private final int value;
  public static final Units INVALID = new Units("Invalid", 0);
  public static final Units NONE = new Units("None", 1);
  public static final Units BYTES = new Units("Bytes", 2);
  public static final Units TICKS = new Units("Ticks", 3);
  public static final Units EVENTS = new Units("Events", 4);
  public static final Units STRING = new Units("String", 5);
  public static final Units HERTZ = new Units("Hertz", 6);
  private static final long serialVersionUID = 6992337162326171013L;
  
  public String toString()
  {
    return name;
  }
  
  public int intValue()
  {
    return value;
  }
  
  public static Units toUnits(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= map.length) || (map[paramInt] == null)) {
      return INVALID;
    }
    return map[paramInt];
  }
  
  private Units(String paramString, int paramInt)
  {
    name = paramString;
    value = paramInt;
    map[paramInt] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\Units.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */