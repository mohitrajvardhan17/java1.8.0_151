package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;

public final class XSLoaderImpl
  implements XSLoader, DOMConfiguration
{
  private final XSGrammarPool fGrammarPool = new XSGrammarMerger();
  private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader();
  
  public XSLoaderImpl()
  {
    fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", fGrammarPool);
  }
  
  public DOMConfiguration getConfig()
  {
    return this;
  }
  
  public XSModel loadURIList(StringList paramStringList)
  {
    int i = paramStringList.getLength();
    try
    {
      fGrammarPool.clear();
      for (int j = 0; j < i; j++) {
        fSchemaLoader.loadGrammar(new XMLInputSource(null, paramStringList.item(j), null));
      }
      return fGrammarPool.toXSModel();
    }
    catch (Exception localException)
    {
      fSchemaLoader.reportDOMFatalError(localException);
    }
    return null;
  }
  
  public XSModel loadInputList(LSInputList paramLSInputList)
  {
    int i = paramLSInputList.getLength();
    try
    {
      fGrammarPool.clear();
      for (int j = 0; j < i; j++) {
        fSchemaLoader.loadGrammar(fSchemaLoader.dom2xmlInputSource(paramLSInputList.item(j)));
      }
      return fGrammarPool.toXSModel();
    }
    catch (Exception localException)
    {
      fSchemaLoader.reportDOMFatalError(localException);
    }
    return null;
  }
  
  public XSModel loadURI(String paramString)
  {
    try
    {
      fGrammarPool.clear();
      return ((XSGrammar)fSchemaLoader.loadGrammar(new XMLInputSource(null, paramString, null))).toXSModel();
    }
    catch (Exception localException)
    {
      fSchemaLoader.reportDOMFatalError(localException);
    }
    return null;
  }
  
  public XSModel load(LSInput paramLSInput)
  {
    try
    {
      fGrammarPool.clear();
      return ((XSGrammar)fSchemaLoader.loadGrammar(fSchemaLoader.dom2xmlInputSource(paramLSInput))).toXSModel();
    }
    catch (Exception localException)
    {
      fSchemaLoader.reportDOMFatalError(localException);
    }
    return null;
  }
  
  public void setParameter(String paramString, Object paramObject)
    throws DOMException
  {
    fSchemaLoader.setParameter(paramString, paramObject);
  }
  
  public Object getParameter(String paramString)
    throws DOMException
  {
    return fSchemaLoader.getParameter(paramString);
  }
  
  public boolean canSetParameter(String paramString, Object paramObject)
  {
    return fSchemaLoader.canSetParameter(paramString, paramObject);
  }
  
  public DOMStringList getParameterNames()
  {
    return fSchemaLoader.getParameterNames();
  }
  
  private static final class XSGrammarMerger
    extends XSGrammarPool
  {
    public XSGrammarMerger() {}
    
    public void putGrammar(Grammar paramGrammar)
    {
      SchemaGrammar localSchemaGrammar1 = toSchemaGrammar(super.getGrammar(paramGrammar.getGrammarDescription()));
      if (localSchemaGrammar1 != null)
      {
        SchemaGrammar localSchemaGrammar2 = toSchemaGrammar(paramGrammar);
        if (localSchemaGrammar2 != null) {
          mergeSchemaGrammars(localSchemaGrammar1, localSchemaGrammar2);
        }
      }
      else
      {
        super.putGrammar(paramGrammar);
      }
    }
    
    private SchemaGrammar toSchemaGrammar(Grammar paramGrammar)
    {
      return (paramGrammar instanceof SchemaGrammar) ? (SchemaGrammar)paramGrammar : null;
    }
    
    private void mergeSchemaGrammars(SchemaGrammar paramSchemaGrammar1, SchemaGrammar paramSchemaGrammar2)
    {
      XSNamedMap localXSNamedMap = paramSchemaGrammar2.getComponents((short)2);
      int i = localXSNamedMap.getLength();
      Object localObject;
      for (int j = 0; j < i; j++)
      {
        localObject = (XSElementDecl)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalElementDecl(((XSElementDecl)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalElementDecl((XSElementDecl)localObject);
        }
      }
      localXSNamedMap = paramSchemaGrammar2.getComponents((short)1);
      i = localXSNamedMap.getLength();
      for (j = 0; j < i; j++)
      {
        localObject = (XSAttributeDecl)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalAttributeDecl(((XSAttributeDecl)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalAttributeDecl((XSAttributeDecl)localObject);
        }
      }
      localXSNamedMap = paramSchemaGrammar2.getComponents((short)3);
      i = localXSNamedMap.getLength();
      for (j = 0; j < i; j++)
      {
        localObject = (XSTypeDefinition)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalTypeDecl(((XSTypeDefinition)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalTypeDecl((XSTypeDefinition)localObject);
        }
      }
      localXSNamedMap = paramSchemaGrammar2.getComponents((short)5);
      i = localXSNamedMap.getLength();
      for (j = 0; j < i; j++)
      {
        localObject = (XSAttributeGroupDecl)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalAttributeGroupDecl(((XSAttributeGroupDecl)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)localObject);
        }
      }
      localXSNamedMap = paramSchemaGrammar2.getComponents((short)7);
      i = localXSNamedMap.getLength();
      for (j = 0; j < i; j++)
      {
        localObject = (XSGroupDecl)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalGroupDecl(((XSGroupDecl)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalGroupDecl((XSGroupDecl)localObject);
        }
      }
      localXSNamedMap = paramSchemaGrammar2.getComponents((short)11);
      i = localXSNamedMap.getLength();
      for (j = 0; j < i; j++)
      {
        localObject = (XSNotationDecl)localXSNamedMap.item(j);
        if (paramSchemaGrammar1.getGlobalNotationDecl(((XSNotationDecl)localObject).getName()) == null) {
          paramSchemaGrammar1.addGlobalNotationDecl((XSNotationDecl)localObject);
        }
      }
      XSObjectList localXSObjectList = paramSchemaGrammar2.getAnnotations();
      i = localXSObjectList.getLength();
      for (int k = 0; k < i; k++) {
        paramSchemaGrammar1.addAnnotation((XSAnnotationImpl)localXSObjectList.item(k));
      }
    }
    
    public boolean containsGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      return false;
    }
    
    public Grammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      return null;
    }
    
    public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
    {
      return null;
    }
    
    public Grammar[] retrieveInitialGrammarSet(String paramString)
    {
      return new Grammar[0];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSLoaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */