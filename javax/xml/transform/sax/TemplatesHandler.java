package javax.xml.transform.sax;

import javax.xml.transform.Templates;
import org.xml.sax.ContentHandler;

public abstract interface TemplatesHandler
  extends ContentHandler
{
  public abstract Templates getTemplates();
  
  public abstract void setSystemId(String paramString);
  
  public abstract String getSystemId();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\sax\TemplatesHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */