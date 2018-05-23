package java.time.chrono;

import java.time.DateTimeException;

public enum IsoEra
  implements Era
{
  BCE,  CE;
  
  private IsoEra() {}
  
  public static IsoEra of(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return BCE;
    case 1: 
      return CE;
    }
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue()
  {
    return ordinal();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\IsoEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */