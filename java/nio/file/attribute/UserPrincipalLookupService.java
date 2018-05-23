package java.nio.file.attribute;

import java.io.IOException;

public abstract class UserPrincipalLookupService
{
  protected UserPrincipalLookupService() {}
  
  public abstract UserPrincipal lookupPrincipalByName(String paramString)
    throws IOException;
  
  public abstract GroupPrincipal lookupPrincipalByGroupName(String paramString)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\UserPrincipalLookupService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */