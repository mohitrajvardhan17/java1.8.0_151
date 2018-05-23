package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.XMLFilter;

public abstract class SAXTransformerFactory
  extends TransformerFactory
{
  public static final String FEATURE = "http://javax.xml.transform.sax.SAXTransformerFactory/feature";
  public static final String FEATURE_XMLFILTER = "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter";
  
  protected SAXTransformerFactory() {}
  
  public abstract TransformerHandler newTransformerHandler(Source paramSource)
    throws TransformerConfigurationException;
  
  public abstract TransformerHandler newTransformerHandler(Templates paramTemplates)
    throws TransformerConfigurationException;
  
  public abstract TransformerHandler newTransformerHandler()
    throws TransformerConfigurationException;
  
  public abstract TemplatesHandler newTemplatesHandler()
    throws TransformerConfigurationException;
  
  public abstract XMLFilter newXMLFilter(Source paramSource)
    throws TransformerConfigurationException;
  
  public abstract XMLFilter newXMLFilter(Templates paramTemplates)
    throws TransformerConfigurationException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\sax\SAXTransformerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */