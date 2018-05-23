package javax.xml.bind;

import java.security.PrivilegedAction;

final class GetPropertyAction
  implements PrivilegedAction<String>
{
  private final String propertyName;
  
  public GetPropertyAction(String paramString)
  {
    propertyName = paramString;
  }
  
  public String run()
  {
    return System.getProperty(propertyName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\GetPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */