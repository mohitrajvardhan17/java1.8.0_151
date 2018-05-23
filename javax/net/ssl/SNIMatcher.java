package javax.net.ssl;

public abstract class SNIMatcher
{
  private final int type;
  
  protected SNIMatcher(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Server name type cannot be less than zero");
    }
    if (paramInt > 255) {
      throw new IllegalArgumentException("Server name type cannot be greater than 255");
    }
    type = paramInt;
  }
  
  public final int getType()
  {
    return type;
  }
  
  public abstract boolean matches(SNIServerName paramSNIServerName);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SNIMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */