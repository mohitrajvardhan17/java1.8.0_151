package sun.security.action;

import java.io.File;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;

public class OpenFileInputStreamAction
  implements PrivilegedExceptionAction<FileInputStream>
{
  private final File file;
  
  public OpenFileInputStreamAction(File paramFile)
  {
    file = paramFile;
  }
  
  public OpenFileInputStreamAction(String paramString)
  {
    file = new File(paramString);
  }
  
  public FileInputStream run()
    throws Exception
  {
    return new FileInputStream(file);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\action\OpenFileInputStreamAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */