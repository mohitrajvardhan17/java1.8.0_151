package javax.xml.soap;

public abstract interface Node
  extends org.w3c.dom.Node
{
  public abstract String getValue();
  
  public abstract void setValue(String paramString);
  
  public abstract void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException;
  
  public abstract SOAPElement getParentElement();
  
  public abstract void detachNode();
  
  public abstract void recycleNode();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */