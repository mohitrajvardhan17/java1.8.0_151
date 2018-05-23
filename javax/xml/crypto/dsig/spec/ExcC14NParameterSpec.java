package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExcC14NParameterSpec
  implements C14NMethodParameterSpec
{
  private List<String> preList;
  public static final String DEFAULT = "#default";
  
  public ExcC14NParameterSpec()
  {
    preList = Collections.emptyList();
  }
  
  public ExcC14NParameterSpec(List paramList)
  {
    if (paramList == null) {
      throw new NullPointerException("prefixList cannot be null");
    }
    ArrayList localArrayList1 = new ArrayList(paramList);
    int i = 0;
    int j = localArrayList1.size();
    while (i < j)
    {
      if (!(localArrayList1.get(i) instanceof String)) {
        throw new ClassCastException("not a String");
      }
      i++;
    }
    ArrayList localArrayList2 = localArrayList1;
    preList = Collections.unmodifiableList(localArrayList2);
  }
  
  public List getPrefixList()
  {
    return preList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\ExcC14NParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */