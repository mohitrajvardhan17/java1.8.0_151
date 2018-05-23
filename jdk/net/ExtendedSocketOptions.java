package jdk.net;

import java.net.SocketOption;
import jdk.Exported;

@Exported
public final class ExtendedSocketOptions
{
  public static final SocketOption<SocketFlow> SO_FLOW_SLA = new ExtSocketOption("SO_FLOW_SLA", SocketFlow.class);
  
  private ExtendedSocketOptions() {}
  
  private static class ExtSocketOption<T>
    implements SocketOption<T>
  {
    private final String name;
    private final Class<T> type;
    
    ExtSocketOption(String paramString, Class<T> paramClass)
    {
      name = paramString;
      type = paramClass;
    }
    
    public String name()
    {
      return name;
    }
    
    public Class<T> type()
    {
      return type;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\net\ExtendedSocketOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */