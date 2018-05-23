package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_MT
  extends ParallelListResourceBundle
{
  public FormatData_en_MT() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "dd/MM/yyyy" } }, { "DateTimePatterns", { "{1} {0}" } } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\resources\en\FormatData_en_MT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */