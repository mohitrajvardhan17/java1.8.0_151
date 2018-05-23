package sun.management;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import sun.management.counter.Counter;
import sun.management.counter.Units;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

public class ConnectorAddressLink
{
  private static final String CONNECTOR_ADDRESS_COUNTER = "sun.management.JMXConnectorServer.address";
  private static final String REMOTE_CONNECTOR_COUNTER_PREFIX = "sun.management.JMXConnectorServer.";
  private static AtomicInteger counter = new AtomicInteger();
  
  public ConnectorAddressLink() {}
  
  public static void export(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("address not specified");
    }
    Perf localPerf = Perf.getPerf();
    localPerf.createString("sun.management.JMXConnectorServer.address", 1, Units.STRING.intValue(), paramString);
  }
  
  public static String importFrom(int paramInt)
    throws IOException
  {
    Perf localPerf = Perf.getPerf();
    ByteBuffer localByteBuffer;
    try
    {
      localByteBuffer = localPerf.attach(paramInt, "r");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IOException(localIllegalArgumentException.getMessage());
    }
    List localList = new PerfInstrumentation(localByteBuffer).findByPattern("sun.management.JMXConnectorServer.address");
    Iterator localIterator = localList.iterator();
    if (localIterator.hasNext())
    {
      Counter localCounter = (Counter)localIterator.next();
      return (String)localCounter.getValue();
    }
    return null;
  }
  
  public static void exportRemote(Map<String, String> paramMap)
  {
    int i = counter.getAndIncrement();
    Perf localPerf = Perf.getPerf();
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localPerf.createString("sun.management.JMXConnectorServer." + i + "." + (String)localEntry.getKey(), 1, Units.STRING.intValue(), (String)localEntry.getValue());
    }
  }
  
  public static Map<String, String> importRemoteFrom(int paramInt)
    throws IOException
  {
    Perf localPerf = Perf.getPerf();
    ByteBuffer localByteBuffer;
    try
    {
      localByteBuffer = localPerf.attach(paramInt, "r");
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IOException(localIllegalArgumentException.getMessage());
    }
    List localList = new PerfInstrumentation(localByteBuffer).getAllCounters();
    HashMap localHashMap = new HashMap();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Counter localCounter = (Counter)localIterator.next();
      String str = localCounter.getName();
      if ((str.startsWith("sun.management.JMXConnectorServer.")) && (!str.equals("sun.management.JMXConnectorServer.address"))) {
        localHashMap.put(str, localCounter.getValue().toString());
      }
    }
    return localHashMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ConnectorAddressLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */