package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl
  extends CharacterDataImpl
  implements ProcessingInstruction
{
  static final long serialVersionUID = 7554435174099981510L;
  protected String target;
  
  public ProcessingInstructionImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2)
  {
    super(paramCoreDocumentImpl, paramString2);
    target = paramString1;
  }
  
  public short getNodeType()
  {
    return 7;
  }
  
  public String getNodeName()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return target;
  }
  
  public String getTarget()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return target;
  }
  
  public String getData()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return data;
  }
  
  public void setData(String paramString)
  {
    setNodeValue(paramString);
  }
  
  public String getBaseURI()
  {
    if (needsSyncData()) {
      synchronizeData();
    }
    return ownerNode.getBaseURI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\ProcessingInstructionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */