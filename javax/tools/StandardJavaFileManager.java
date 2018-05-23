package javax.tools;

import java.io.File;
import java.io.IOException;

public abstract interface StandardJavaFileManager
  extends JavaFileManager
{
  public abstract boolean isSameFile(FileObject paramFileObject1, FileObject paramFileObject2);
  
  public abstract Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> paramIterable);
  
  public abstract Iterable<? extends JavaFileObject> getJavaFileObjects(File... paramVarArgs);
  
  public abstract Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> paramIterable);
  
  public abstract Iterable<? extends JavaFileObject> getJavaFileObjects(String... paramVarArgs);
  
  public abstract void setLocation(JavaFileManager.Location paramLocation, Iterable<? extends File> paramIterable)
    throws IOException;
  
  public abstract Iterable<? extends File> getLocation(JavaFileManager.Location paramLocation);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\StandardJavaFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */