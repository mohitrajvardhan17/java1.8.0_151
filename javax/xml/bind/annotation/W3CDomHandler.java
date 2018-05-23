package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class W3CDomHandler
  implements DomHandler<Element, DOMResult>
{
  private DocumentBuilder builder;
  
  public W3CDomHandler()
  {
    builder = null;
  }
  
  public W3CDomHandler(DocumentBuilder paramDocumentBuilder)
  {
    if (paramDocumentBuilder == null) {
      throw new IllegalArgumentException();
    }
    builder = paramDocumentBuilder;
  }
  
  public DocumentBuilder getBuilder()
  {
    return builder;
  }
  
  public void setBuilder(DocumentBuilder paramDocumentBuilder)
  {
    builder = paramDocumentBuilder;
  }
  
  public DOMResult createUnmarshaller(ValidationEventHandler paramValidationEventHandler)
  {
    if (builder == null) {
      return new DOMResult();
    }
    return new DOMResult(builder.newDocument());
  }
  
  public Element getElement(DOMResult paramDOMResult)
  {
    Node localNode = paramDOMResult.getNode();
    if ((localNode instanceof Document)) {
      return ((Document)localNode).getDocumentElement();
    }
    if ((localNode instanceof Element)) {
      return (Element)localNode;
    }
    if ((localNode instanceof DocumentFragment)) {
      return (Element)localNode.getChildNodes().item(0);
    }
    throw new IllegalStateException(localNode.toString());
  }
  
  public Source marshal(Element paramElement, ValidationEventHandler paramValidationEventHandler)
  {
    return new DOMSource(paramElement);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\annotation\W3CDomHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */