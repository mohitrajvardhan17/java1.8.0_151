package sun.misc;

public class RequestProcessor
  implements Runnable
{
  private static Queue<Request> requestQueue;
  private static Thread dispatcher;
  
  public RequestProcessor() {}
  
  public static void postRequest(Request paramRequest)
  {
    lazyInitialize();
    requestQueue.enqueue(paramRequest);
  }
  
  public void run()
  {
    
    try
    {
      for (;;)
      {
        Request localRequest = (Request)requestQueue.dequeue();
        try
        {
          localRequest.execute();
        }
        catch (Throwable localThrowable) {}
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public static synchronized void startProcessing()
  {
    if (dispatcher == null)
    {
      dispatcher = new Thread(new RequestProcessor(), "Request Processor");
      dispatcher.setPriority(7);
      dispatcher.start();
    }
  }
  
  private static synchronized void lazyInitialize()
  {
    if (requestQueue == null) {
      requestQueue = new Queue();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\RequestProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */