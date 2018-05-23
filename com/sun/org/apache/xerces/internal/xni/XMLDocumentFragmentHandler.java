package com.sun.org.apache.xerces.internal.xni;

public abstract interface XMLDocumentFragmentHandler
{
  public abstract void startDocumentFragment(XMLLocator paramXMLLocator, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void startCDATA(Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void endCDATA(Augmentations paramAugmentations)
    throws XNIException;
  
  public abstract void endDocumentFragment(Augmentations paramAugmentations)
    throws XNIException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLDocumentFragmentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */