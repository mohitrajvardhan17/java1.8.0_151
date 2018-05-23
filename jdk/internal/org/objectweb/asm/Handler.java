package jdk.internal.org.objectweb.asm;

class Handler
{
  Label start;
  Label end;
  Label handler;
  String desc;
  int type;
  Handler next;
  
  Handler() {}
  
  static Handler remove(Handler paramHandler, Label paramLabel1, Label paramLabel2)
  {
    if (paramHandler == null) {
      return null;
    }
    next = remove(next, paramLabel1, paramLabel2);
    int i = start.position;
    int j = end.position;
    int k = position;
    int m = paramLabel2 == null ? Integer.MAX_VALUE : position;
    if ((k < j) && (m > i)) {
      if (k <= i)
      {
        if (m >= j) {
          paramHandler = next;
        } else {
          start = paramLabel2;
        }
      }
      else if (m >= j)
      {
        end = paramLabel1;
      }
      else
      {
        Handler localHandler = new Handler();
        start = paramLabel2;
        end = end;
        handler = handler;
        desc = desc;
        type = type;
        next = next;
        end = paramLabel1;
        next = localHandler;
      }
    }
    return paramHandler;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */