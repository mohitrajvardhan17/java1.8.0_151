package java.io;

public class FileReader
  extends InputStreamReader
{
  public FileReader(String paramString)
    throws FileNotFoundException
  {
    super(new FileInputStream(paramString));
  }
  
  public FileReader(File paramFile)
    throws FileNotFoundException
  {
    super(new FileInputStream(paramFile));
  }
  
  public FileReader(FileDescriptor paramFileDescriptor)
  {
    super(new FileInputStream(paramFileDescriptor));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */