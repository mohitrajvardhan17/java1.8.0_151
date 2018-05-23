package sun.util.locale.provider;

import java.io.IOException;
import java.text.BreakIterator;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class BreakIteratorProviderImpl
  extends BreakIteratorProvider
  implements AvailableLanguageTags
{
  private static final int CHARACTER_INDEX = 0;
  private static final int WORD_INDEX = 1;
  private static final int LINE_INDEX = 2;
  private static final int SENTENCE_INDEX = 3;
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public BreakIteratorProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(langtags);
  }
  
  public BreakIterator getWordInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 1, "WordData", "WordDictionary");
  }
  
  public BreakIterator getLineInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 2, "LineData", "LineDictionary");
  }
  
  public BreakIterator getCharacterInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 0, "CharacterData", "CharacterDictionary");
  }
  
  public BreakIterator getSentenceInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 3, "SentenceData", "SentenceDictionary");
  }
  
  private BreakIterator getBreakInstance(Locale paramLocale, int paramInt, String paramString1, String paramString2)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    LocaleResources localLocaleResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String[] arrayOfString = (String[])localLocaleResources.getBreakIteratorInfo("BreakIteratorClasses");
    String str1 = (String)localLocaleResources.getBreakIteratorInfo(paramString1);
    try
    {
      switch (arrayOfString[paramInt])
      {
      case "RuleBasedBreakIterator": 
        return new RuleBasedBreakIterator(str1);
      case "DictionaryBasedBreakIterator": 
        String str3 = (String)localLocaleResources.getBreakIteratorInfo(paramString2);
        return new DictionaryBasedBreakIterator(str1, str3);
      }
      throw new IllegalArgumentException("Invalid break iterator class \"" + arrayOfString[paramInt] + "\"");
    }
    catch (IOException|MissingResourceException|IllegalArgumentException localIOException)
    {
      throw new InternalError(localIOException.toString(), localIOException);
    }
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, type, langtags);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\BreakIteratorProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */