package sun.management.counter;

import java.io.Serializable;

public class Variability
  implements Serializable
{
  private static final int NATTRIBUTES = 4;
  private static Variability[] map = new Variability[4];
  private String name;
  private int value;
  public static final Variability INVALID = new Variability("Invalid", 0);
  public static final Variability CONSTANT = new Variability("Constant", 1);
  public static final Variability MONOTONIC = new Variability("Monotonic", 2);
  public static final Variability VARIABLE = new Variability("Variable", 3);
  private static final long serialVersionUID = 6992337162326171013L;
  
  public String toString()
  {
    return name;
  }
  
  public int intValue()
  {
    return value;
  }
  
  public static Variability toVariability(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= map.length) || (map[paramInt] == null)) {
      return INVALID;
    }
    return map[paramInt];
  }
  
  private Variability(String paramString, int paramInt)
  {
    name = paramString;
    value = paramInt;
    map[paramInt] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\Variability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */