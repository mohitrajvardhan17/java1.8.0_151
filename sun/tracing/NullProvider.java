package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;

class NullProvider
  extends ProviderSkeleton
{
  NullProvider(Class<? extends Provider> paramClass)
  {
    super(paramClass);
  }
  
  protected ProbeSkeleton createProbe(Method paramMethod)
  {
    return new NullProbe(paramMethod.getParameterTypes());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\NullProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */