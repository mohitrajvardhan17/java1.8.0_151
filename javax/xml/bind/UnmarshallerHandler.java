package javax.xml.bind;

import org.xml.sax.ContentHandler;

public abstract interface UnmarshallerHandler
  extends ContentHandler
{
  public abstract Object getResult()
    throws JAXBException, IllegalStateException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\UnmarshallerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */