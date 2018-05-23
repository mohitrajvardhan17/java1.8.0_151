package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.io.PrintStream;

public class PrintStreamProviderFactory
  extends ProviderFactory
{
  private PrintStream stream;
  
  public PrintStreamProviderFactory(PrintStream paramPrintStream)
  {
    stream = paramPrintStream;
  }
  
  public <T extends Provider> T createProvider(Class<T> paramClass)
  {
    PrintStreamProvider localPrintStreamProvider = new PrintStreamProvider(paramClass, stream);
    localPrintStreamProvider.init();
    return localPrintStreamProvider.newProxyInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\PrintStreamProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */