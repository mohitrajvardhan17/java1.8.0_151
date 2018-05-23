package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobMediaSheetsSupported
  extends SetOfIntegerSyntax
  implements SupportedValuesAttribute
{
  private static final long serialVersionUID = 2953685470388672940L;
  
  public JobMediaSheetsSupported(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("Null range specified");
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Job K octets value < 0 specified");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof JobMediaSheetsSupported));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return JobMediaSheetsSupported.class;
  }
  
  public final String getName()
  {
    return "job-media-sheets-supported";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\standard\JobMediaSheetsSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */