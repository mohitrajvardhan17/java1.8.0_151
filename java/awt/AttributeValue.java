package java.awt;

import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

abstract class AttributeValue
{
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AttributeValue");
  private final int value;
  private final String[] names;
  
  protected AttributeValue(int paramInt, String[] paramArrayOfString)
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("value = " + paramInt + ", names = " + paramArrayOfString);
    }
    if ((log.isLoggable(PlatformLogger.Level.FINER)) && ((paramInt < 0) || (paramArrayOfString == null) || (paramInt >= paramArrayOfString.length))) {
      log.finer("Assertion failed");
    }
    value = paramInt;
    names = paramArrayOfString;
  }
  
  public int hashCode()
  {
    return value;
  }
  
  public String toString()
  {
    return names[value];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\AttributeValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */