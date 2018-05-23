package java.nio.file;

public class FileAlreadyExistsException
  extends FileSystemException
{
  static final long serialVersionUID = 7579540934498831181L;
  
  public FileAlreadyExistsException(String paramString)
  {
    super(paramString);
  }
  
  public FileAlreadyExistsException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, paramString3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\FileAlreadyExistsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */