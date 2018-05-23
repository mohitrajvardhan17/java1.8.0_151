package com.sun.media.sound;

public final class AudioSynthesizerPropertyInfo
{
  public String name;
  public String description = null;
  public Object value = null;
  public Class valueClass = null;
  public Object[] choices = null;
  
  public AudioSynthesizerPropertyInfo(String paramString, Object paramObject)
  {
    name = paramString;
    if ((paramObject instanceof Class))
    {
      valueClass = ((Class)paramObject);
    }
    else
    {
      value = paramObject;
      if (paramObject != null) {
        valueClass = paramObject.getClass();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AudioSynthesizerPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */