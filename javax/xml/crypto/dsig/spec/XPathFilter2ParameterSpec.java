package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XPathFilter2ParameterSpec
  implements TransformParameterSpec
{
  private final List<XPathType> xPathList;
  
  public XPathFilter2ParameterSpec(List paramList)
  {
    if (paramList == null) {
      throw new NullPointerException("xPathList cannot be null");
    }
    ArrayList localArrayList1 = new ArrayList(paramList);
    if (localArrayList1.isEmpty()) {
      throw new IllegalArgumentException("xPathList cannot be empty");
    }
    int i = localArrayList1.size();
    for (int j = 0; j < i; j++) {
      if (!(localArrayList1.get(j) instanceof XPathType)) {
        throw new ClassCastException("xPathList[" + j + "] is not a valid type");
      }
    }
    ArrayList localArrayList2 = localArrayList1;
    xPathList = Collections.unmodifiableList(localArrayList2);
  }
  
  public List getXPathList()
  {
    return xPathList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\XPathFilter2ParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */