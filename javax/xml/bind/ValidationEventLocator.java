package javax.xml.bind;

import java.net.URL;
import org.w3c.dom.Node;

public abstract interface ValidationEventLocator
{
  public abstract URL getURL();
  
  public abstract int getOffset();
  
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
  
  public abstract Object getObject();
  
  public abstract Node getNode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\ValidationEventLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */