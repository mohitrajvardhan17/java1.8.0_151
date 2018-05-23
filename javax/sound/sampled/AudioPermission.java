package javax.sound.sampled;

import java.security.BasicPermission;

public class AudioPermission
  extends BasicPermission
{
  public AudioPermission(String paramString)
  {
    super(paramString);
  }
  
  public AudioPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\AudioPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */