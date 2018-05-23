package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_ZA
  extends ParallelListResourceBundle
{
  public FormatData_en_ZA() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%" } }, { "TimePatterns", { "h:mm:ss a", "h:mm:ss a", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE dd MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "yyyy/MM/dd" } }, { "DateTimePatterns", { "{1} {0}" } } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\resources\en\FormatData_en_ZA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */