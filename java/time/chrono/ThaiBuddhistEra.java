package java.time.chrono;

import java.time.DateTimeException;

public enum ThaiBuddhistEra
  implements Era
{
  BEFORE_BE,  BE;
  
  private ThaiBuddhistEra() {}
  
  public static ThaiBuddhistEra of(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return BEFORE_BE;
    case 1: 
      return BE;
    }
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue()
  {
    return ordinal();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ThaiBuddhistEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */