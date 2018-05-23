package sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;

public abstract interface BasicFileAttributesHolder
{
  public abstract BasicFileAttributes get();
  
  public abstract void invalidate();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\BasicFileAttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */