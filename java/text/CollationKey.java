package java.text;

public abstract class CollationKey
  implements Comparable<CollationKey>
{
  private final String source;
  
  public abstract int compareTo(CollationKey paramCollationKey);
  
  public String getSourceString()
  {
    return source;
  }
  
  public abstract byte[] toByteArray();
  
  protected CollationKey(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    source = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\CollationKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */