package java.nio.file.attribute;

import java.io.IOException;

public class UserPrincipalNotFoundException
  extends IOException
{
  static final long serialVersionUID = -5369283889045833024L;
  private final String name;
  
  public UserPrincipalNotFoundException(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\UserPrincipalNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */