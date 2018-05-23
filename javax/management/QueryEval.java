package javax.management;

import java.io.Serializable;

public abstract class QueryEval
  implements Serializable
{
  private static final long serialVersionUID = 2675899265640874796L;
  private static ThreadLocal<MBeanServer> server = new InheritableThreadLocal();
  
  public QueryEval() {}
  
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    server.set(paramMBeanServer);
  }
  
  public static MBeanServer getMBeanServer()
  {
    return (MBeanServer)server.get();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\QueryEval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */