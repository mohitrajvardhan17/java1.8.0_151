package java.nio.file;

public class NoSuchFileException
  extends FileSystemException
{
  static final long serialVersionUID = -1390291775875351931L;
  
  public NoSuchFileException(String paramString)
  {
    super(paramString);
  }
  
  public NoSuchFileException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, paramString3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\NoSuchFileException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */