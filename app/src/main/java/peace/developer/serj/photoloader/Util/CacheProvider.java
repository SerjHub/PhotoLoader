package peace.developer.serj.photoloader.Util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class CacheProvider {
    private LruCache<Integer,Bitmap> mCache;

    public CacheProvider (LruCache<Integer,Bitmap> cache){
        mCache = cache;
    }

    public void saveToCache(Bitmap bitmap, int id){
        mCache.put(id,bitmap);
    }
    public Bitmap getPhotoFromCache(int id){
        return mCache.get(id);
    }
}
