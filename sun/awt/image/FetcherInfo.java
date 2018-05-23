package sun.awt.image;

import java.util.Vector;
import sun.awt.AppContext;

class FetcherInfo
{
  static final int MAX_NUM_FETCHERS_PER_APPCONTEXT = 4;
  Thread[] fetchers = new Thread[4];
  int numFetchers = 0;
  int numWaiting = 0;
  Vector waitList = new Vector();
  private static final Object FETCHER_INFO_KEY = new StringBuffer("FetcherInfo");
  
  private FetcherInfo() {}
  
  static FetcherInfo getFetcherInfo()
  {
    AppContext localAppContext = AppContext.getAppContext();
    synchronized (localAppContext)
    {
      FetcherInfo localFetcherInfo = (FetcherInfo)localAppContext.get(FETCHER_INFO_KEY);
      if (localFetcherInfo == null)
      {
        localFetcherInfo = new FetcherInfo();
        localAppContext.put(FETCHER_INFO_KEY, localFetcherInfo);
      }
      return localFetcherInfo;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\FetcherInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */