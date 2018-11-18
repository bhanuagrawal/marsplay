package agrawal.bhanu.marsplay;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import agrawal.bhanu.marsplay.imagelist.ui.ImageList;
import agrawal.bhanu.marsplay.upload.ui.Camera;
import agrawal.bhanu.marsplay.upload.ImageManager;
import agrawal.bhanu.marsplay.upload.ui.UploadImagePreview;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        ImageList.OnFragmentInteractionListener,
        Camera.OnFragmentInteractionListener, UploadImagePreview.OnFragmentInteractionListener {

    public static final String MAIN_FRAGMENT = "dfsgfdhfmgcfxgd";
    public static final String UPLOAD_PREVIEW_FRAGMENT = "sdrtdthgfjgfdh" ;
    private Fragment mainFragment;
    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageManager = new ImageManager(getApplication());
        setContentView(R.layout.activity_main);


        if (savedInstanceState != null) {
            mainFragment = getSupportFragmentManager().getFragment(savedInstanceState, MAIN_FRAGMENT);
        }
        else{
            mainFragment = MainFragment.newInstance("", "");
        }

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, mainFragment, MainActivity.MAIN_FRAGMENT).
                commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT, mainFragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCameraSelected() {
        mainFragment = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
        ((MainFragment)mainFragment).openCamera();
    }

    @Override
    public void openImagePreviewFragment(Uri uri) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, UploadImagePreview.newInstance("7055553175", uri.getPath(), uri,  true), MainActivity.UPLOAD_PREVIEW_FRAGMENT).
                addToBackStack(MainActivity.MAIN_FRAGMENT).
                commit();
    }

    @Override
    public void onCapture(String path) {



        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, UploadImagePreview.newInstance("7055553175", path, null, true), MainActivity.UPLOAD_PREVIEW_FRAGMENT).
                addToBackStack(MainActivity.MAIN_FRAGMENT).
                commit();

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
        onBackPressed();
    }

    @Override
    public void onCancelUpload(String path) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        mainFragment = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
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
}
