package sun.security.provider;

public final class NativePRNG
{
  public NativePRNG() {}
  
  static boolean isAvailable()
  {
    return false;
  }
  
  public static final class Blocking
  {
    public Blocking() {}
    
    static boolean isAvailable()
    {
      return false;
    }
  }
  
  public static final class NonBlocking
  {
    public NonBlocking() {}
    
    static boolean isAvailable()
    {
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\NativePRNG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */