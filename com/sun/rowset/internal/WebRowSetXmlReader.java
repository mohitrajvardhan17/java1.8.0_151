package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class WebRowSetXmlReader
  implements XmlReader, Serializable
{
  private JdbcRowSetResourceBundle resBundle;
  static final long serialVersionUID = -9127058392819008014L;
  
  public WebRowSetXmlReader()
  {
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public void readXML(WebRowSet paramWebRowSet, Reader paramReader)
    throws SQLException
  {
    try
    {
      InputSource localInputSource = new InputSource(paramReader);
      localObject = new XmlErrorHandler();
      XmlReaderContentHandler localXmlReaderContentHandler = new XmlReaderContentHandler(paramWebRowSet);
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      localSAXParserFactory.setNamespaceAware(true);
      localSAXParserFactory.setValidating(true);
      SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
      localSAXParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
      XMLReader localXMLReader = localSAXParser.getXMLReader();
      localXMLReader.setEntityResolver(new XmlResolver());
      localXMLReader.setContentHandler(localXmlReaderContentHandler);
      localXMLReader.setErrorHandler((ErrorHandler)localObject);
      localXMLReader.parse(localInputSource);
    }
    catch (SAXParseException localSAXParseException)
    {
      System.out.println(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[] { localSAXParseException.getMessage(), Integer.valueOf(localSAXParseException.getLineNumber()), localSAXParseException.getSystemId() }));
      localSAXParseException.printStackTrace();
      throw new SQLException(localSAXParseException.getMessage());
    }
    catch (SAXException localSAXException)
    {
      Object localObject = localSAXException;
      if (localSAXException.getException() != null) {
        localObject = localSAXException.getException();
      }
      ((Exception)localObject).printStackTrace();
      throw new SQLException(((Exception)localObject).getMessage());
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SQLException(resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
    }
    catch (Throwable localThrowable)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.readxml").toString(), new Object[] { localThrowable.getMessage() }));
    }
  }
  
  public void readXML(WebRowSet paramWebRowSet, InputStream paramInputStream)
    throws SQLException
  {
    try
    {
      InputSource localInputSource = new InputSource(paramInputStream);
      localObject = new XmlErrorHandler();
      XmlReaderContentHandler localXmlReaderContentHandler = new XmlReaderContentHandler(paramWebRowSet);
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      localSAXParserFactory.setNamespaceAware(true);
      localSAXParserFactory.setValidating(true);
      SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
      localSAXParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
      XMLReader localXMLReader = localSAXParser.getXMLReader();
      localXMLReader.setEntityResolver(new XmlResolver());
      localXMLReader.setContentHandler(localXmlReaderContentHandler);
      localXMLReader.setErrorHandler((ErrorHandler)localObject);
      localXMLReader.parse(localInputSource);
    }
    catch (SAXParseException localSAXParseException)
    {
      System.out.println(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[] { Integer.valueOf(localSAXParseException.getLineNumber()), localSAXParseException.getSystemId() }));
      System.out.println("   " + localSAXParseException.getMessage());
      localSAXParseException.printStackTrace();
      throw new SQLException(localSAXParseException.getMessage());
    }
    catch (SAXException localSAXException)
    {
      Object localObject = localSAXException;
      if (localSAXException.getException() != null) {
        localObject = localSAXException.getException();
      }
      ((Exception)localObject).printStackTrace();
      throw new SQLException(((Exception)localObject).getMessage());
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SQLException(resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
    }
    catch (Throwable localThrowable)
    {
      throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlreader.readxml").toString(), new Object[] { localThrowable.getMessage() }));
    }
  }
  
  public void readData(RowSetInternal paramRowSetInternal) {}
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\WebRowSetXmlReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */