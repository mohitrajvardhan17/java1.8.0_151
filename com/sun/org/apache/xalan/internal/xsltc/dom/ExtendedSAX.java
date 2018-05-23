package com.sun.org.apache.xalan.internal.xsltc.dom;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract interface ExtendedSAX
  extends ContentHandler, LexicalHandler, DTDHandler, DeclHandler
{}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\ExtendedSAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */