package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetParser;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public abstract interface FastInfosetReader
  extends XMLReader, FastInfosetParser
{
  public static final String ENCODING_ALGORITHM_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler";
  public static final String PRIMITIVE_TYPE_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler";
  
  public abstract void parse(InputStream paramInputStream)
    throws IOException, FastInfosetException, SAXException;
  
  public abstract void setLexicalHandler(LexicalHandler paramLexicalHandler);
  
  public abstract LexicalHandler getLexicalHandler();
  
  public abstract void setDeclHandler(DeclHandler paramDeclHandler);
  
  public abstract DeclHandler getDeclHandler();
  
  public abstract void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler paramEncodingAlgorithmContentHandler);
  
  public abstract EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler();
  
  public abstract void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler paramPrimitiveTypeContentHandler);
  
  public abstract PrimitiveTypeContentHandler getPrimitiveTypeContentHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\FastInfosetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */