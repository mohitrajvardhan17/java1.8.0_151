package java.nio.file.attribute;

import java.io.IOException;
import java.util.Set;

public abstract interface PosixFileAttributeView
  extends BasicFileAttributeView, FileOwnerAttributeView
{
  public abstract String name();
  
  public abstract PosixFileAttributes readAttributes()
    throws IOException;
  
  public abstract void setPermissions(Set<PosixFilePermission> paramSet)
    throws IOException;
  
  public abstract void setGroup(GroupPrincipal paramGroupPrincipal)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\PosixFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */