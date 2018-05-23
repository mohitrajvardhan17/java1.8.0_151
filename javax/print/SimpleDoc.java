package javax.print;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import sun.reflect.misc.ReflectUtil;

public final class SimpleDoc
  implements Doc
{
  private DocFlavor flavor;
  private DocAttributeSet attributes;
  private Object printData;
  private Reader reader;
  private InputStream inStream;
  
  public SimpleDoc(Object paramObject, DocFlavor paramDocFlavor, DocAttributeSet paramDocAttributeSet)
  {
    if ((paramDocFlavor == null) || (paramObject == null)) {
      throw new IllegalArgumentException("null argument(s)");
    }
    Class localClass = null;
    try
    {
      String str = paramDocFlavor.getRepresentationClassName();
      ReflectUtil.checkPackageAccess(str);
      localClass = Class.forName(str, false, Thread.currentThread().getContextClassLoader());
    }
    catch (Throwable localThrowable)
    {
      throw new IllegalArgumentException("unknown representation class");
    }
    if (!localClass.isInstance(paramObject)) {
      throw new IllegalArgumentException("data is not of declared type");
    }
    flavor = paramDocFlavor;
    if (paramDocAttributeSet != null) {
      attributes = AttributeSetUtilities.unmodifiableView(paramDocAttributeSet);
    }
    printData = paramObject;
  }
  
  public DocFlavor getDocFlavor()
  {
    return flavor;
  }
  
  public DocAttributeSet getAttributes()
  {
    return attributes;
  }
  
  public Object getPrintData()
    throws IOException
  {
    return printData;
  }
  
  public Reader getReaderForText()
    throws IOException
  {
    if ((printData instanceof Reader)) {
      return (Reader)printData;
    }
    synchronized (this)
    {
      if (reader != null) {
        return reader;
      }
      if ((printData instanceof char[])) {
        reader = new CharArrayReader((char[])printData);
      } else if ((printData instanceof String)) {
        reader = new StringReader((String)printData);
      }
    }
    return reader;
  }
  
  public InputStream getStreamForBytes()
    throws IOException
  {
    if ((printData instanceof InputStream)) {
      return (InputStream)printData;
    }
    synchronized (this)
    {
      if (inStream != null) {
        return inStream;
      }
      if ((printData instanceof byte[])) {
        inStream = new ByteArrayInputStream((byte[])printData);
      }
    }
    return inStream;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\SimpleDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */