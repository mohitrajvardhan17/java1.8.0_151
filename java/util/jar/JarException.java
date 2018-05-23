package java.util.jar;

import java.util.zip.ZipException;

public class JarException
  extends ZipException
{
  private static final long serialVersionUID = 7159778400963954473L;
  
  public JarException() {}
  
  public JarException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */