package com.example.propertylist.view;

/*
 * <!-- Copyright (C) 2010 The Android Open Source Project

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
-->
 */
import com.example.propertylist.PropertyInterface;
import com.example.propertylist.R;
import com.example.propertylist.model.Property;
import com.example.propertylist.utils.Utils;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class PropertyOverView extends Fragment implements PropertyInterface {
    private View vWebView;
    private int mPropertyPosition = -1;
    private Property adsLists;
    private String mAddress = null;
    private String mDescription = null;
    private String mDafturl = null;
    private String mMediumImage = null;
    private String mLargeImage = null;
    private boolean bLargeScreen = false;
    private Handler  mHandler;
    private TextView addressView = null;
    private TextView messageView = null;

    public static PropertyOverView newInstance
            (String address, String description, String dafturl, String mediumImage, String largeImage,
        int position, boolean largeScreen, Property property) {
        PropertyOverView propertyFrag = new PropertyOverView();
        Bundle args = new Bundle();
        args.putString(ARG_ADDRESS, address);
        args.putString(ARG_DESCRIPTION , description);
        args.putString(ARG_DAFT_URL , dafturl);
        args.putString(ARG_MEDIUM_IMAGE , mediumImage);
        args.putString(ARG_LARGE_IMAGE , largeImage);
        args.putBoolean(ARG_LARGESCREEN, largeScreen);
        args.putInt(ARG_POSITION, position);
        args.putParcelable(ARG_PROPERTY, property);
        propertyFrag.setArguments(args);
        return propertyFrag;
    }// end PropertyDialogFragment constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddress = getArguments().getString(ARG_ADDRESS);
        mDescription = getArguments().getString(ARG_DESCRIPTION);
        mDafturl = getArguments().getString(ARG_DAFT_URL);
        mMediumImage = getArguments().getString(ARG_MEDIUM_IMAGE);
        mLargeImage = getArguments().getString(ARG_LARGE_IMAGE);
        mPropertyPosition = getArguments().getInt(ARG_POSITION);
        bLargeScreen = getArguments().getBoolean(ARG_LARGESCREEN);
        adsLists = getArguments().getParcelable(ARG_PROPERTY);

        //   setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHandler = new Handler();
        View customView = inflater.inflate(R.layout.discription_dialog, container, false);

        vWebView = inflater.inflate(R.layout.webview_layout, container, false);

        addressView = (TextView) customView.findViewById(R.id.address_text);

        messageView = (TextView) customView.findViewById(R.id.description_text);

        return customView;
    }// end onCreateView
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
      inflater.inflate(R.menu.property_menu, menu);
      super.onCreateOptionsMenu(menu, inflater);
    }// onCreateOptionsMenu
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
      // handle item selection
      switch (item.getItemId()) {
        case R.id.action_view_image:
          viewFullImagePoster(mLargeImage, mPropertyPosition, bLargeScreen, adsLists);
         return true;
		case R.id.action_property_online:
          visitDaftPage(mDafturl);
         return true;
        default:
         return super.onOptionsItemSelected(item);
      }
    }

    @Override
    public void onStart() {
      super.onStart();
      Bundle args = getArguments();
      if(args != null) {
        updateOverView(mAddress = args.getString(ARG_ADDRESS), mDescription = args.getString(ARG_DESCRIPTION));
      }else if (mPropertyPosition != -1) {
       // Set article based on saved instance state defined during onCreateView
        updateOverView(mAddress, mDescription);
      }
    }// end onStart

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mPropertyPosition);
        outState.putString(ARG_ADDRESS, mAddress);
        outState.putString(ARG_DESCRIPTION, mDescription);
        outState.putString(ARG_DAFT_URL, mDafturl);
        outState.putString(ARG_MEDIUM_IMAGE, mMediumImage);
        outState.putString(ARG_LARGE_IMAGE, mLargeImage);
    } // end onSaveInstanceState

    // If description is available, text will be displayed else a message is displayed
    public void updateOverView(final String address, final String description) {
      mHandler.post(new Runnable() {
        public void run() {
          addressView.setText(address);
          if(Utils.isNotMissing(description)){
            messageView.setText(description);
          }else{
            String sNoOverview = getActivity().getString(R.string.no_description);
            messageView.setText(sNoOverview);
          }
          try{
              addressView.setSelected(true);
             }catch(OutOfMemoryError e){
                e.printStackTrace();
             }
        } // end runnable
      }); //end mHandler
} // end updateOverView

    public int getShownIndex() {
      return getArguments().getInt(ARG_POSITION, 0);
    } // end getShownIndex()
    /**
     * Called by Property Overview fragment to view a full Property image
     */
    public void viewFullImagePoster(String imageUrl, int position, boolean largeScreen, Property ads) {
      DialogFragment imageFragment = ImageDialogFragment.newInstance(position, imageUrl, largeScreen, ads);
      imageFragment.show(getFragmentManager(),  "dialog");
    }// end viewFullImagePoster


    public void visitDaftPage(String mDafturl) {
      String noDaftId = "No Daft.ie";
      final String daftMessage = getString(R.string.no_daft_available);
      if(Utils.isNotMissing(mDafturl)) {
        WebView myWebView = (WebView) vWebView.findViewById(R.id.webview);
        WebSettings mWebSettings = myWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(true);
        myWebView.loadUrl(mDafturl);
      }else{
        noDaftDialog(noDaftId, daftMessage);
      }
    }// end visitDaftPage method

    public void noDaftDialog(String nodaft, String daftMessage) {
        DialogFragment imageAlertDialog = ImageAlertDialog.newInstance(nodaft, daftMessage);
        imageAlertDialog.show(getFragmentManager(), "dialog");
    }
}// end class Propertyoverview