package sun.management.counter.perf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.management.counter.Counter;
import sun.management.counter.Units;

public class PerfInstrumentation
{
  private ByteBuffer buffer;
  private Prologue prologue;
  private long lastModificationTime;
  private long lastUsed;
  private int nextEntry;
  private SortedMap<String, Counter> map;
  
  public PerfInstrumentation(ByteBuffer paramByteBuffer)
  {
    prologue = new Prologue(paramByteBuffer);
    buffer = paramByteBuffer;
    buffer.order(prologue.getByteOrder());
    int i = getMajorVersion();
    int j = getMinorVersion();
    if (i < 2) {
      throw new InstrumentationException("Unsupported version: " + i + "." + j);
    }
    rewind();
  }
  
  public int getMajorVersion()
  {
    return prologue.getMajorVersion();
  }
  
  public int getMinorVersion()
  {
    return prologue.getMinorVersion();
  }
  
  public long getModificationTimeStamp()
  {
    return prologue.getModificationTimeStamp();
  }
  
  void rewind()
  {
    buffer.rewind();
    buffer.position(prologue.getEntryOffset());
    nextEntry = buffer.position();
    map = new TreeMap();
  }
  
  boolean hasNext()
  {
    return nextEntry < prologue.getUsed();
  }
  
  Counter getNextCounter()
  {
    if (!hasNext()) {
      return null;
    }
    if (nextEntry % 4 != 0) {
      throw new InstrumentationException("Entry index not properly aligned: " + nextEntry);
    }
    if ((nextEntry < 0) || (nextEntry > buffer.limit())) {
      throw new InstrumentationException("Entry index out of bounds: nextEntry = " + nextEntry + ", limit = " + buffer.limit());
    }
    buffer.position(nextEntry);
    PerfDataEntry localPerfDataEntry = new PerfDataEntry(buffer);
    nextEntry += localPerfDataEntry.size();
    Object localObject = null;
    PerfDataType localPerfDataType = localPerfDataEntry.type();
    if (localPerfDataType == PerfDataType.BYTE)
    {
      if ((localPerfDataEntry.units() == Units.STRING) && (localPerfDataEntry.vectorLength() > 0)) {
        localObject = new PerfStringCounter(localPerfDataEntry.name(), localPerfDataEntry.variability(), localPerfDataEntry.flags(), localPerfDataEntry.vectorLength(), localPerfDataEntry.byteData());
      } else if (localPerfDataEntry.vectorLength() > 0) {
        localObject = new PerfByteArrayCounter(localPerfDataEntry.name(), localPerfDataEntry.units(), localPerfDataEntry.variability(), localPerfDataEntry.flags(), localPerfDataEntry.vectorLength(), localPerfDataEntry.byteData());
      } else if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    else if (localPerfDataType == PerfDataType.LONG)
    {
      if (localPerfDataEntry.vectorLength() == 0) {
        localObject = new PerfLongCounter(localPerfDataEntry.name(), localPerfDataEntry.units(), localPerfDataEntry.variability(), localPerfDataEntry.flags(), localPerfDataEntry.longData());
      } else {
        localObject = new PerfLongArrayCounter(localPerfDataEntry.name(), localPerfDataEntry.units(), localPerfDataEntry.variability(), localPerfDataEntry.flags(), localPerfDataEntry.vectorLength(), localPerfDataEntry.longData());
      }
    }
    else if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return (Counter)localObject;
  }
  
  public synchronized List<Counter> getAllCounters()
  {
    while (hasNext())
    {
      Counter localCounter = getNextCounter();
      if (localCounter != null) {
        map.put(localCounter.getName(), localCounter);
      }
    }
    return new ArrayList(map.values());
  }
  
  public synchronized List<Counter> findByPattern(String paramString)
  {
    while (hasNext())
    {
      localObject = getNextCounter();
      if (localObject != null) {
        map.put(((Counter)localObject).getName(), localObject);
      }
    }
    Object localObject = Pattern.compile(paramString);
    Matcher localMatcher = ((Pattern)localObject).matcher("");
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = map.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      localMatcher.reset(str);
      if (localMatcher.lookingAt()) {
        localArrayList.add(localEntry.getValue());
      }
    }
    return localArrayList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\perf\PerfInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */