package java.text;

final class EntryPair
{
  public String entryName;
  public int value;
  public boolean fwd;
  
  public EntryPair(String paramString, int paramInt)
  {
    this(paramString, paramInt, true);
  }
  
  public EntryPair(String paramString, int paramInt, boolean paramBoolean)
  {
    entryName = paramString;
    value = paramInt;
    fwd = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\EntryPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */