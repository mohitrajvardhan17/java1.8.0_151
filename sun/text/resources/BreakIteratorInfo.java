package sun.text.resources;

import java.util.ListResourceBundle;

public class BreakIteratorInfo
  extends ListResourceBundle
{
  public BreakIteratorInfo() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "BreakIteratorClasses", { "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator" } }, { "CharacterData", "CharacterBreakIteratorData" }, { "WordData", "WordBreakIteratorData" }, { "LineData", "LineBreakIteratorData" }, { "SentenceData", "SentenceBreakIteratorData" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\resources\BreakIteratorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */