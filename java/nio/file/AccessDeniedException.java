package java.nio.file;

public class AccessDeniedException
  extends FileSystemException
{
  private static final long serialVersionUID = 4943049599949219617L;
  
  public AccessDeniedException(String paramString)
  {
    super(paramString);
  }
  
  public AccessDeniedException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, paramString3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\AccessDeniedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */