package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.ArrayList;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

public class JAXPExtensionsProvider
  implements ExtensionsProvider
{
  private final XPathFunctionResolver resolver;
  private boolean extensionInvocationDisabled = false;
  
  public JAXPExtensionsProvider(XPathFunctionResolver paramXPathFunctionResolver)
  {
    resolver = paramXPathFunctionResolver;
    extensionInvocationDisabled = false;
  }
  
  public JAXPExtensionsProvider(XPathFunctionResolver paramXPathFunctionResolver, boolean paramBoolean, FeatureManager paramFeatureManager)
  {
    resolver = paramXPathFunctionResolver;
    if ((paramBoolean) && (!paramFeatureManager.isFeatureEnabled(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION))) {
      extensionInvocationDisabled = true;
    }
  }
  
  public boolean functionAvailable(String paramString1, String paramString2)
    throws TransformerException
  {
    try
    {
      if (paramString2 == null)
      {
        localObject = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
        throw new NullPointerException((String)localObject);
      }
      Object localObject = new QName(paramString1, paramString2);
      XPathFunction localXPathFunction = resolver.resolveFunction((QName)localObject, 0);
      return localXPathFunction != null;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public boolean elementAvailable(String paramString1, String paramString2)
    throws TransformerException
  {
    return false;
  }
  
  public Object extFunction(String paramString1, String paramString2, Vector paramVector, Object paramObject)
    throws TransformerException
  {
    try
    {
      if (paramString2 == null)
      {
        localObject1 = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
        throw new NullPointerException((String)localObject1);
      }
      Object localObject1 = new QName(paramString1, paramString2);
      if (extensionInvocationDisabled)
      {
        String str = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { ((QName)localObject1).toString() });
        throw new XPathFunctionException(str);
      }
      int i = paramVector.size();
      XPathFunction localXPathFunction = resolver.resolveFunction((QName)localObject1, i);
      ArrayList localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++)
      {
        Object localObject2 = paramVector.elementAt(j);
        if ((localObject2 instanceof XNodeSet))
        {
          localArrayList.add(j, ((XNodeSet)localObject2).nodelist());
        }
        else if ((localObject2 instanceof XObject))
        {
          Object localObject3 = ((XObject)localObject2).object();
          localArrayList.add(j, localObject3);
        }
        else
        {
          localArrayList.add(j, localObject2);
        }
      }
      return localXPathFunction.evaluate(localArrayList);
    }
    catch (XPathFunctionException localXPathFunctionException)
    {
      throw new WrappedRuntimeException(localXPathFunctionException);
    }
    catch (Exception localException)
    {
      throw new TransformerException(localException);
    }
  }
  
  public Object extFunction(FuncExtFunction paramFuncExtFunction, Vector paramVector)
    throws TransformerException
  {
    try
    {
      String str1 = paramFuncExtFunction.getNamespace();
      String str2 = paramFuncExtFunction.getFunctionName();
      int i = paramFuncExtFunction.getArgCount();
      QName localQName = new QName(str1, str2);
      if (extensionInvocationDisabled)
      {
        localObject1 = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { localQName.toString() });
        throw new XPathFunctionException((String)localObject1);
      }
      Object localObject1 = resolver.resolveFunction(localQName, i);
      ArrayList localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++)
      {
        Object localObject2 = paramVector.elementAt(j);
        if ((localObject2 instanceof XNodeSet))
        {
          localArrayList.add(j, ((XNodeSet)localObject2).nodelist());
        }
        else if ((localObject2 instanceof XObject))
        {
          Object localObject3 = ((XObject)localObject2).object();
          localArrayList.add(j, localObject3);
        }
        else
        {
          localArrayList.add(j, localObject2);
        }
      }
      return ((XPathFunction)localObject1).evaluate(localArrayList);
    }
    catch (XPathFunctionException localXPathFunctionException)
    {
      throw new WrappedRuntimeException(localXPathFunctionException);
    }
    catch (Exception localException)
    {
      throw new TransformerException(localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPExtensionsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */