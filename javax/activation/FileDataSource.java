package javax.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDataSource
  implements DataSource
{
  private File _file = null;
  private FileTypeMap typeMap = null;
  
  public FileDataSource(File paramFile)
  {
    _file = paramFile;
  }
  
  public FileDataSource(String paramString)
  {
    this(new File(paramString));
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return new FileInputStream(_file);
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    return new FileOutputStream(_file);
  }
  
  public String getContentType()
  {
    if (typeMap == null) {
      return FileTypeMap.getDefaultFileTypeMap().getContentType(_file);
    }
    return typeMap.getContentType(_file);
  }
  
  public String getName()
  {
    return _file.getName();
  }
  
  public File getFile()
  {
    return _file;
  }
  
  public void setFileTypeMap(FileTypeMap paramFileTypeMap)
  {
    typeMap = paramFileTypeMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\FileDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */