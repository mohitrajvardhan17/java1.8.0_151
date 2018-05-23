package sun.tracing;

import com.sun.tracing.ProbeName;
import com.sun.tracing.Provider;
import java.io.PrintStream;
import java.lang.reflect.Method;

class PrintStreamProvider
  extends ProviderSkeleton
{
  private PrintStream stream;
  private String providerName;
  
  protected ProbeSkeleton createProbe(Method paramMethod)
  {
    String str = getAnnotationString(paramMethod, ProbeName.class, paramMethod.getName());
    return new PrintStreamProbe(this, str, paramMethod.getParameterTypes());
  }
  
  PrintStreamProvider(Class<? extends Provider> paramClass, PrintStream paramPrintStream)
  {
    super(paramClass);
    stream = paramPrintStream;
    providerName = getProviderName();
  }
  
  PrintStream getStream()
  {
    return stream;
  }
  
  String getName()
  {
    return providerName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\PrintStreamProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */