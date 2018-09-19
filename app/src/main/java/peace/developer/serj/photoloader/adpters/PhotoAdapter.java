package peace.developer.serj.photoloader.adpters;

import android.graphics.Bitmap;
import android.graphics.Outline;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import peace.developer.serj.photoloader.Api.NetworkProvider;
import peace.developer.serj.photoloader.R;
import peace.developer.serj.photoloader.Util.CacheProvider;
import peace.developer.serj.photoloader.Util.CustomImageView;
import peace.developer.serj.photoloader.interfaces.PhotoCallback;
import peace.developer.serj.photoloader.models.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "PhotoAdapter";
    private List<Photo>list;
    private CacheProvider cacheProvider;

    public PhotoAdapter(List<Photo>list, CacheProvider cacheProvider){
        this.list = list;
        this.cacheProvider = cacheProvider;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Photo photo = list.get(position);
        ((ViewHolder)holder).title.setText(photo.getTitle());
        ((ViewHolder)holder).error.setVisibility(View.GONE);


        Bitmap bitmap = cacheProvider.getPhotoFromCache(photo.getId());
        if(bitmap != null){
            Log.i(TAG," FROM CACHE!!");
            ((ViewHolder)holder).image.setImageBitmap(bitmap);
        } else {
            ((ViewHolder)holder).switchProgress(true);
            Log.i(TAG," LOADING ...");
            NetworkProvider.getmInstance().getPhoto(photo.getLink(), new PhotoCallback() {
                @Override
                public void onPhotoLoaded(Bitmap bitmap) {
                    ((ViewHolder)holder).switchProgress(false);
                    cacheProvider.saveToCache(bitmap,photo.getId());
                    ((ViewHolder)holder).image.setImageBitmap(bitmap);
                }

                @Override
                public void onError() {
                    ((ViewHolder)holder).switchProgress(false);
                    ((ViewHolder)holder).error.setVisibility(View.VISIBLE);
                }

            });
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title,error;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            image =  itemView.findViewById(R.id.photo_image);
            title = itemView.findViewById(R.id.photo_title);
            error = itemView.findViewById(R.id.error_text);
            progressBar = itemView.findViewById(R.id.progress_view);
            image.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0,0,view.getWidth(),view.getHeight() + 12, 12);
                    view.setClipToOutline(true);
                }
            });
        }

        public void switchProgress(boolean progressVisible){
            if (progressVisible) {
                image.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
