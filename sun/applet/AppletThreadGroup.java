package sun.applet;

public class AppletThreadGroup
  extends ThreadGroup
{
  public AppletThreadGroup(String paramString)
  {
    this(Thread.currentThread().getThreadGroup(), paramString);
  }
  
  public AppletThreadGroup(ThreadGroup paramThreadGroup, String paramString)
  {
    super(paramThreadGroup, paramString);
    setMaxPriority(4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletThreadGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */