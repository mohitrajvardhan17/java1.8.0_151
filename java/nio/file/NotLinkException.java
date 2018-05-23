package java.nio.file;

public class NotLinkException
  extends FileSystemException
{
  static final long serialVersionUID = -388655596416518021L;
  
  public NotLinkException(String paramString)
  {
    super(paramString);
  }
  
  public NotLinkException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, paramString3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\NotLinkException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */