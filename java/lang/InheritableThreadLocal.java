package java.lang;

public class InheritableThreadLocal<T>
  extends ThreadLocal<T>
{
  public InheritableThreadLocal() {}
  
  protected T childValue(T paramT)
  {
    return paramT;
  }
  
  ThreadLocal.ThreadLocalMap getMap(Thread paramThread)
  {
    return inheritableThreadLocals;
  }
  
  void createMap(Thread paramThread, T paramT)
  {
    inheritableThreadLocals = new ThreadLocal.ThreadLocalMap(this, paramT);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\InheritableThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */