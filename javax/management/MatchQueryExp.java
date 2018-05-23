package javax.management;

class MatchQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -7156603696948215014L;
  private AttributeValueExp exp;
  private String pattern;
  
  public MatchQueryExp() {}
  
  public MatchQueryExp(AttributeValueExp paramAttributeValueExp, StringValueExp paramStringValueExp)
  {
    exp = paramAttributeValueExp;
    pattern = paramStringValueExp.getValue();
  }
  
  public AttributeValueExp getAttribute()
  {
    return exp;
  }
  
  public String getPattern()
  {
    return pattern;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    ValueExp localValueExp = exp.apply(paramObjectName);
    if (!(localValueExp instanceof StringValueExp)) {
      return false;
    }
    return wildmatch(((StringValueExp)localValueExp).getValue(), pattern);
  }
  
  public String toString()
  {
    return exp + " like " + new StringValueExp(pattern);
  }
  
  private static boolean wildmatch(String paramString1, String paramString2)
  {
    int j = 0;
    int k = 0;
    int m = paramString1.length();
    int n = paramString2.length();
    while (k < n)
    {
      int i = paramString2.charAt(k++);
      if (i == 63)
      {
        j++;
        if (j > m) {
          return false;
        }
      }
      else if (i == 91)
      {
        if (j >= m) {
          return false;
        }
        int i1 = 1;
        int i2 = 0;
        if (paramString2.charAt(k) == '!')
        {
          i1 = 0;
          k++;
        }
        while ((i = paramString2.charAt(k)) != ']')
        {
          k++;
          if (k >= n) {
            break;
          }
          if ((paramString2.charAt(k) == '-') && (k + 1 < n) && (paramString2.charAt(k + 1) != ']'))
          {
            if ((paramString1.charAt(j) >= paramString2.charAt(k - 1)) && (paramString1.charAt(j) <= paramString2.charAt(k + 1))) {
              i2 = 1;
            }
            k++;
          }
          else if (i == paramString1.charAt(j))
          {
            i2 = 1;
          }
        }
        if ((k >= n) || (i1 != i2)) {
          return false;
        }
        k++;
        j++;
      }
      else
      {
        if (i == 42)
        {
          if (k >= n) {
            return true;
          }
          do
          {
            if (wildmatch(paramString1.substring(j), paramString2.substring(k))) {
              return true;
            }
            j++;
          } while (j < m);
          return false;
        }
        if (i == 92)
        {
          if ((k >= n) || (j >= m) || (paramString2.charAt(k++) != paramString1.charAt(j++))) {
            return false;
          }
        }
        else if ((j >= m) || (i != paramString1.charAt(j++))) {
          return false;
        }
      }
    }
    return j == m;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MatchQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */