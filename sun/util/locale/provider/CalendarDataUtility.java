package sun.util.locale.provider;

import java.util.Locale;
import java.util.Map;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;

public class CalendarDataUtility
{
  public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
  public static final String MINIMAL_DAYS_IN_FIRST_WEEK = "minimalDaysInFirstWeek";
  
  private CalendarDataUtility() {}
  
  public static int retrieveFirstDayOfWeek(Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
    Integer localInteger = (Integer)localLocaleServiceProviderPool.getLocalizedObject(CalendarWeekParameterGetter.INSTANCE, paramLocale, "firstDayOfWeek", new Object[0]);
    return (localInteger != null) && (localInteger.intValue() >= 1) && (localInteger.intValue() <= 7) ? localInteger.intValue() : 1;
  }
  
  public static int retrieveMinimalDaysInFirstWeek(Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
    Integer localInteger = (Integer)localLocaleServiceProviderPool.getLocalizedObject(CalendarWeekParameterGetter.INSTANCE, paramLocale, "minimalDaysInFirstWeek", new Object[0]);
    return (localInteger != null) && (localInteger.intValue() >= 1) && (localInteger.intValue() <= 7) ? localInteger.intValue() : 1;
  }
  
  public static String retrieveFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    return (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) });
  }
  
  public static String retrieveJavaTimeFieldValueName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    String str = (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(true) });
    if (str == null) {
      str = (String)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNameGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Boolean.valueOf(false) });
    }
    return str;
  }
  
  public static Map<String, Integer> retrieveFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    return (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) });
  }
  
  public static Map<String, Integer> retrieveJavaTimeFieldValueNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
    Map localMap = (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(true) });
    if (localMap == null) {
      localMap = (Map)localLocaleServiceProviderPool.getLocalizedObject(CalendarFieldValueNamesMapGetter.INSTANCE, paramLocale, normalizeCalendarType(paramString), new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(false) });
    }
    return localMap;
  }
  
  static String normalizeCalendarType(String paramString)
  {
    String str;
    if ((paramString.equals("gregorian")) || (paramString.equals("iso8601"))) {
      str = "gregory";
    } else if (paramString.startsWith("islamic")) {
      str = "islamic";
    } else {
      str = paramString;
    }
    return str;
  }
  
  private static class CalendarFieldValueNameGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, String>
  {
    private static final CalendarFieldValueNameGetter INSTANCE = new CalendarFieldValueNameGetter();
    
    private CalendarFieldValueNameGetter() {}
    
    public String getObject(CalendarNameProvider paramCalendarNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 4);
      int i = ((Integer)paramVarArgs[0]).intValue();
      int j = ((Integer)paramVarArgs[1]).intValue();
      int k = ((Integer)paramVarArgs[2]).intValue();
      boolean bool = ((Boolean)paramVarArgs[3]).booleanValue();
      if ((bool) && ((paramCalendarNameProvider instanceof CalendarNameProviderImpl)))
      {
        String str = ((CalendarNameProviderImpl)paramCalendarNameProvider).getJavaTimeDisplayName(paramString, i, j, k, paramLocale);
        return str;
      }
      return paramCalendarNameProvider.getDisplayName(paramString, i, j, k, paramLocale);
    }
  }
  
  private static class CalendarFieldValueNamesMapGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, Map<String, Integer>>
  {
    private static final CalendarFieldValueNamesMapGetter INSTANCE = new CalendarFieldValueNamesMapGetter();
    
    private CalendarFieldValueNamesMapGetter() {}
    
    public Map<String, Integer> getObject(CalendarNameProvider paramCalendarNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 3);
      int i = ((Integer)paramVarArgs[0]).intValue();
      int j = ((Integer)paramVarArgs[1]).intValue();
      boolean bool = ((Boolean)paramVarArgs[2]).booleanValue();
      if ((bool) && ((paramCalendarNameProvider instanceof CalendarNameProviderImpl)))
      {
        Map localMap = ((CalendarNameProviderImpl)paramCalendarNameProvider).getJavaTimeDisplayNames(paramString, i, j, paramLocale);
        return localMap;
      }
      return paramCalendarNameProvider.getDisplayNames(paramString, i, j, paramLocale);
    }
  }
  
  private static class CalendarWeekParameterGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarDataProvider, Integer>
  {
    private static final CalendarWeekParameterGetter INSTANCE = new CalendarWeekParameterGetter();
    
    private CalendarWeekParameterGetter() {}
    
    public Integer getObject(CalendarDataProvider paramCalendarDataProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 0);
      int i;
      switch (paramString)
      {
      case "firstDayOfWeek": 
        i = paramCalendarDataProvider.getFirstDayOfWeek(paramLocale);
        break;
      case "minimalDaysInFirstWeek": 
        i = paramCalendarDataProvider.getMinimalDaysInFirstWeek(paramLocale);
        break;
      default: 
        throw new InternalError("invalid requestID: " + paramString);
      }
      return i != 0 ? Integer.valueOf(i) : null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\CalendarDataUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */