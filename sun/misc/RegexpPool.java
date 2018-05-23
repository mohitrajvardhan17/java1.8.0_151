package sun.misc;

import java.io.PrintStream;

public class RegexpPool
{
  private RegexpNode prefixMachine = new RegexpNode();
  private RegexpNode suffixMachine = new RegexpNode();
  private static final int BIG = Integer.MAX_VALUE;
  private int lastDepth = Integer.MAX_VALUE;
  
  public RegexpPool() {}
  
  public void add(String paramString, Object paramObject)
    throws REException
  {
    add(paramString, paramObject, false);
  }
  
  public void replace(String paramString, Object paramObject)
  {
    try
    {
      add(paramString, paramObject, true);
    }
    catch (Exception localException) {}
  }
  
  public Object delete(String paramString)
  {
    Object localObject = null;
    RegexpNode localRegexpNode1 = prefixMachine;
    RegexpNode localRegexpNode2 = localRegexpNode1;
    int i = paramString.length() - 1;
    int k = 1;
    if ((!paramString.startsWith("*")) || (!paramString.endsWith("*"))) {
      i++;
    }
    if (i <= 0) {
      return null;
    }
    for (int j = 0; localRegexpNode1 != null; j++)
    {
      if ((result != null) && (depth < Integer.MAX_VALUE) && ((!exact) || (j == i))) {
        localRegexpNode2 = localRegexpNode1;
      }
      if (j >= i) {
        break;
      }
      localRegexpNode1 = localRegexpNode1.find(paramString.charAt(j));
    }
    localRegexpNode1 = suffixMachine;
    j = i;
    for (;;)
    {
      j--;
      if ((j < 0) || (localRegexpNode1 == null)) {
        break;
      }
      if ((result != null) && (depth < Integer.MAX_VALUE))
      {
        k = 0;
        localRegexpNode2 = localRegexpNode1;
      }
      localRegexpNode1 = localRegexpNode1.find(paramString.charAt(j));
    }
    if (k != 0)
    {
      if (paramString.equals(re))
      {
        localObject = result;
        result = null;
      }
    }
    else if (paramString.equals(re))
    {
      localObject = result;
      result = null;
    }
    return localObject;
  }
  
  public Object match(String paramString)
  {
    return matchAfter(paramString, Integer.MAX_VALUE);
  }
  
  public Object matchNext(String paramString)
  {
    return matchAfter(paramString, lastDepth);
  }
  
  private void add(String paramString, Object paramObject, boolean paramBoolean)
    throws REException
  {
    int i = paramString.length();
    if (paramString.charAt(0) == '*') {
      for (localRegexpNode = suffixMachine; i > 1; localRegexpNode = localRegexpNode.add(paramString.charAt(--i))) {}
    }
    boolean bool = false;
    if (paramString.charAt(i - 1) == '*') {
      i--;
    } else {
      bool = true;
    }
    RegexpNode localRegexpNode = prefixMachine;
    for (int j = 0; j < i; j++) {
      localRegexpNode = localRegexpNode.add(paramString.charAt(j));
    }
    exact = bool;
    if ((result != null) && (!paramBoolean)) {
      throw new REException(paramString + " is a duplicate");
    }
    re = paramString;
    result = paramObject;
  }
  
  private Object matchAfter(String paramString, int paramInt)
  {
    RegexpNode localRegexpNode1 = prefixMachine;
    RegexpNode localRegexpNode2 = localRegexpNode1;
    int i = 0;
    int j = 0;
    int k = paramString.length();
    if (k <= 0) {
      return null;
    }
    for (int m = 0; localRegexpNode1 != null; m++)
    {
      if ((result != null) && (depth < paramInt) && ((!exact) || (m == k)))
      {
        lastDepth = depth;
        localRegexpNode2 = localRegexpNode1;
        i = m;
        j = k;
      }
      if (m >= k) {
        break;
      }
      localRegexpNode1 = localRegexpNode1.find(paramString.charAt(m));
    }
    localRegexpNode1 = suffixMachine;
    m = k;
    for (;;)
    {
      m--;
      if ((m < 0) || (localRegexpNode1 == null)) {
        break;
      }
      if ((result != null) && (depth < paramInt))
      {
        lastDepth = depth;
        localRegexpNode2 = localRegexpNode1;
        i = 0;
        j = m + 1;
      }
      localRegexpNode1 = localRegexpNode1.find(paramString.charAt(m));
    }
    Object localObject = result;
    if ((localObject != null) && ((localObject instanceof RegexpTarget))) {
      localObject = ((RegexpTarget)localObject).found(paramString.substring(i, j));
    }
    return localObject;
  }
  
  public void reset()
  {
    lastDepth = Integer.MAX_VALUE;
  }
  
  public void print(PrintStream paramPrintStream)
  {
    paramPrintStream.print("Regexp pool:\n");
    if (suffixMachine.firstchild != null)
    {
      paramPrintStream.print(" Suffix machine: ");
      suffixMachine.firstchild.print(paramPrintStream);
      paramPrintStream.print("\n");
    }
    if (prefixMachine.firstchild != null)
    {
      paramPrintStream.print(" Prefix machine: ");
      prefixMachine.firstchild.print(paramPrintStream);
      paramPrintStream.print("\n");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\RegexpPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */