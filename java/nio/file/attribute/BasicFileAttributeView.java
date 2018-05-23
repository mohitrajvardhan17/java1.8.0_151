package java.nio.file.attribute;

import java.io.IOException;

public abstract interface BasicFileAttributeView
  extends FileAttributeView
{
  public abstract String name();
  
  public abstract BasicFileAttributes readAttributes()
    throws IOException;
  
  public abstract void setTimes(FileTime paramFileTime1, FileTime paramFileTime2, FileTime paramFileTime3)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\BasicFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */