package javax.tools;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ForwardingJavaFileManager<M extends JavaFileManager>
  implements JavaFileManager
{
  protected final M fileManager;
  
  protected ForwardingJavaFileManager(M paramM)
  {
    paramM.getClass();
    fileManager = paramM;
  }
  
  public ClassLoader getClassLoader(JavaFileManager.Location paramLocation)
  {
    return fileManager.getClassLoader(paramLocation);
  }
  
  public Iterable<JavaFileObject> list(JavaFileManager.Location paramLocation, String paramString, Set<JavaFileObject.Kind> paramSet, boolean paramBoolean)
    throws IOException
  {
    return fileManager.list(paramLocation, paramString, paramSet, paramBoolean);
  }
  
  public String inferBinaryName(JavaFileManager.Location paramLocation, JavaFileObject paramJavaFileObject)
  {
    return fileManager.inferBinaryName(paramLocation, paramJavaFileObject);
  }
  
  public boolean isSameFile(FileObject paramFileObject1, FileObject paramFileObject2)
  {
    return fileManager.isSameFile(paramFileObject1, paramFileObject2);
  }
  
  public boolean handleOption(String paramString, Iterator<String> paramIterator)
  {
    return fileManager.handleOption(paramString, paramIterator);
  }
  
  public boolean hasLocation(JavaFileManager.Location paramLocation)
  {
    return fileManager.hasLocation(paramLocation);
  }
  
  public int isSupportedOption(String paramString)
  {
    return fileManager.isSupportedOption(paramString);
  }
  
  public JavaFileObject getJavaFileForInput(JavaFileManager.Location paramLocation, String paramString, JavaFileObject.Kind paramKind)
    throws IOException
  {
    return fileManager.getJavaFileForInput(paramLocation, paramString, paramKind);
  }
  
  public JavaFileObject getJavaFileForOutput(JavaFileManager.Location paramLocation, String paramString, JavaFileObject.Kind paramKind, FileObject paramFileObject)
    throws IOException
  {
    return fileManager.getJavaFileForOutput(paramLocation, paramString, paramKind, paramFileObject);
  }
  
  public FileObject getFileForInput(JavaFileManager.Location paramLocation, String paramString1, String paramString2)
    throws IOException
  {
    return fileManager.getFileForInput(paramLocation, paramString1, paramString2);
  }
  
  public FileObject getFileForOutput(JavaFileManager.Location paramLocation, String paramString1, String paramString2, FileObject paramFileObject)
    throws IOException
  {
    return fileManager.getFileForOutput(paramLocation, paramString1, paramString2, paramFileObject);
  }
  
  public void flush()
    throws IOException
  {
    fileManager.flush();
  }
  
  public void close()
    throws IOException
  {
    fileManager.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\ForwardingJavaFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */