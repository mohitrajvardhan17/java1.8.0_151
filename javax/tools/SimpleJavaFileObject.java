package javax.tools;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.nio.CharBuffer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class SimpleJavaFileObject
  implements JavaFileObject
{
  protected final URI uri;
  protected final JavaFileObject.Kind kind;
  
  protected SimpleJavaFileObject(URI paramURI, JavaFileObject.Kind paramKind)
  {
    paramURI.getClass();
    paramKind.getClass();
    if (paramURI.getPath() == null) {
      throw new IllegalArgumentException("URI must have a path: " + paramURI);
    }
    uri = paramURI;
    kind = paramKind;
  }
  
  public URI toUri()
  {
    return uri;
  }
  
  public String getName()
  {
    return toUri().getPath();
  }
  
  public InputStream openInputStream()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public OutputStream openOutputStream()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public Reader openReader(boolean paramBoolean)
    throws IOException
  {
    CharSequence localCharSequence = getCharContent(paramBoolean);
    if (localCharSequence == null) {
      throw new UnsupportedOperationException();
    }
    if ((localCharSequence instanceof CharBuffer))
    {
      CharBuffer localCharBuffer = (CharBuffer)localCharSequence;
      if (localCharBuffer.hasArray()) {
        return new CharArrayReader(localCharBuffer.array());
      }
    }
    return new StringReader(localCharSequence.toString());
  }
  
  public CharSequence getCharContent(boolean paramBoolean)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public Writer openWriter()
    throws IOException
  {
    return new OutputStreamWriter(openOutputStream());
  }
  
  public long getLastModified()
  {
    return 0L;
  }
  
  public boolean delete()
  {
    return false;
  }
  
  public JavaFileObject.Kind getKind()
  {
    return kind;
  }
  
  public boolean isNameCompatible(String paramString, JavaFileObject.Kind paramKind)
  {
    String str = paramString + extension;
    return (paramKind.equals(getKind())) && ((str.equals(toUri().getPath())) || (toUri().getPath().endsWith("/" + str)));
  }
  
  public NestingKind getNestingKind()
  {
    return null;
  }
  
  public Modifier getAccessLevel()
  {
    return null;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + toUri() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\SimpleJavaFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */