package sun.tracing;

import java.io.PrintStream;

class PrintStreamProbe
  extends ProbeSkeleton
{
  private PrintStreamProvider provider;
  private String name;
  
  PrintStreamProbe(PrintStreamProvider paramPrintStreamProvider, String paramString, Class<?>[] paramArrayOfClass)
  {
    super(paramArrayOfClass);
    provider = paramPrintStreamProvider;
    name = paramString;
  }
  
  public boolean isEnabled()
  {
    return true;
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(provider.getName());
    localStringBuffer.append(".");
    localStringBuffer.append(name);
    localStringBuffer.append("(");
    int i = 1;
    for (Object localObject : paramArrayOfObject)
    {
      if (i == 0) {
        localStringBuffer.append(",");
      } else {
        i = 0;
      }
      localStringBuffer.append(localObject.toString());
    }
    localStringBuffer.append(")");
    provider.getStream().println(localStringBuffer.toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\PrintStreamProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */