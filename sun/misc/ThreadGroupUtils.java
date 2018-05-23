package sun.misc;

public final class ThreadGroupUtils
{
  private ThreadGroupUtils() {}
  
  public static ThreadGroup getRootThreadGroup()
  {
    Object localObject = Thread.currentThread().getThreadGroup();
    for (ThreadGroup localThreadGroup = ((ThreadGroup)localObject).getParent(); localThreadGroup != null; localThreadGroup = ((ThreadGroup)localObject).getParent()) {
      localObject = localThreadGroup;
    }
    return (ThreadGroup)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ThreadGroupUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */