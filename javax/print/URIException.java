package javax.print;

import java.net.URI;

public abstract interface URIException
{
  public static final int URIInaccessible = 1;
  public static final int URISchemeNotSupported = 2;
  public static final int URIOtherProblem = -1;
  
  public abstract URI getUnsupportedURI();
  
  public abstract int getReason();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\URIException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */