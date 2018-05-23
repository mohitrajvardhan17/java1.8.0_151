package javax.sound.midi;

public class Patch
{
  private final int bank;
  private final int program;
  
  public Patch(int paramInt1, int paramInt2)
  {
    bank = paramInt1;
    program = paramInt2;
  }
  
  public int getBank()
  {
    return bank;
  }
  
  public int getProgram()
  {
    return program;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Patch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */