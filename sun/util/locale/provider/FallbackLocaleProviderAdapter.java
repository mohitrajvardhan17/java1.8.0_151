package sun.util.locale.provider;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class FallbackLocaleProviderAdapter
  extends JRELocaleProviderAdapter
{
  private static final Set<String> rootTagSet = Collections.singleton(Locale.ROOT.toLanguageTag());
  private final LocaleResources rootLocaleResources = new LocaleResources(this, Locale.ROOT);
  
  public FallbackLocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.FALLBACK;
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale)
  {
    return rootLocaleResources;
  }
  
  protected Set<String> createLanguageTagSet(String paramString)
  {
    return rootTagSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\FallbackLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */