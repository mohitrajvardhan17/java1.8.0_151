package com.sun.xml.internal.messaging.saaj.util;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ParserPool
{
  private final BlockingQueue queue;
  private SAXParserFactory factory;
  private int capacity;
  
  public ParserPool(int paramInt)
  {
    capacity = paramInt;
    queue = new ArrayBlockingQueue(paramInt);
    factory = new SAXParserFactoryImpl();
    factory.setNamespaceAware(true);
    for (int i = 0; i < paramInt; i++) {
      try
      {
        queue.put(factory.newSAXParser());
      }
      catch (InterruptedException localInterruptedException)
      {
        Thread.currentThread().interrupt();
        throw new RuntimeException(localInterruptedException);
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new RuntimeException(localParserConfigurationException);
      }
      catch (SAXException localSAXException)
      {
        throw new RuntimeException(localSAXException);
      }
    }
  }
  
  public SAXParser get()
    throws ParserConfigurationException, SAXException
  {
    try
    {
      return (SAXParser)queue.take();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new SAXException(localInterruptedException);
    }
  }
  
  public void put(SAXParser paramSAXParser)
  {
    queue.offer(paramSAXParser);
  }
  
  public void returnParser(SAXParser paramSAXParser)
  {
    paramSAXParser.reset();
    resetSaxParser(paramSAXParser);
    put(paramSAXParser);
  }
  
  private void resetSaxParser(SAXParser paramSAXParser)
  {
    try
    {
      SymbolTable localSymbolTable = new SymbolTable();
      paramSAXParser.setProperty("http://apache.org/xml/properties/internal/symbol-table", localSymbolTable);
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException) {}catch (SAXNotSupportedException localSAXNotSupportedException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\ParserPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */