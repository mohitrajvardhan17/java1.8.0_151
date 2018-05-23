package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Locale;

public abstract interface XMLGrammarLoader
{
  public abstract String[] getRecognizedFeatures();
  
  public abstract boolean getFeature(String paramString)
    throws XMLConfigurationException;
  
  public abstract void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException;
  
  public abstract String[] getRecognizedProperties();
  
  public abstract Object getProperty(String paramString)
    throws XMLConfigurationException;
  
  public abstract void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException;
  
  public abstract void setLocale(Locale paramLocale);
  
  public abstract Locale getLocale();
  
  public abstract void setErrorHandler(XMLErrorHandler paramXMLErrorHandler);
  
  public abstract XMLErrorHandler getErrorHandler();
  
  public abstract void setEntityResolver(XMLEntityResolver paramXMLEntityResolver);
  
  public abstract XMLEntityResolver getEntityResolver();
  
  public abstract Grammar loadGrammar(XMLInputSource paramXMLInputSource)
    throws IOException, XNIException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */