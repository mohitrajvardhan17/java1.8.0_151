package java.nio.file.attribute;

import java.io.IOException;

public abstract interface DosFileAttributeView
  extends BasicFileAttributeView
{
  public abstract String name();
  
  public abstract DosFileAttributes readAttributes()
    throws IOException;
  
  public abstract void setReadOnly(boolean paramBoolean)
    throws IOException;
  
  public abstract void setHidden(boolean paramBoolean)
    throws IOException;
  
  public abstract void setSystem(boolean paramBoolean)
    throws IOException;
  
  public abstract void setArchive(boolean paramBoolean)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\DosFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */