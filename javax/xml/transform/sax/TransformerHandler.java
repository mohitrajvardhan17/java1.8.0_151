package javax.xml.transform.sax;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract interface TransformerHandler
  extends ContentHandler, LexicalHandler, DTDHandler
{
  public abstract void setResult(Result paramResult)
    throws IllegalArgumentException;
  
  public abstract void setSystemId(String paramString);
  
  public abstract String getSystemId();
  
  public abstract Transformer getTransformer();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\sax\TransformerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */