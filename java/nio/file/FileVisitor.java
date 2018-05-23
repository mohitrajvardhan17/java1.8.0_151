package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

public abstract interface FileVisitor<T>
{
  public abstract FileVisitResult preVisitDirectory(T paramT, BasicFileAttributes paramBasicFileAttributes)
    throws IOException;
  
  public abstract FileVisitResult visitFile(T paramT, BasicFileAttributes paramBasicFileAttributes)
    throws IOException;
  
  public abstract FileVisitResult visitFileFailed(T paramT, IOException paramIOException)
    throws IOException;
  
  public abstract FileVisitResult postVisitDirectory(T paramT, IOException paramIOException)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\FileVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */