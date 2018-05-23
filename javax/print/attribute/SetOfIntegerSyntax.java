package javax.print.attribute;

import java.io.Serializable;
import java.util.Vector;

public abstract class SetOfIntegerSyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = 3666874174847632203L;
  private int[][] members;
  
  protected SetOfIntegerSyntax(String paramString)
  {
    members = parse(paramString);
  }
  
  private static int[][] parse(String paramString)
  {
    Vector localVector = new Vector();
    int i = paramString == null ? 0 : paramString.length();
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (j < i)
    {
      char c = paramString.charAt(j++);
      int i1;
      switch (k)
      {
      case 0: 
        if (Character.isWhitespace(c))
        {
          k = 0;
        }
        else if ((i1 = Character.digit(c, 10)) != -1)
        {
          m = i1;
          k = 1;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 1: 
        if (Character.isWhitespace(c))
        {
          k = 2;
        }
        else if ((i1 = Character.digit(c, 10)) != -1)
        {
          m = 10 * m + i1;
          k = 1;
        }
        else if ((c == '-') || (c == ':'))
        {
          k = 3;
        }
        else if (c == ',')
        {
          accumulate(localVector, m, m);
          k = 6;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 2: 
        if (Character.isWhitespace(c))
        {
          k = 2;
        }
        else if ((c == '-') || (c == ':'))
        {
          k = 3;
        }
        else if (c == ',')
        {
          accumulate(localVector, m, m);
          k = 6;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 3: 
        if (Character.isWhitespace(c))
        {
          k = 3;
        }
        else if ((i1 = Character.digit(c, 10)) != -1)
        {
          n = i1;
          k = 4;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 4: 
        if (Character.isWhitespace(c))
        {
          k = 5;
        }
        else if ((i1 = Character.digit(c, 10)) != -1)
        {
          n = 10 * n + i1;
          k = 4;
        }
        else if (c == ',')
        {
          accumulate(localVector, m, n);
          k = 6;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 5: 
        if (Character.isWhitespace(c))
        {
          k = 5;
        }
        else if (c == ',')
        {
          accumulate(localVector, m, n);
          k = 6;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      case 6: 
        if (Character.isWhitespace(c))
        {
          k = 6;
        }
        else if ((i1 = Character.digit(c, 10)) != -1)
        {
          m = i1;
          k = 1;
        }
        else
        {
          throw new IllegalArgumentException();
        }
        break;
      }
    }
    switch (k)
    {
    case 0: 
      break;
    case 1: 
    case 2: 
      accumulate(localVector, m, m);
      break;
    case 4: 
    case 5: 
      accumulate(localVector, m, n);
      break;
    case 3: 
    case 6: 
      throw new IllegalArgumentException();
    }
    return canonicalArrayForm(localVector);
  }
  
  private static void accumulate(Vector paramVector, int paramInt1, int paramInt2)
  {
    if (paramInt1 <= paramInt2)
    {
      paramVector.add(new int[] { paramInt1, paramInt2 });
      for (int i = paramVector.size() - 2; i >= 0; i--)
      {
        int[] arrayOfInt1 = (int[])paramVector.elementAt(i);
        int j = arrayOfInt1[0];
        int k = arrayOfInt1[1];
        int[] arrayOfInt2 = (int[])paramVector.elementAt(i + 1);
        int m = arrayOfInt2[0];
        int n = arrayOfInt2[1];
        if (Math.max(j, m) - Math.min(k, n) <= 1)
        {
          paramVector.setElementAt(new int[] { Math.min(j, m), Math.max(k, n) }, i);
          paramVector.remove(i + 1);
        }
        else
        {
          if (j <= m) {
            break;
          }
          paramVector.setElementAt(arrayOfInt2, i);
          paramVector.setElementAt(arrayOfInt1, i + 1);
        }
      }
    }
  }
  
  private static int[][] canonicalArrayForm(Vector paramVector)
  {
    return (int[][])paramVector.toArray(new int[paramVector.size()][]);
  }
  
  protected SetOfIntegerSyntax(int[][] paramArrayOfInt)
  {
    members = parse(paramArrayOfInt);
  }
  
  private static int[][] parse(int[][] paramArrayOfInt)
  {
    Vector localVector = new Vector();
    int i = paramArrayOfInt == null ? 0 : paramArrayOfInt.length;
    for (int j = 0; j < i; j++)
    {
      int m;
      int k;
      if (paramArrayOfInt[j].length == 1)
      {
        k = m = paramArrayOfInt[j][0];
      }
      else if (paramArrayOfInt[j].length == 2)
      {
        k = paramArrayOfInt[j][0];
        m = paramArrayOfInt[j][1];
      }
      else
      {
        throw new IllegalArgumentException();
      }
      if ((k <= m) && (k < 0)) {
        throw new IllegalArgumentException();
      }
      accumulate(localVector, k, m);
    }
    return canonicalArrayForm(localVector);
  }
  
  protected SetOfIntegerSyntax(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    members = new int[][] { { paramInt, paramInt } };
  }
  
  protected SetOfIntegerSyntax(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= paramInt2) && (paramInt1 < 0)) {
      throw new IllegalArgumentException();
    }
    members = (paramInt1 <= paramInt2 ? new int[][] { { paramInt1, paramInt2 } } : new int[0][]);
  }
  
  public int[][] getMembers()
  {
    int i = members.length;
    int[][] arrayOfInt = new int[i][];
    for (int j = 0; j < i; j++) {
      arrayOfInt[j] = { members[j][0], members[j][1] };
    }
    return arrayOfInt;
  }
  
  public boolean contains(int paramInt)
  {
    int i = members.length;
    for (int j = 0; j < i; j++)
    {
      if (paramInt < members[j][0]) {
        return false;
      }
      if (paramInt <= members[j][1]) {
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(IntegerSyntax paramIntegerSyntax)
  {
    return contains(paramIntegerSyntax.getValue());
  }
  
  public int next(int paramInt)
  {
    int i = members.length;
    for (int j = 0; j < i; j++)
    {
      if (paramInt < members[j][0]) {
        return members[j][0];
      }
      if (paramInt < members[j][1]) {
        return paramInt + 1;
      }
    }
    return -1;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof SetOfIntegerSyntax)))
    {
      int[][] arrayOfInt1 = members;
      int[][] arrayOfInt2 = members;
      int i = arrayOfInt1.length;
      int j = arrayOfInt2.length;
      if (i == j)
      {
        for (int k = 0; k < i; k++) {
          if ((arrayOfInt1[k][0] != arrayOfInt2[k][0]) || (arrayOfInt1[k][1] != arrayOfInt2[k][1])) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = members.length;
    for (int k = 0; k < j; k++) {
      i += members[k][0] + members[k][1];
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = members.length;
    for (int j = 0; j < i; j++)
    {
      if (j > 0) {
        localStringBuffer.append(',');
      }
      localStringBuffer.append(members[j][0]);
      if (members[j][0] != members[j][1])
      {
        localStringBuffer.append('-');
        localStringBuffer.append(members[j][1]);
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\SetOfIntegerSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */