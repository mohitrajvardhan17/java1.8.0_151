package java.text;

final class RuleBasedCollationKey
  extends CollationKey
{
  private String key = null;
  
  public int compareTo(CollationKey paramCollationKey)
  {
    int i = key.compareTo(key);
    if (i <= -1) {
      return -1;
    }
    if (i >= 1) {
      return 1;
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!getClass().equals(paramObject.getClass()))) {
      return false;
    }
    RuleBasedCollationKey localRuleBasedCollationKey = (RuleBasedCollationKey)paramObject;
    return key.equals(key);
  }
  
  public int hashCode()
  {
    return key.hashCode();
  }
  
  public byte[] toByteArray()
  {
    char[] arrayOfChar = key.toCharArray();
    byte[] arrayOfByte = new byte[2 * arrayOfChar.length];
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      arrayOfByte[(i++)] = ((byte)(arrayOfChar[j] >>> '\b'));
      arrayOfByte[(i++)] = ((byte)(arrayOfChar[j] & 0xFF));
    }
    return arrayOfByte;
  }
  
  RuleBasedCollationKey(String paramString1, String paramString2)
  {
    super(paramString1);
    key = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\RuleBasedCollationKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */