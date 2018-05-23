package java.lang;

import java.io.Serializable;
import java.util.Objects;

public final class StackTraceElement
  implements Serializable
{
  private String declaringClass;
  private String methodName;
  private String fileName;
  private int lineNumber;
  private static final long serialVersionUID = 6992337162326171013L;
  
  public StackTraceElement(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    declaringClass = ((String)Objects.requireNonNull(paramString1, "Declaring class is null"));
    methodName = ((String)Objects.requireNonNull(paramString2, "Method name is null"));
    fileName = paramString3;
    lineNumber = paramInt;
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public String getClassName()
  {
    return declaringClass;
  }
  
  public String getMethodName()
  {
    return methodName;
  }
  
  public boolean isNativeMethod()
  {
    return lineNumber == -2;
  }
  
  public String toString()
  {
    return getClassName() + "." + methodName + (fileName != null ? "(" + fileName + ")" : (fileName != null) && (lineNumber >= 0) ? "(" + fileName + ":" + lineNumber + ")" : isNativeMethod() ? "(Native Method)" : "(Unknown Source)");
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof StackTraceElement)) {
      return false;
    }
    StackTraceElement localStackTraceElement = (StackTraceElement)paramObject;
    return (declaringClass.equals(declaringClass)) && (lineNumber == lineNumber) && (Objects.equals(methodName, methodName)) && (Objects.equals(fileName, fileName));
  }
  
  public int hashCode()
  {
    int i = 31 * declaringClass.hashCode() + methodName.hashCode();
    i = 31 * i + Objects.hashCode(fileName);
    i = 31 * i + lineNumber;
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\StackTraceElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */