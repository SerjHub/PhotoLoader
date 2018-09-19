package peace.developer.serj.photoloader;

import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import peace.developer.serj.photoloader.Api.NetworkProvider;
import peace.developer.serj.photoloader.fragments.PhotosFragment;
import peace.developer.serj.photoloader.fragments.UsersFragment;
import peace.developer.serj.photoloader.interfaces.ApiCallback;
import peace.developer.serj.photoloader.models.User;

public class MainActivity extends AppCompatActivity {
    public PhotosFragment photosFragment;
    public UsersFragment usersFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkProvider.mainThreadHandler = new Handler();
        usersFragment = new UsersFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, usersFragment).commit();


    }

    @Override
    public void onBackPressed() {
        if (usersFragment.isVisible())
            super.onBackPressed();
        else if (photosFragment != null && photosFragment.isVisible()){
            getFragmentManager().beginTransaction().hide(photosFragment).commit();
            getFragmentManager().beginTransaction().show(usersFragment).commit();
        }
    }
}
