package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathVariableResolver;

public class JAXPVariableStack
  extends VariableStack
{
  private final XPathVariableResolver resolver;
  
  public JAXPVariableStack(XPathVariableResolver paramXPathVariableResolver)
  {
    resolver = paramXPathVariableResolver;
  }
  
  public XObject getVariableOrParam(XPathContext paramXPathContext, com.sun.org.apache.xml.internal.utils.QName paramQName)
    throws TransformerException, IllegalArgumentException
  {
    if (paramQName == null)
    {
      localObject1 = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Variable qname" });
      throw new IllegalArgumentException((String)localObject1);
    }
    Object localObject1 = new javax.xml.namespace.QName(paramQName.getNamespace(), paramQName.getLocalPart());
    Object localObject2 = resolver.resolveVariable((javax.xml.namespace.QName)localObject1);
    if (localObject2 == null)
    {
      String str = XSLMessages.createXPATHMessage("ER_RESOLVE_VARIABLE_RETURNS_NULL", new Object[] { ((javax.xml.namespace.QName)localObject1).toString() });
      throw new TransformerException(str);
    }
    return XObject.create(localObject2, paramXPathContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPVariableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */