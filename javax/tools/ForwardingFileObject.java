package javax.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public class ForwardingFileObject<F extends FileObject>
  implements FileObject
{
  protected final F fileObject;
  
  protected ForwardingFileObject(F paramF)
  {
    paramF.getClass();
    fileObject = paramF;
  }
  
  public URI toUri()
  {
    return fileObject.toUri();
  }
  
  public String getName()
  {
    return fileObject.getName();
  }
  
  public InputStream openInputStream()
    throws IOException
  {
    return fileObject.openInputStream();
  }
  
  public OutputStream openOutputStream()
    throws IOException
  {
    return fileObject.openOutputStream();
  }
  
  public Reader openReader(boolean paramBoolean)
    throws IOException
  {
    return fileObject.openReader(paramBoolean);
  }
  
  public CharSequence getCharContent(boolean paramBoolean)
    throws IOException
  {
    return fileObject.getCharContent(paramBoolean);
  }
  
  public Writer openWriter()
    throws IOException
  {
    return fileObject.openWriter();
  }
  
  public long getLastModified()
  {
    return fileObject.getLastModified();
  }
  
  public boolean delete()
  {
    return fileObject.delete();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\ForwardingFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */