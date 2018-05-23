package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.CollatorProvider;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class Collator
  implements Comparator<Object>, Cloneable
{
  public static final int PRIMARY = 0;
  public static final int SECONDARY = 1;
  public static final int TERTIARY = 2;
  public static final int IDENTICAL = 3;
  public static final int NO_DECOMPOSITION = 0;
  public static final int CANONICAL_DECOMPOSITION = 1;
  public static final int FULL_DECOMPOSITION = 2;
  private int strength = 0;
  private int decmp = 0;
  private static final ConcurrentMap<Locale, SoftReference<Collator>> cache = new ConcurrentHashMap();
  static final int LESS = -1;
  static final int EQUAL = 0;
  static final int GREATER = 1;
  
  public static synchronized Collator getInstance()
  {
    return getInstance(Locale.getDefault());
  }
  
  public static Collator getInstance(Locale paramLocale)
  {
    SoftReference localSoftReference = (SoftReference)cache.get(paramLocale);
    Object localObject = localSoftReference != null ? (Collator)localSoftReference.get() : null;
    if (localObject == null)
    {
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(CollatorProvider.class, paramLocale);
      CollatorProvider localCollatorProvider = localLocaleProviderAdapter.getCollatorProvider();
      localObject = localCollatorProvider.getInstance(paramLocale);
      if (localObject == null) {
        localObject = LocaleProviderAdapter.forJRE().getCollatorProvider().getInstance(paramLocale);
      }
      for (;;)
      {
        if (localSoftReference != null) {
          cache.remove(paramLocale, localSoftReference);
        }
        localSoftReference = (SoftReference)cache.putIfAbsent(paramLocale, new SoftReference(localObject));
        if (localSoftReference == null) {
          break;
        }
        Collator localCollator = (Collator)localSoftReference.get();
        if (localCollator != null)
        {
          localObject = localCollator;
          break;
        }
      }
    }
    return (Collator)((Collator)localObject).clone();
  }
  
  public abstract int compare(String paramString1, String paramString2);
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return compare((String)paramObject1, (String)paramObject2);
  }
  
  public abstract CollationKey getCollationKey(String paramString);
  
  public boolean equals(String paramString1, String paramString2)
  {
    return compare(paramString1, paramString2) == 0;
  }
  
  public synchronized int getStrength()
  {
    return strength;
  }
  
  public synchronized void setStrength(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
      throw new IllegalArgumentException("Incorrect comparison level.");
    }
    strength = paramInt;
  }
  
  public synchronized int getDecomposition()
  {
    return decmp;
  }
  
  public synchronized void setDecomposition(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
      throw new IllegalArgumentException("Wrong decomposition mode.");
    }
    decmp = paramInt;
  }
  
  public static synchronized Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CollatorProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  public Object clone()
  {
    try
    {
      return (Collator)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    Collator localCollator = (Collator)paramObject;
    return (strength == strength) && (decmp == decmp);
  }
  
  public abstract int hashCode();
  
  protected Collator() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\Collator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */