package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2DOM;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXEventWriter;
import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXStreamWriter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import com.sun.org.apache.xml.internal.serializer.ToTextSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToTextStream;
import com.sun.org.apache.xml.internal.serializer.ToUnknownStream;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class TransletOutputHandlerFactory
{
  public static final int STREAM = 0;
  public static final int SAX = 1;
  public static final int DOM = 2;
  public static final int STAX = 3;
  private String _encoding = "utf-8";
  private String _method = null;
  private int _outputType = 0;
  private OutputStream _ostream = System.out;
  private Writer _writer = null;
  private Node _node = null;
  private Node _nextSibling = null;
  private XMLEventWriter _xmlStAXEventWriter = null;
  private XMLStreamWriter _xmlStAXStreamWriter = null;
  private int _indentNumber = -1;
  private ContentHandler _handler = null;
  private LexicalHandler _lexHandler = null;
  private boolean _useServicesMechanism;
  
  public static TransletOutputHandlerFactory newInstance()
  {
    return new TransletOutputHandlerFactory(true);
  }
  
  public static TransletOutputHandlerFactory newInstance(boolean paramBoolean)
  {
    return new TransletOutputHandlerFactory(paramBoolean);
  }
  
  public TransletOutputHandlerFactory(boolean paramBoolean)
  {
    _useServicesMechanism = paramBoolean;
  }
  
  public void setOutputType(int paramInt)
  {
    _outputType = paramInt;
  }
  
  public void setEncoding(String paramString)
  {
    if (paramString != null) {
      _encoding = paramString;
    }
  }
  
  public void setOutputMethod(String paramString)
  {
    _method = paramString;
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    _ostream = paramOutputStream;
  }
  
  public void setWriter(Writer paramWriter)
  {
    _writer = paramWriter;
  }
  
  public void setHandler(ContentHandler paramContentHandler)
  {
    _handler = paramContentHandler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    _lexHandler = paramLexicalHandler;
  }
  
  public void setNode(Node paramNode)
  {
    _node = paramNode;
  }
  
  public Node getNode()
  {
    return (_handler instanceof SAX2DOM) ? ((SAX2DOM)_handler).getDOM() : null;
  }
  
  public void setNextSibling(Node paramNode)
  {
    _nextSibling = paramNode;
  }
  
  public XMLEventWriter getXMLEventWriter()
  {
    return (_handler instanceof SAX2StAXEventWriter) ? ((SAX2StAXEventWriter)_handler).getEventWriter() : null;
  }
  
  public void setXMLEventWriter(XMLEventWriter paramXMLEventWriter)
  {
    _xmlStAXEventWriter = paramXMLEventWriter;
  }
  
  public XMLStreamWriter getXMLStreamWriter()
  {
    return (_handler instanceof SAX2StAXStreamWriter) ? ((SAX2StAXStreamWriter)_handler).getStreamWriter() : null;
  }
  
  public void setXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    _xmlStAXStreamWriter = paramXMLStreamWriter;
  }
  
  public void setIndentNumber(int paramInt)
  {
    _indentNumber = paramInt;
  }
  
  public SerializationHandler getSerializationHandler()
    throws IOException, ParserConfigurationException
  {
    Object localObject = null;
    switch (_outputType)
    {
    case 0: 
      if (_method == null) {
        localObject = new ToUnknownStream();
      } else if (_method.equalsIgnoreCase("xml")) {
        localObject = new ToXMLStream();
      } else if (_method.equalsIgnoreCase("html")) {
        localObject = new ToHTMLStream();
      } else if (_method.equalsIgnoreCase("text")) {
        localObject = new ToTextStream();
      }
      if ((localObject != null) && (_indentNumber >= 0)) {
        ((SerializationHandler)localObject).setIndentAmount(_indentNumber);
      }
      ((SerializationHandler)localObject).setEncoding(_encoding);
      if (_writer != null) {
        ((SerializationHandler)localObject).setWriter(_writer);
      } else {
        ((SerializationHandler)localObject).setOutputStream(_ostream);
      }
      return (SerializationHandler)localObject;
    case 2: 
      _handler = (_node != null ? new SAX2DOM(_node, _nextSibling, _useServicesMechanism) : new SAX2DOM(_useServicesMechanism));
      _lexHandler = ((LexicalHandler)_handler);
    case 3: 
      if (_xmlStAXEventWriter != null) {
        _handler = new SAX2StAXEventWriter(_xmlStAXEventWriter);
      } else if (_xmlStAXStreamWriter != null) {
        _handler = new SAX2StAXStreamWriter(_xmlStAXStreamWriter);
      }
      _lexHandler = ((LexicalHandler)_handler);
    case 1: 
      if (_method == null) {
        _method = "xml";
      }
      if (_method.equalsIgnoreCase("xml"))
      {
        if (_lexHandler == null) {
          localObject = new ToXMLSAXHandler(_handler, _encoding);
        } else {
          localObject = new ToXMLSAXHandler(_handler, _lexHandler, _encoding);
        }
      }
      else if (_method.equalsIgnoreCase("html"))
      {
        if (_lexHandler == null) {
          localObject = new ToHTMLSAXHandler(_handler, _encoding);
        } else {
          localObject = new ToHTMLSAXHandler(_handler, _lexHandler, _encoding);
        }
      }
      else if (_method.equalsIgnoreCase("text")) {
        if (_lexHandler == null) {
          localObject = new ToTextSAXHandler(_handler, _encoding);
        } else {
          localObject = new ToTextSAXHandler(_handler, _lexHandler, _encoding);
        }
      }
      return (SerializationHandler)localObject;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\TransletOutputHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */