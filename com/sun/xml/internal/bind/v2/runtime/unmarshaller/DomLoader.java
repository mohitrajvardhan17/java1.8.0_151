package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;

public class DomLoader<ResultT extends Result>
  extends Loader
{
  private final DomHandler<?, ResultT> dom;
  
  public DomLoader(DomHandler<?, ResultT> paramDomHandler)
  {
    super(true);
    dom = paramDomHandler;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    if (paramState.getTarget() == null) {
      paramState.setTarget(new State(localUnmarshallingContext));
    }
    State localState = (State)paramState.getTarget();
    try
    {
      localState.declarePrefixes(localUnmarshallingContext, localUnmarshallingContext.getNewlyDeclaredPrefixes());
      handler.startElement(uri, local, paramTagName.getQname(), atts);
    }
    catch (SAXException localSAXException)
    {
      localUnmarshallingContext.handleError(localSAXException);
      throw localSAXException;
    }
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    paramState.setLoader(this);
    State localState = (State)paramState.getPrev().getTarget();
    depth += 1;
    paramState.setTarget(localState);
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    if (paramCharSequence.length() == 0) {
      return;
    }
    try
    {
      State localState = (State)paramState.getTarget();
      handler.characters(paramCharSequence.toString().toCharArray(), 0, paramCharSequence.length());
    }
    catch (SAXException localSAXException)
    {
      paramState.getContext().handleError(localSAXException);
      throw localSAXException;
    }
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    State localState = (State)paramState.getTarget();
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    try
    {
      handler.endElement(uri, local, paramTagName.getQname());
      localState.undeclarePrefixes(localUnmarshallingContext.getNewlyDeclaredPrefixes());
    }
    catch (SAXException localSAXException1)
    {
      localUnmarshallingContext.handleError(localSAXException1);
      throw localSAXException1;
    }
    if (--depth == 0)
    {
      try
      {
        localState.undeclarePrefixes(localUnmarshallingContext.getAllDeclaredPrefixes());
        handler.endDocument();
      }
      catch (SAXException localSAXException2)
      {
        localUnmarshallingContext.handleError(localSAXException2);
        throw localSAXException2;
      }
      paramState.setTarget(localState.getElement());
    }
  }
  
  private final class State
  {
    private TransformerHandler handler = null;
    private final ResultT result;
    int depth = 1;
    
    public State(UnmarshallingContext paramUnmarshallingContext)
      throws SAXException
    {
      handler = JAXBContextImpl.createTransformerHandler(getJAXBContextdisableSecurityProcessing);
      result = dom.createUnmarshaller(paramUnmarshallingContext);
      handler.setResult(result);
      try
      {
        handler.setDocumentLocator(paramUnmarshallingContext.getLocator());
        handler.startDocument();
        declarePrefixes(paramUnmarshallingContext, paramUnmarshallingContext.getAllDeclaredPrefixes());
      }
      catch (SAXException localSAXException)
      {
        paramUnmarshallingContext.handleError(localSAXException);
        throw localSAXException;
      }
    }
    
    public Object getElement()
    {
      return dom.getElement(result);
    }
    
    private void declarePrefixes(UnmarshallingContext paramUnmarshallingContext, String[] paramArrayOfString)
      throws SAXException
    {
      for (int i = paramArrayOfString.length - 1; i >= 0; i--)
      {
        String str = paramUnmarshallingContext.getNamespaceURI(paramArrayOfString[i]);
        if (str == null) {
          throw new IllegalStateException("prefix '" + paramArrayOfString[i] + "' isn't bound");
        }
        handler.startPrefixMapping(paramArrayOfString[i], str);
      }
    }
    
    private void undeclarePrefixes(String[] paramArrayOfString)
      throws SAXException
    {
      for (int i = paramArrayOfString.length - 1; i >= 0; i--) {
        handler.endPrefixMapping(paramArrayOfString[i]);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\DomLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */