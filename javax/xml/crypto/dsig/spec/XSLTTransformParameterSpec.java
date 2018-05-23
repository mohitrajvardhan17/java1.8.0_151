package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.XMLStructure;

public final class XSLTTransformParameterSpec
  implements TransformParameterSpec
{
  private XMLStructure stylesheet;
  
  public XSLTTransformParameterSpec(XMLStructure paramXMLStructure)
  {
    if (paramXMLStructure == null) {
      throw new NullPointerException();
    }
    stylesheet = paramXMLStructure;
  }
  
  public XMLStructure getStylesheet()
  {
    return stylesheet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\XSLTTransformParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */