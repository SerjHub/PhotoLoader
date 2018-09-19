package peace.developer.serj.photoloader.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import peace.developer.serj.photoloader.Api.NetworkProvider;
import peace.developer.serj.photoloader.MainActivity;
import peace.developer.serj.photoloader.R;
import peace.developer.serj.photoloader.Util.CacheProvider;
import peace.developer.serj.photoloader.adpters.PhotoAdapter;
import peace.developer.serj.photoloader.interfaces.ApiCallback;
import peace.developer.serj.photoloader.models.Photo;
import peace.developer.serj.photoloader.models.User;

public class PhotosFragment extends Fragment implements ApiCallback {
    private MainActivity mActivity;
    private User currentUser;
    private RecyclerView recyclerView;
    private List<Photo> photoList;

    public void setCurrentUser(User user) {
        currentUser = user;
        photoList = currentUser.getPhotos();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.photos_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.photo_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new PhotoAdapter(photoList,createCache()));
        Log.i("CHECKVIEW", " created");
        getPhotos();
    }

    private void getPhotos() {
        NetworkProvider.getmInstance().getUserAlbums(currentUser.getId(), this);
    }


    @Override
    public void onResponse(String response) {
        Log.i("PGOTOS: ", response);

        try {
            List<Integer> albumList = new ArrayList<>();
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                albumList.add(array.getJSONObject(i).getInt("id"));
            }
            if (albumList.size() != 0)
                getPhotosFromAlbum(albumList, 0);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {
        Toast.makeText(mActivity, "Some error", Toast.LENGTH_SHORT).show();
    }

    private void getPhotosFromAlbum(final List<Integer> idsList, final int currentIndex) {

        NetworkProvider.getmInstance().getPhotoByAlbumId(idsList.get(currentIndex), new ApiCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);
                    List<Photo> list = currentUser.getPhotos();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        int id = object.getInt("id");
                        String title = object.getString("title");
                        String link = object.getString("url");
                        list.add(new Photo(id, link, title));

                    }
                    int arrayLength = array.length();
                    recyclerView.getAdapter().notifyItemRangeChanged(list.size() - arrayLength, arrayLength);
                    if (currentIndex < idsList.size() - 1)
                        getPhotosFromAlbum(idsList, currentIndex + 1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError() {}
        });
    }

    private CacheProvider createCache(){
        final int memorySize = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final LruCache cache = new LruCache<Integer,Bitmap>(memorySize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        return new CacheProvider(cache);
    }

}
