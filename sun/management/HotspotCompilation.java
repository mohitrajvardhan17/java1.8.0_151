package sun.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import sun.management.counter.Counter;
import sun.management.counter.LongCounter;
import sun.management.counter.StringCounter;

class HotspotCompilation
  implements HotspotCompilationMBean
{
  private VMManagement jvm;
  private static final String JAVA_CI = "java.ci.";
  private static final String COM_SUN_CI = "com.sun.ci.";
  private static final String SUN_CI = "sun.ci.";
  private static final String CI_COUNTER_NAME_PATTERN = "java.ci.|com.sun.ci.|sun.ci.";
  private LongCounter compilerThreads;
  private LongCounter totalCompiles;
  private LongCounter totalBailouts;
  private LongCounter totalInvalidates;
  private LongCounter nmethodCodeSize;
  private LongCounter nmethodSize;
  private StringCounter lastMethod;
  private LongCounter lastSize;
  private LongCounter lastType;
  private StringCounter lastFailedMethod;
  private LongCounter lastFailedType;
  private StringCounter lastInvalidatedMethod;
  private LongCounter lastInvalidatedType;
  private CompilerThreadInfo[] threads;
  private int numActiveThreads;
  private Map<String, Counter> counters;
  
  HotspotCompilation(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
    initCompilerCounters();
  }
  
  private Counter lookup(String paramString)
  {
    Counter localCounter = null;
    if ((localCounter = (Counter)counters.get("sun.ci." + paramString)) != null) {
      return localCounter;
    }
    if ((localCounter = (Counter)counters.get("com.sun.ci." + paramString)) != null) {
      return localCounter;
    }
    if ((localCounter = (Counter)counters.get("java.ci." + paramString)) != null) {
      return localCounter;
    }
    throw new AssertionError("Counter " + paramString + " does not exist");
  }
  
  private void initCompilerCounters()
  {
    counters = new TreeMap();
    Iterator localIterator = getInternalCompilerCounters().iterator();
    while (localIterator.hasNext())
    {
      Counter localCounter = (Counter)localIterator.next();
      counters.put(localCounter.getName(), localCounter);
    }
    compilerThreads = ((LongCounter)lookup("threads"));
    totalCompiles = ((LongCounter)lookup("totalCompiles"));
    totalBailouts = ((LongCounter)lookup("totalBailouts"));
    totalInvalidates = ((LongCounter)lookup("totalInvalidates"));
    nmethodCodeSize = ((LongCounter)lookup("nmethodCodeSize"));
    nmethodSize = ((LongCounter)lookup("nmethodSize"));
    lastMethod = ((StringCounter)lookup("lastMethod"));
    lastSize = ((LongCounter)lookup("lastSize"));
    lastType = ((LongCounter)lookup("lastType"));
    lastFailedMethod = ((StringCounter)lookup("lastFailedMethod"));
    lastFailedType = ((LongCounter)lookup("lastFailedType"));
    lastInvalidatedMethod = ((StringCounter)lookup("lastInvalidatedMethod"));
    lastInvalidatedType = ((LongCounter)lookup("lastInvalidatedType"));
    numActiveThreads = ((int)compilerThreads.longValue());
    threads = new CompilerThreadInfo[numActiveThreads + 1];
    if (counters.containsKey("sun.ci.adapterThread.compiles"))
    {
      threads[0] = new CompilerThreadInfo("adapterThread", 0);
      numActiveThreads += 1;
    }
    else
    {
      threads[0] = null;
    }
    for (int i = 1; i < threads.length; i++) {
      threads[i] = new CompilerThreadInfo("compilerThread", i - 1);
    }
  }
  
  public int getCompilerThreadCount()
  {
    return numActiveThreads;
  }
  
  public long getTotalCompileCount()
  {
    return totalCompiles.longValue();
  }
  
  public long getBailoutCompileCount()
  {
    return totalBailouts.longValue();
  }
  
  public long getInvalidatedCompileCount()
  {
    return totalInvalidates.longValue();
  }
  
  public long getCompiledMethodCodeSize()
  {
    return nmethodCodeSize.longValue();
  }
  
  public long getCompiledMethodSize()
  {
    return nmethodSize.longValue();
  }
  
  public List<CompilerThreadStat> getCompilerThreadStats()
  {
    ArrayList localArrayList = new ArrayList(threads.length);
    int i = 0;
    if (threads[0] == null) {}
    for (i = 1; i < threads.length; i++) {
      localArrayList.add(threads[i].getCompilerThreadStat());
    }
    return localArrayList;
  }
  
  public MethodInfo getLastCompile()
  {
    return new MethodInfo(lastMethod.stringValue(), (int)lastType.longValue(), (int)lastSize.longValue());
  }
  
  public MethodInfo getFailedCompile()
  {
    return new MethodInfo(lastFailedMethod.stringValue(), (int)lastFailedType.longValue(), -1);
  }
  
  public MethodInfo getInvalidatedCompile()
  {
    return new MethodInfo(lastInvalidatedMethod.stringValue(), (int)lastInvalidatedType.longValue(), -1);
  }
  
  public List<Counter> getInternalCompilerCounters()
  {
    return jvm.getInternalCounters("java.ci.|com.sun.ci.|sun.ci.");
  }
  
  private class CompilerThreadInfo
  {
    int index;
    String name;
    StringCounter method;
    LongCounter type;
    LongCounter compiles;
    LongCounter time;
    
    CompilerThreadInfo(String paramString, int paramInt)
    {
      String str = paramString + "." + paramInt + ".";
      name = (paramString + "-" + paramInt);
      method = ((StringCounter)HotspotCompilation.this.lookup(str + "method"));
      type = ((LongCounter)HotspotCompilation.this.lookup(str + "type"));
      compiles = ((LongCounter)HotspotCompilation.this.lookup(str + "compiles"));
      time = ((LongCounter)HotspotCompilation.this.lookup(str + "time"));
    }
    
    CompilerThreadInfo(String paramString)
    {
      String str = paramString + ".";
      name = paramString;
      method = ((StringCounter)HotspotCompilation.this.lookup(str + "method"));
      type = ((LongCounter)HotspotCompilation.this.lookup(str + "type"));
      compiles = ((LongCounter)HotspotCompilation.this.lookup(str + "compiles"));
      time = ((LongCounter)HotspotCompilation.this.lookup(str + "time"));
    }
    
    CompilerThreadStat getCompilerThreadStat()
    {
      MethodInfo localMethodInfo = new MethodInfo(method.stringValue(), (int)type.longValue(), -1);
      return new CompilerThreadStat(name, compiles.longValue(), time.longValue(), localMethodInfo);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\HotspotCompilation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */