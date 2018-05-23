package sun.text.normalizer;

import java.text.ParsePosition;

@Deprecated
public abstract interface SymbolTable
{
  @Deprecated
  public static final char SYMBOL_REF = '$';
  
  @Deprecated
  public abstract char[] lookup(String paramString);
  
  @Deprecated
  public abstract UnicodeMatcher lookupMatcher(int paramInt);
  
  @Deprecated
  public abstract String parseReference(String paramString, ParsePosition paramParsePosition, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */