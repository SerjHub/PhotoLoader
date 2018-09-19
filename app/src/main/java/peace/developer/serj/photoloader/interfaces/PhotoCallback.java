package peace.developer.serj.photoloader.interfaces;

import android.graphics.Bitmap;

public interface PhotoCallback extends BaseCallBack {

    void onPhotoLoaded(Bitmap bitmap);

}
