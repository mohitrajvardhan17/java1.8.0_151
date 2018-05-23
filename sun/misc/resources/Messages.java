package sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages
  extends ListResourceBundle
{
  public Messages() {}
  
  protected Object[][] getContents()
  {
    return new Object[][] { { "optpkg.versionerror", "ERROR: Invalid version format used in {0} JAR file. Check the documentation for the supported version format." }, { "optpkg.attributeerror", "ERROR: The required {0} JAR manifest attribute is not set in {1} JAR file." }, { "optpkg.attributeserror", "ERROR: Some required JAR manifest attributes are not set in {0} JAR file." } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\resources\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */