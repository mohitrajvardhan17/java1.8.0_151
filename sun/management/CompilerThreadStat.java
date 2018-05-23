package sun.management;

import java.io.Serializable;

public class CompilerThreadStat
  implements Serializable
{
  private String name;
  private long taskCount;
  private long compileTime;
  private MethodInfo lastMethod;
  private static final long serialVersionUID = 6992337162326171013L;
  
  CompilerThreadStat(String paramString, long paramLong1, long paramLong2, MethodInfo paramMethodInfo)
  {
    name = paramString;
    taskCount = paramLong1;
    compileTime = paramLong2;
    lastMethod = paramMethodInfo;
  }
  
  public String getName()
  {
    return name;
  }
  
  public long getCompileTaskCount()
  {
    return taskCount;
  }
  
  public long getCompileTime()
  {
    return compileTime;
  }
  
  public MethodInfo getLastCompiledMethodInfo()
  {
    return lastMethod;
  }
  
  public String toString()
  {
    return getName() + " compileTasks = " + getCompileTaskCount() + " compileTime = " + getCompileTime();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\CompilerThreadStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */