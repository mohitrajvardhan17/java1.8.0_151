package java.security;

import java.io.Serializable;

public abstract interface Key
  extends Serializable
{
  public static final long serialVersionUID = 6603384152749567654L;
  
  public abstract String getAlgorithm();
  
  public abstract String getFormat();
  
  public abstract byte[] getEncoded();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */