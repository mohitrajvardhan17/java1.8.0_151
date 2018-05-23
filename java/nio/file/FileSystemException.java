package java.nio.file;

import java.io.IOException;

public class FileSystemException
  extends IOException
{
  static final long serialVersionUID = -3055425747967319812L;
  private final String file;
  private final String other;
  
  public FileSystemException(String paramString)
  {
    super((String)null);
    file = paramString;
    other = null;
  }
  
  public FileSystemException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString3);
    file = paramString1;
    other = paramString2;
  }
  
  public String getFile()
  {
    return file;
  }
  
  public String getOtherFile()
  {
    return other;
  }
  
  public String getReason()
  {
    return super.getMessage();
  }
  
  public String getMessage()
  {
    if ((file == null) && (other == null)) {
      return getReason();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (file != null) {
      localStringBuilder.append(file);
    }
    if (other != null)
    {
      localStringBuilder.append(" -> ");
      localStringBuilder.append(other);
    }
    if (getReason() != null)
    {
      localStringBuilder.append(": ");
      localStringBuilder.append(getReason());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\FileSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */