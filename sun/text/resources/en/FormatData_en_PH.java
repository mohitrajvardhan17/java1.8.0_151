package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_PH
  extends ParallelListResourceBundle
{
  public FormatData_en_PH() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, MMMM d, yyyy", "MMMM d, yyyy", "MM d, yy", "M/d/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\resources\en\FormatData_en_PH.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */