package sun.print;

import java.awt.print.Pageable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;

public class PageableDoc
  implements Doc
{
  private Pageable pageable;
  
  public PageableDoc(Pageable paramPageable)
  {
    pageable = paramPageable;
  }
  
  public DocFlavor getDocFlavor()
  {
    return DocFlavor.SERVICE_FORMATTED.PAGEABLE;
  }
  
  public DocAttributeSet getAttributes()
  {
    return new HashDocAttributeSet();
  }
  
  public Object getPrintData()
    throws IOException
  {
    return pageable;
  }
  
  public Reader getReaderForText()
    throws UnsupportedEncodingException, IOException
  {
    return null;
  }
  
  public InputStream getStreamForBytes()
    throws IOException
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PageableDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */