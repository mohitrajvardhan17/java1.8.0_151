package javax.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.print.attribute.DocAttributeSet;

public abstract interface Doc
{
  public abstract DocFlavor getDocFlavor();
  
  public abstract Object getPrintData()
    throws IOException;
  
  public abstract DocAttributeSet getAttributes();
  
  public abstract Reader getReaderForText()
    throws IOException;
  
  public abstract InputStream getStreamForBytes()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\Doc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */