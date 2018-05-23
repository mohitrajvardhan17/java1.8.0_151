package javax.xml.soap;

import java.util.Locale;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;

public abstract interface SOAPBody
  extends SOAPElement
{
  public abstract SOAPFault addFault()
    throws SOAPException;
  
  public abstract SOAPFault addFault(Name paramName, String paramString, Locale paramLocale)
    throws SOAPException;
  
  public abstract SOAPFault addFault(QName paramQName, String paramString, Locale paramLocale)
    throws SOAPException;
  
  public abstract SOAPFault addFault(Name paramName, String paramString)
    throws SOAPException;
  
  public abstract SOAPFault addFault(QName paramQName, String paramString)
    throws SOAPException;
  
  public abstract boolean hasFault();
  
  public abstract SOAPFault getFault();
  
  public abstract SOAPBodyElement addBodyElement(Name paramName)
    throws SOAPException;
  
  public abstract SOAPBodyElement addBodyElement(QName paramQName)
    throws SOAPException;
  
  public abstract SOAPBodyElement addDocument(Document paramDocument)
    throws SOAPException;
  
  public abstract Document extractContentAsDocument()
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */