package javax.swing;

public abstract class InputVerifier
{
  public InputVerifier() {}
  
  public abstract boolean verify(JComponent paramJComponent);
  
  public boolean shouldYieldFocus(JComponent paramJComponent)
  {
    return verify(paramJComponent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\InputVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */