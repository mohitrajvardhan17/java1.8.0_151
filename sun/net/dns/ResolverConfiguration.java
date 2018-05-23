package sun.net.dns;

import java.util.List;

public abstract class ResolverConfiguration
{
  private static final Object lock = new Object();
  private static ResolverConfiguration provider;
  
  protected ResolverConfiguration() {}
  
  public static ResolverConfiguration open()
  {
    synchronized (lock)
    {
      if (provider == null) {
        provider = new ResolverConfigurationImpl();
      }
      return provider;
    }
  }
  
  public abstract List<String> searchlist();
  
  public abstract List<String> nameservers();
  
  public abstract Options options();
  
  public static abstract class Options
  {
    public Options() {}
    
    public int attempts()
    {
      return -1;
    }
    
    public int retrans()
    {
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\dns\ResolverConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */