package java.io;

public class FileNotFoundException
  extends IOException
{
  private static final long serialVersionUID = -897856973823710492L;
  
  public FileNotFoundException() {}
  
  public FileNotFoundException(String paramString)
  {
    super(paramString);
  }
  
  private FileNotFoundException(String paramString1, String paramString2)
  {
    super(paramString1 + (paramString2 == null ? "" : new StringBuilder().append(" (").append(paramString2).append(")").toString()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */