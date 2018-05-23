package sun.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;

public class CoreResourceBundleControl
  extends ResourceBundle.Control
{
  private final Collection<Locale> excludedJDKLocales = Arrays.asList(new Locale[] { Locale.GERMANY, Locale.ENGLISH, Locale.US, new Locale("es", "ES"), Locale.FRANCE, Locale.ITALY, Locale.JAPAN, Locale.KOREA, new Locale("sv", "SE"), Locale.CHINESE });
  private static CoreResourceBundleControl resourceBundleControlInstance = new CoreResourceBundleControl();
  
  protected CoreResourceBundleControl() {}
  
  public static CoreResourceBundleControl getRBControlInstance()
  {
    return resourceBundleControlInstance;
  }
  
  public static CoreResourceBundleControl getRBControlInstance(String paramString)
  {
    if ((paramString.startsWith("com.sun.")) || (paramString.startsWith("java.")) || (paramString.startsWith("javax.")) || (paramString.startsWith("sun."))) {
      return resourceBundleControlInstance;
    }
    return null;
  }
  
  public List<Locale> getCandidateLocales(String paramString, Locale paramLocale)
  {
    List localList = super.getCandidateLocales(paramString, paramLocale);
    localList.removeAll(excludedJDKLocales);
    return localList;
  }
  
  public long getTimeToLive(String paramString, Locale paramLocale)
  {
    return -1L;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\CoreResourceBundleControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */