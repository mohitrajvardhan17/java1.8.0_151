package java.nio.file.attribute;

import java.io.IOException;
import java.util.List;

public abstract interface AclFileAttributeView
  extends FileOwnerAttributeView
{
  public abstract String name();
  
  public abstract List<AclEntry> getAcl()
    throws IOException;
  
  public abstract void setAcl(List<AclEntry> paramList)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\AclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */