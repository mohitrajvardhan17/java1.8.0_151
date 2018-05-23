package javax.xml.xpath;

import java.util.List;

public abstract interface XPathFunction
{
  public abstract Object evaluate(List paramList)
    throws XPathFunctionException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\xpath\XPathFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */