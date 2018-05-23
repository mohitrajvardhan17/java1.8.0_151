package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract interface FastInfosetWriter
  extends ContentHandler, LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler, RestrictedAlphabetContentHandler, ExtendedContentHandler, FastInfosetSerializer
{}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\FastInfosetWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */