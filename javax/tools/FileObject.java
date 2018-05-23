package javax.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public abstract interface FileObject
{
  public abstract URI toUri();
  
  public abstract String getName();
  
  public abstract InputStream openInputStream()
    throws IOException;
  
  public abstract OutputStream openOutputStream()
    throws IOException;
  
  public abstract Reader openReader(boolean paramBoolean)
    throws IOException;
  
  public abstract CharSequence getCharContent(boolean paramBoolean)
    throws IOException;
  
  public abstract Writer openWriter()
    throws IOException;
  
  public abstract long getLastModified();
  
  public abstract boolean delete();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\FileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */