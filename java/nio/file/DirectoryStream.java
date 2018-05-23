package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public abstract interface DirectoryStream<T>
  extends Closeable, Iterable<T>
{
  public abstract Iterator<T> iterator();
  
  @FunctionalInterface
  public static abstract interface Filter<T>
  {
    public abstract boolean accept(T paramT)
      throws IOException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\DirectoryStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */