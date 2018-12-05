package agrawal.bhanu.marsplay;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

import agrawal.bhanu.marsplay.imagelist.ui.ImageList;
import agrawal.bhanu.marsplay.upload.ui.Camera;
import agrawal.bhanu.marsplay.upload.ImageManager;
import agrawal.bhanu.marsplay.upload.ui.UploadImagePreview;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        ImageList.OnFragmentInteractionListener,
        Camera.OnFragmentInteractionListener, UploadImagePreview.OnFragmentInteractionListener {

    public static final String MAIN_FRAGMENT = "dfsgfdhfmgcfxgd";
    public static final String UPLOAD_PREVIEW_FRAGMENT = "sdrtdthgfjgfdh" ;
    private static final String NAV_FRAGMENT = "asdfasdfasdfasdf";
    private ImageManager imageManager;
    private NavController navigationController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageManager = new ImageManager(getApplication());
        setContentView(R.layout.activity_main);


        navigationController = Navigation.findNavController(this, R.id.my_nav_host_fragment);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            if (type.startsWith("image/")) {

/*                Bundle bundle = new Bundle();
                bundle.putString("param1", "");
                bundle.putString("param2", "");
                navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
                navigationController.navigate(R.id.mainFragment, bundle);*/

                navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                openImagePreviewFragment(imageUri);
            }

            return;
        }
        if (savedInstanceState != null) {
            navHostFragment = (NavHostFragment) getSupportFragmentManager().getFragment(savedInstanceState, NAV_FRAGMENT);
        }
        else{
            navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
            savedInstanceState = new Bundle();
            savedInstanceState.putString("param1", "");
            savedInstanceState.putString("param2", "");
            navigationController.navigate(R.id.mainFragment, savedInstanceState);
        }


    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, NAV_FRAGMENT, navHostFragment);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void openImagePreviewFragment(Uri uri) {


        Bundle bundle = new Bundle();
        bundle.putString("param1", "7055553175");
        bundle.putString("param2", uri.getPath());
        bundle.putParcelable("param3", uri);
        bundle.putBoolean("param4", true);

        navigationController.navigate(R.id.uploadImagePreview, bundle);
    }

    @Override
    public void onCapture(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("param1", "7055553175");
        bundle.putString("param2", path);
        bundle.putParcelable("param3", null);
        bundle.putBoolean("param4", true);

        navigationController.navigate(R.id.uploadImagePreview, bundle);

    }

    @Override
    public void onFragmentCreated(Fragment fragment) {

    }

    @Override
    public void onCofirmUpload(File path) {
        imageManager.uploadFile(path);
        onSend();
    }

    private void onSend() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        Fragment mainFragment = navHostFragment.getChildFragmentManager().findFragmentByTag(MAIN_FRAGMENT);

        if(mainFragment == null){

/*            Bundle bundle = new Bundle();
            bundle.putString("param1", "");
            bundle.putString("param2", "");

            navigationController.navigate(R.id.mainFragment, bundle);*/
            onBackPressed();

        }
        else{
            onBackPressed();
        }
    }

    @Override
    public void onCancelUpload(String path) {
        navigationController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        Fragment mainFragment = navHostFragment.getChildFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
        if(mainFragment != null && mainFragment.isVisible()){

            ViewPager viewPager = ((MainFragment) mainFragment).getHomeViewPager();
            if(viewPager.getCurrentItem() != Constants.DEFAULT_PAGE_POSITION){
                viewPager.setCurrentItem(Constants.DEFAULT_PAGE_POSITION, true);
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    public void showImagePreview(String path) {

        Bundle bundle = new Bundle();
        bundle.putString("param1", "7055553175");
        bundle.putString("param2", path);
        bundle.putParcelable("param3", null);
        bundle.putBoolean("param4", false);

        navigationController.navigate(R.id.uploadImagePreview, bundle);




    }




}
