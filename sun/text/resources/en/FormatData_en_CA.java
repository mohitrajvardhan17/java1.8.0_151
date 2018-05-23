package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_CA
  extends ParallelListResourceBundle
{
  public FormatData_en_CA() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "TimePatterns", { "h:mm:ss 'o''clock' a z", "h:mm:ss z a", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, MMMM d, yyyy", "MMMM d, yyyy", "d-MMM-yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\resources\en\FormatData_en_CA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */