package javax.swing;

final class LegacyLayoutFocusTraversalPolicy
  extends LayoutFocusTraversalPolicy
{
  LegacyLayoutFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager)
  {
    super(new CompareTabOrderComparator(paramDefaultFocusManager));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\LegacyLayoutFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */