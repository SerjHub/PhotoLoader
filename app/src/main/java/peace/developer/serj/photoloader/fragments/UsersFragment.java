package peace.developer.serj.photoloader.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import peace.developer.serj.photoloader.MainActivity;
import peace.developer.serj.photoloader.R;
import peace.developer.serj.photoloader.interfaces.ApiCallback;
import peace.developer.serj.photoloader.models.User;

public class UsersFragment extends Fragment implements ApiCallback {
    private final String TAG = "UsersFragment";
    LinearLayout container_ll;
    MainActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.users_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkProvider.getmInstance().getUsers(this);
        mActivity = (MainActivity) getActivity();
        initViews(view);
    }

    private void initViews(View view) {
        container_ll = view.findViewById(R.id.items_container);
    }

    @Override
    public void onResponse(String response) {
        try {
            List<User> list = new ArrayList<>();
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(new User(object.getInt("id"), object.getString("name")));
            }
            fillUsers(list);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError() {
        Toast.makeText(mActivity, "Some error", Toast.LENGTH_SHORT).show();
    }


    private void fillUsers(final List<User> list) {

        for (int i = 0; i < list.size(); i++) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.item_view, null);

            ((TextView) v.findViewById(R.id.user_name)).setText(list.get(i).getName());
            container_ll.addView(v);
            v.setTag(i + 1);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getFragmentManager().beginTransaction().hide(UsersFragment.this).commit();

                    PhotosFragment fragment;
                    if (mActivity.photosFragment != null) {
                        fragment = mActivity.photosFragment;
                        getFragmentManager().beginTransaction().show(fragment).commit();
                    } else {
                        fragment = new PhotosFragment();
                        mActivity.photosFragment = fragment;
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }

                    fragment.setCurrentUser(list.get((int) v.getTag() - 1));


                }
            });
        }
    }

}
