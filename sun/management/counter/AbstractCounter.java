package sun.management.counter;

public abstract class AbstractCounter
  implements Counter
{
  String name;
  Units units;
  Variability variability;
  int flags;
  int vectorLength;
  private static final long serialVersionUID = 6992337162326171013L;
  
  protected AbstractCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt1, int paramInt2)
  {
    name = paramString;
    units = paramUnits;
    variability = paramVariability;
    flags = paramInt1;
    vectorLength = paramInt2;
  }
  
  protected AbstractCounter(String paramString, Units paramUnits, Variability paramVariability, int paramInt)
  {
    this(paramString, paramUnits, paramVariability, paramInt, 0);
  }
  
  public String getName()
  {
    return name;
  }
  
  public Units getUnits()
  {
    return units;
  }
  
  public Variability getVariability()
  {
    return variability;
  }
  
  public boolean isVector()
  {
    return vectorLength > 0;
  }
  
  public int getVectorLength()
  {
    return vectorLength;
  }
  
  public boolean isInternal()
  {
    return (flags & 0x1) == 0;
  }
  
  public int getFlags()
  {
    return flags;
  }
  
  public abstract Object getValue();
  
  public String toString()
  {
    String str = getName() + ": " + getValue() + " " + getUnits();
    if (isInternal()) {
      return str + " [INTERNAL]";
    }
    return str;
  }
  
  class Flags
  {
    static final int SUPPORTED = 1;
    
    Flags() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\AbstractCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */