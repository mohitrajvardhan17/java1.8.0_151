package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class MultiplexProbe
  extends ProbeSkeleton
{
  private Set<Probe> probes = new HashSet();
  
  MultiplexProbe(Method paramMethod, Set<Provider> paramSet)
  {
    super(paramMethod.getParameterTypes());
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      Provider localProvider = (Provider)localIterator.next();
      Probe localProbe = localProvider.getProbe(paramMethod);
      if (localProbe != null) {
        probes.add(localProbe);
      }
    }
  }
  
  public boolean isEnabled()
  {
    Iterator localIterator = probes.iterator();
    while (localIterator.hasNext())
    {
      Probe localProbe = (Probe)localIterator.next();
      if (localProbe.isEnabled()) {
        return true;
      }
    }
    return false;
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject)
  {
    Iterator localIterator = probes.iterator();
    while (localIterator.hasNext())
    {
      Probe localProbe = (Probe)localIterator.next();
      try
      {
        ProbeSkeleton localProbeSkeleton = (ProbeSkeleton)localProbe;
        localProbeSkeleton.uncheckedTrigger(paramArrayOfObject);
      }
      catch (ClassCastException localClassCastException)
      {
        try
        {
          Method localMethod = Probe.class.getMethod("trigger", new Class[] { Class.forName("[java.lang.Object") });
          localMethod.invoke(localProbe, paramArrayOfObject);
        }
        catch (Exception localException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\MultiplexProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */