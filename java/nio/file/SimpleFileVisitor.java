package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class SimpleFileVisitor<T>
  implements FileVisitor<T>
{
  protected SimpleFileVisitor() {}
  
  public FileVisitResult preVisitDirectory(T paramT, BasicFileAttributes paramBasicFileAttributes)
    throws IOException
  {
    Objects.requireNonNull(paramT);
    Objects.requireNonNull(paramBasicFileAttributes);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFile(T paramT, BasicFileAttributes paramBasicFileAttributes)
    throws IOException
  {
    Objects.requireNonNull(paramT);
    Objects.requireNonNull(paramBasicFileAttributes);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFileFailed(T paramT, IOException paramIOException)
    throws IOException
  {
    Objects.requireNonNull(paramT);
    throw paramIOException;
  }
  
  public FileVisitResult postVisitDirectory(T paramT, IOException paramIOException)
    throws IOException
  {
    Objects.requireNonNull(paramT);
    if (paramIOException != null) {
      throw paramIOException;
    }
    return FileVisitResult.CONTINUE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\SimpleFileVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */