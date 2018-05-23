package sun.security.jgss;

public class GSSCaller
{
  public static final GSSCaller CALLER_UNKNOWN = new GSSCaller("UNKNOWN");
  public static final GSSCaller CALLER_INITIATE = new GSSCaller("INITIATE");
  public static final GSSCaller CALLER_ACCEPT = new GSSCaller("ACCEPT");
  public static final GSSCaller CALLER_SSL_CLIENT = new GSSCaller("SSL_CLIENT");
  public static final GSSCaller CALLER_SSL_SERVER = new GSSCaller("SSL_SERVER");
  private String name;
  
  GSSCaller(String paramString)
  {
    name = paramString;
  }
  
  public String toString()
  {
    return "GSSCaller{" + name + '}';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSCaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */