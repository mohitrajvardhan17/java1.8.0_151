package sun.management;

import com.sun.management.GcInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GcInfoBuilder
{
  private final GarbageCollectorMXBean gc;
  private final String[] poolNames;
  private String[] allItemNames;
  private CompositeType gcInfoCompositeType;
  private final int gcExtItemCount;
  private final String[] gcExtItemNames;
  private final String[] gcExtItemDescs;
  private final char[] gcExtItemTypes;
  
  GcInfoBuilder(GarbageCollectorMXBean paramGarbageCollectorMXBean, String[] paramArrayOfString)
  {
    gc = paramGarbageCollectorMXBean;
    poolNames = paramArrayOfString;
    gcExtItemCount = getNumGcExtAttributes(paramGarbageCollectorMXBean);
    gcExtItemNames = new String[gcExtItemCount];
    gcExtItemDescs = new String[gcExtItemCount];
    gcExtItemTypes = new char[gcExtItemCount];
    fillGcAttributeInfo(paramGarbageCollectorMXBean, gcExtItemCount, gcExtItemNames, gcExtItemTypes, gcExtItemDescs);
    gcInfoCompositeType = null;
  }
  
  GcInfo getLastGcInfo()
  {
    MemoryUsage[] arrayOfMemoryUsage1 = new MemoryUsage[poolNames.length];
    MemoryUsage[] arrayOfMemoryUsage2 = new MemoryUsage[poolNames.length];
    Object[] arrayOfObject = new Object[gcExtItemCount];
    return getLastGcInfo0(gc, gcExtItemCount, arrayOfObject, gcExtItemTypes, arrayOfMemoryUsage1, arrayOfMemoryUsage2);
  }
  
  public String[] getPoolNames()
  {
    return poolNames;
  }
  
  int getGcExtItemCount()
  {
    return gcExtItemCount;
  }
  
  synchronized CompositeType getGcInfoCompositeType()
  {
    if (gcInfoCompositeType != null) {
      return gcInfoCompositeType;
    }
    String[] arrayOfString1 = GcInfoCompositeData.getBaseGcInfoItemNames();
    OpenType[] arrayOfOpenType1 = GcInfoCompositeData.getBaseGcInfoItemTypes();
    int i = arrayOfString1.length;
    int j = i + gcExtItemCount;
    allItemNames = new String[j];
    String[] arrayOfString2 = new String[j];
    OpenType[] arrayOfOpenType2 = new OpenType[j];
    System.arraycopy(arrayOfString1, 0, allItemNames, 0, i);
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
    System.arraycopy(arrayOfOpenType1, 0, arrayOfOpenType2, 0, i);
    if (gcExtItemCount > 0)
    {
      fillGcAttributeInfo(gc, gcExtItemCount, gcExtItemNames, gcExtItemTypes, gcExtItemDescs);
      System.arraycopy(gcExtItemNames, 0, allItemNames, i, gcExtItemCount);
      System.arraycopy(gcExtItemDescs, 0, arrayOfString2, i, gcExtItemCount);
      int k = i;
      for (int m = 0; m < gcExtItemCount; m++)
      {
        switch (gcExtItemTypes[m])
        {
        case 'Z': 
          arrayOfOpenType2[k] = SimpleType.BOOLEAN;
          break;
        case 'B': 
          arrayOfOpenType2[k] = SimpleType.BYTE;
          break;
        case 'C': 
          arrayOfOpenType2[k] = SimpleType.CHARACTER;
          break;
        case 'S': 
          arrayOfOpenType2[k] = SimpleType.SHORT;
          break;
        case 'I': 
          arrayOfOpenType2[k] = SimpleType.INTEGER;
          break;
        case 'J': 
          arrayOfOpenType2[k] = SimpleType.LONG;
          break;
        case 'F': 
          arrayOfOpenType2[k] = SimpleType.FLOAT;
          break;
        case 'D': 
          arrayOfOpenType2[k] = SimpleType.DOUBLE;
          break;
        case 'E': 
        case 'G': 
        case 'H': 
        case 'K': 
        case 'L': 
        case 'M': 
        case 'N': 
        case 'O': 
        case 'P': 
        case 'Q': 
        case 'R': 
        case 'T': 
        case 'U': 
        case 'V': 
        case 'W': 
        case 'X': 
        case 'Y': 
        default: 
          throw new AssertionError("Unsupported type [" + gcExtItemTypes[k] + "]");
        }
        k++;
      }
    }
    CompositeType localCompositeType = null;
    try
    {
      String str = "sun.management." + gc.getName() + ".GcInfoCompositeType";
      localCompositeType = new CompositeType(str, "CompositeType for GC info for " + gc.getName(), allItemNames, arrayOfString2, arrayOfOpenType2);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw Util.newException(localOpenDataException);
    }
    gcInfoCompositeType = localCompositeType;
    return gcInfoCompositeType;
  }
  
  synchronized String[] getItemNames()
  {
    if (allItemNames == null) {
      getGcInfoCompositeType();
    }
    return allItemNames;
  }
  
  private native int getNumGcExtAttributes(GarbageCollectorMXBean paramGarbageCollectorMXBean);
  
  private native void fillGcAttributeInfo(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt, String[] paramArrayOfString1, char[] paramArrayOfChar, String[] paramArrayOfString2);
  
  private native GcInfo getLastGcInfo0(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt, Object[] paramArrayOfObject, char[] paramArrayOfChar, MemoryUsage[] paramArrayOfMemoryUsage1, MemoryUsage[] paramArrayOfMemoryUsage2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\GcInfoBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */