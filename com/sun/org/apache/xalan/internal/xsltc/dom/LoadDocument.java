package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.FileNotFoundException;
import javax.xml.transform.stream.StreamSource;

public final class LoadDocument
{
  private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
  
  public LoadDocument() {}
  
  public static DTMAxisIterator documentF(Object paramObject, DTMAxisIterator paramDTMAxisIterator, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM)
    throws TransletException
  {
    String str1 = null;
    int i = paramDTMAxisIterator.next();
    if (i == -1) {
      return EmptyIterator.getInstance();
    }
    str1 = paramDOM.getDocumentURI(i);
    if (!SystemIDResolver.isAbsoluteURI(str1)) {
      str1 = SystemIDResolver.getAbsoluteURIFromRelative(str1);
    }
    try
    {
      if ((paramObject instanceof String))
      {
        if (((String)paramObject).length() == 0) {
          return document(paramString, "", paramAbstractTranslet, paramDOM);
        }
        return document((String)paramObject, str1, paramAbstractTranslet, paramDOM);
      }
      if ((paramObject instanceof DTMAxisIterator)) {
        return document((DTMAxisIterator)paramObject, str1, paramAbstractTranslet, paramDOM);
      }
      String str2 = "document(" + paramObject.toString() + ")";
      throw new IllegalArgumentException(str2);
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  public static DTMAxisIterator documentF(Object paramObject, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM)
    throws TransletException
  {
    try
    {
      if ((paramObject instanceof String))
      {
        if (paramString == null) {
          paramString = "";
        }
        str1 = paramString;
        if (!SystemIDResolver.isAbsoluteURI(paramString)) {
          str1 = SystemIDResolver.getAbsoluteURIFromRelative(paramString);
        }
        String str2 = (String)paramObject;
        if (str2.length() == 0)
        {
          str2 = "";
          TemplatesImpl localTemplatesImpl = (TemplatesImpl)paramAbstractTranslet.getTemplates();
          DOM localDOM = null;
          if (localTemplatesImpl != null) {
            localDOM = localTemplatesImpl.getStylesheetDOM();
          }
          if (localDOM != null) {
            return document(localDOM, paramAbstractTranslet, paramDOM);
          }
          return document(str2, str1, paramAbstractTranslet, paramDOM, true);
        }
        return document(str2, str1, paramAbstractTranslet, paramDOM);
      }
      if ((paramObject instanceof DTMAxisIterator)) {
        return document((DTMAxisIterator)paramObject, null, paramAbstractTranslet, paramDOM);
      }
      String str1 = "document(" + paramObject.toString() + ")";
      throw new IllegalArgumentException(str1);
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  private static DTMAxisIterator document(String paramString1, String paramString2, AbstractTranslet paramAbstractTranslet, DOM paramDOM)
    throws Exception
  {
    return document(paramString1, paramString2, paramAbstractTranslet, paramDOM, false);
  }
  
  private static DTMAxisIterator document(String paramString1, String paramString2, AbstractTranslet paramAbstractTranslet, DOM paramDOM, boolean paramBoolean)
    throws Exception
  {
    try
    {
      String str = paramString1;
      MultiDOM localMultiDOM = (MultiDOM)paramDOM;
      if ((paramString2 != null) && (!paramString2.equals(""))) {
        paramString1 = SystemIDResolver.getAbsoluteURI(paramString1, paramString2);
      }
      if ((paramString1 == null) || (paramString1.equals(""))) {
        return EmptyIterator.getInstance();
      }
      int i = localMultiDOM.getDocumentMask(paramString1);
      if (i != -1)
      {
        localObject1 = ((DOMAdapter)localMultiDOM.getDOMAdapter(paramString1)).getDOMImpl();
        if ((localObject1 instanceof DOMEnhancedForDTM)) {
          return new SingletonIterator(((DOMEnhancedForDTM)localObject1).getDocument(), true);
        }
      }
      Object localObject1 = paramAbstractTranslet.getDOMCache();
      i = localMultiDOM.nextMask();
      Object localObject2;
      if (localObject1 != null)
      {
        localObject2 = ((DOMCache)localObject1).retrieveDocument(paramString2, str, paramAbstractTranslet);
        if (localObject2 == null)
        {
          localObject3 = new FileNotFoundException(str);
          throw new TransletException((Exception)localObject3);
        }
      }
      else
      {
        localObject3 = SecuritySupport.checkAccess(paramString1, paramAbstractTranslet.getAllowedProtocols(), "all");
        if (localObject3 != null)
        {
          localObject4 = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(paramString1), localObject3);
          throw new Exception(((ErrorMsg)localObject4).toString());
        }
        Object localObject4 = (XSLTCDTMManager)localMultiDOM.getDTMManager();
        DOMEnhancedForDTM localDOMEnhancedForDTM = (DOMEnhancedForDTM)((XSLTCDTMManager)localObject4).getDTM(new StreamSource(paramString1), false, null, true, false, paramAbstractTranslet.hasIdCall(), paramBoolean);
        localObject2 = localDOMEnhancedForDTM;
        if (paramBoolean)
        {
          TemplatesImpl localTemplatesImpl = (TemplatesImpl)paramAbstractTranslet.getTemplates();
          if (localTemplatesImpl != null) {
            localTemplatesImpl.setStylesheetDOM(localDOMEnhancedForDTM);
          }
        }
        paramAbstractTranslet.prepassDocument(localDOMEnhancedForDTM);
        localDOMEnhancedForDTM.setDocumentURI(paramString1);
      }
      Object localObject3 = paramAbstractTranslet.makeDOMAdapter((DOM)localObject2);
      localMultiDOM.addDOMAdapter((DOMAdapter)localObject3);
      paramAbstractTranslet.buildKeys((DOM)localObject3, null, null, ((DOM)localObject2).getDocument());
      return new SingletonIterator(((DOM)localObject2).getDocument(), true);
    }
    catch (Exception localException)
    {
      throw localException;
    }
  }
  
  private static DTMAxisIterator document(DTMAxisIterator paramDTMAxisIterator, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM)
    throws Exception
  {
    UnionIterator localUnionIterator = new UnionIterator(paramDOM);
    int i = -1;
    while ((i = paramDTMAxisIterator.next()) != -1)
    {
      String str = paramDOM.getStringValueX(i);
      if (paramString == null)
      {
        paramString = paramDOM.getDocumentURI(i);
        if (!SystemIDResolver.isAbsoluteURI(paramString)) {
          paramString = SystemIDResolver.getAbsoluteURIFromRelative(paramString);
        }
      }
      localUnionIterator.addIterator(document(str, paramString, paramAbstractTranslet, paramDOM));
    }
    return localUnionIterator;
  }
  
  private static DTMAxisIterator document(DOM paramDOM1, AbstractTranslet paramAbstractTranslet, DOM paramDOM2)
    throws Exception
  {
    DTMManager localDTMManager = ((MultiDOM)paramDOM2).getDTMManager();
    if ((localDTMManager != null) && ((paramDOM1 instanceof DTM))) {
      ((DTM)paramDOM1).migrateTo(localDTMManager);
    }
    paramAbstractTranslet.prepassDocument(paramDOM1);
    DOMAdapter localDOMAdapter = paramAbstractTranslet.makeDOMAdapter(paramDOM1);
    ((MultiDOM)paramDOM2).addDOMAdapter(localDOMAdapter);
    paramAbstractTranslet.buildKeys(localDOMAdapter, null, null, paramDOM1.getDocument());
    return new SingletonIterator(paramDOM1.getDocument(), true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\LoadDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */