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

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        ImageList.OnFragmentInteractionListener,
        Camera.OnFragmentInteractionListener, UploadImagePreview.OnFragmentInteractionListener {

    public static final String MAIN_FRAGMENT = "dfsgfdhfmgcfxgd";
    public static final String UPLOAD_PREVIEW_FRAGMENT = "sdrtdthgfjgfdh" ;
    private ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageManager = new ImageManager(getApplication());
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            if (type.startsWith("image/")) {

                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.container, MainFragment.newInstance("", ""), MainActivity.MAIN_FRAGMENT).
                        commit();


                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                openImagePreviewFragment(imageUri);
            }

            return;
        }
        Fragment currentFragment, mf, uipF;
        if (savedInstanceState != null) {
            mf = getSupportFragmentManager().getFragment(savedInstanceState, MAIN_FRAGMENT);
            uipF  = getSupportFragmentManager().getFragment(savedInstanceState, UPLOAD_PREVIEW_FRAGMENT);
            if(uipF != null){
                currentFragment = mf;
            }
            else if(mf != null){
                currentFragment = mf;
            }
            else{
                mf = MainFragment.newInstance("", "");
                currentFragment = mf;
            }

        }
        else{
            mf = MainFragment.newInstance("", "");
            currentFragment = mf;

        }


        if(currentFragment instanceof MainFragment){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.container, currentFragment, MainActivity.MAIN_FRAGMENT).
                    commit();
        }
        else if(currentFragment instanceof UploadImagePreview){
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.container, currentFragment, MainActivity.UPLOAD_PREVIEW_FRAGMENT).
                    commit();
        }




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        Fragment uipF = getSupportFragmentManager().findFragmentByTag(UPLOAD_PREVIEW_FRAGMENT);
        Fragment mF = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);

        if(uipF!=null && uipF.isVisible()){
            getSupportFragmentManager().putFragment(outState, UPLOAD_PREVIEW_FRAGMENT, uipF);
        }
        else if(mF!=null && mF.isVisible()){
            getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT, mF);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCameraSelected() {
        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
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



        if(getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT) == null){



            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.container, MainFragment.newInstance("", ""), MainActivity.MAIN_FRAGMENT).
                    addToBackStack(null).
                    commit();

            //removeAllFragments();
        }
        else{
            onBackPressed();
        }
    }

    @Override
    public void onCancelUpload(String path) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(UPLOAD_PREVIEW_FRAGMENT);
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        getSupportFragmentManager().popBackStack(UPLOAD_PREVIEW_FRAGMENT, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
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

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, UploadImagePreview.newInstance("7055553175", path, null, false), MainActivity.UPLOAD_PREVIEW_FRAGMENT).
                addToBackStack(MainActivity.MAIN_FRAGMENT).
                commit();
    }

    public void removeAllFragments() {
        for (Fragment fragment:getSupportFragmentManager().getFragments()) {
            if(!(fragment instanceof MainFragment)){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            }
        }

        FragmentManager manager = getSupportFragmentManager();

        for (int i = 0; i < manager.getBackStackEntryCount(); i++){
            String tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            if(!tag.equals(MAIN_FRAGMENT)){
                manager.popBackStackImmediate(i, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }


}
