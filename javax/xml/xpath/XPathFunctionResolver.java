package javax.xml.xpath;

import javax.xml.namespace.QName;

public abstract interface XPathFunctionResolver
{
  public abstract XPathFunction resolveFunction(QName paramQName, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\XPathFunctionResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */