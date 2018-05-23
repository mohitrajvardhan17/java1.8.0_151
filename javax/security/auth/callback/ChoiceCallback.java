package javax.security.auth.callback;

import java.io.Serializable;

public class ChoiceCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = -3975664071579892167L;
  private String prompt;
  private String[] choices;
  private int defaultChoice;
  private boolean multipleSelectionsAllowed;
  private int[] selections;
  
  public ChoiceCallback(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayOfString == null) || (paramArrayOfString.length == 0) || (paramInt < 0) || (paramInt >= paramArrayOfString.length)) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].length() == 0)) {
        throw new IllegalArgumentException();
      }
    }
    prompt = paramString;
    choices = paramArrayOfString;
    defaultChoice = paramInt;
    multipleSelectionsAllowed = paramBoolean;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public String[] getChoices()
  {
    return choices;
  }
  
  public int getDefaultChoice()
  {
    return defaultChoice;
  }
  
  public boolean allowMultipleSelections()
  {
    return multipleSelectionsAllowed;
  }
  
  public void setSelectedIndex(int paramInt)
  {
    selections = new int[1];
    selections[0] = paramInt;
  }
  
  public void setSelectedIndexes(int[] paramArrayOfInt)
  {
    if (!multipleSelectionsAllowed) {
      throw new UnsupportedOperationException();
    }
    selections = paramArrayOfInt;
  }
  
  public int[] getSelectedIndexes()
  {
    return selections;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\callback\ChoiceCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */