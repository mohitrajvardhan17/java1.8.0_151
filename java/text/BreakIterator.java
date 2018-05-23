package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class BreakIterator
  implements Cloneable
{
  public static final int DONE = -1;
  private static final int CHARACTER_INDEX = 0;
  private static final int WORD_INDEX = 1;
  private static final int LINE_INDEX = 2;
  private static final int SENTENCE_INDEX = 3;
  private static final SoftReference<BreakIteratorCache>[] iterCache = (SoftReference[])new SoftReference[4];
  
  protected BreakIterator() {}
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public abstract int first();
  
  public abstract int last();
  
  public abstract int next(int paramInt);
  
  public abstract int next();
  
  public abstract int previous();
  
  public abstract int following(int paramInt);
  
  public int preceding(int paramInt)
  {
    for (int i = following(paramInt); (i >= paramInt) && (i != -1); i = previous()) {}
    return i;
  }
  
  public boolean isBoundary(int paramInt)
  {
    if (paramInt == 0) {
      return true;
    }
    int i = following(paramInt - 1);
    if (i == -1) {
      throw new IllegalArgumentException();
    }
    return i == paramInt;
  }
  
  public abstract int current();
  
  public abstract CharacterIterator getText();
  
  public void setText(String paramString)
  {
    setText(new StringCharacterIterator(paramString));
  }
  
  public abstract void setText(CharacterIterator paramCharacterIterator);
  
  public static BreakIterator getWordInstance()
  {
    return getWordInstance(Locale.getDefault());
  }
  
  public static BreakIterator getWordInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 1);
  }
  
  public static BreakIterator getLineInstance()
  {
    return getLineInstance(Locale.getDefault());
  }
  
  public static BreakIterator getLineInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 2);
  }
  
  public static BreakIterator getCharacterInstance()
  {
    return getCharacterInstance(Locale.getDefault());
  }
  
  public static BreakIterator getCharacterInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 0);
  }
  
  public static BreakIterator getSentenceInstance()
  {
    return getSentenceInstance(Locale.getDefault());
  }
  
  public static BreakIterator getSentenceInstance(Locale paramLocale)
  {
    return getBreakInstance(paramLocale, 3);
  }
  
  private static BreakIterator getBreakInstance(Locale paramLocale, int paramInt)
  {
    if (iterCache[paramInt] != null)
    {
      localObject = (BreakIteratorCache)iterCache[paramInt].get();
      if ((localObject != null) && (((BreakIteratorCache)localObject).getLocale().equals(paramLocale))) {
        return ((BreakIteratorCache)localObject).createBreakInstance();
      }
    }
    Object localObject = createBreakInstance(paramLocale, paramInt);
    BreakIteratorCache localBreakIteratorCache = new BreakIteratorCache(paramLocale, (BreakIterator)localObject);
    iterCache[paramInt] = new SoftReference(localBreakIteratorCache);
    return (BreakIterator)localObject;
  }
  
  private static BreakIterator createBreakInstance(Locale paramLocale, int paramInt)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(BreakIteratorProvider.class, paramLocale);
    BreakIterator localBreakIterator = createBreakInstance(localLocaleProviderAdapter, paramLocale, paramInt);
    if (localBreakIterator == null) {
      localBreakIterator = createBreakInstance(LocaleProviderAdapter.forJRE(), paramLocale, paramInt);
    }
    return localBreakIterator;
  }
  
  private static BreakIterator createBreakInstance(LocaleProviderAdapter paramLocaleProviderAdapter, Locale paramLocale, int paramInt)
  {
    BreakIteratorProvider localBreakIteratorProvider = paramLocaleProviderAdapter.getBreakIteratorProvider();
    BreakIterator localBreakIterator = null;
    switch (paramInt)
    {
    case 0: 
      localBreakIterator = localBreakIteratorProvider.getCharacterInstance(paramLocale);
      break;
    case 1: 
      localBreakIterator = localBreakIteratorProvider.getWordInstance(paramLocale);
      break;
    case 2: 
      localBreakIterator = localBreakIteratorProvider.getLineInstance(paramLocale);
      break;
    case 3: 
      localBreakIterator = localBreakIteratorProvider.getSentenceInstance(paramLocale);
    }
    return localBreakIterator;
  }
  
  public static synchronized Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(BreakIteratorProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  private static final class BreakIteratorCache
  {
    private BreakIterator iter;
    private Locale locale;
    
    BreakIteratorCache(Locale paramLocale, BreakIterator paramBreakIterator)
    {
      locale = paramLocale;
      iter = ((BreakIterator)paramBreakIterator.clone());
    }
    
    Locale getLocale()
    {
      return locale;
    }
    
    BreakIterator createBreakInstance()
    {
      return (BreakIterator)iter.clone();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\BreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */