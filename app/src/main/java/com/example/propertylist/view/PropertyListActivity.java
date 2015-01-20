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
import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.propertylist.DescriptionActivity;
import com.example.propertylist.PropertyInterface;
import com.example.propertylist.R;
import com.example.propertylist.model.Property;
import com.example.propertylist.adapter.PropertyAdapter;


public class PropertyListActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    @Override
      protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    // This fragment displays the list in potrait mode if screen is too small
    // for a split pane which will display both list and property description
    public static class PropertyList extends ListFragment implements PropertyInterface {
        private int mCurCheckPosition = 0;
        private static String mFullAddress = null;
        private static String mDescription = null;
        private static String mDaftUrl = null;
        private static String mLargeImageUrl = null;
        private static String mMediumImageUrl = null;
        private static List<Property> adsList = null;
        private static PropertyAdapter propertyAdapter = null;
        private static Property mProperty = null;
        private static boolean bLargeScreen = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          inflater.inflate(R.layout.property_layout, container, false);
          return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
          super.onActivityCreated(savedInstanceState);
          bLargeScreen = getActivity().getIntent().getBooleanExtra(ARG_LARGESCREEN, bLargeScreen);

          loadAdsAdapter();

          setListAdapter(propertyAdapter);
          propertyAdapter.notifyDataSetChanged();

          boolean dualPane = isItDualPain();

          if(savedInstanceState != null) {
             // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt(ARG_POSITION, 0);
          }

          if(dualPane){
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mProperty = propertyAdapter.getItem(mCurCheckPosition);
            overviewSelected(mCurCheckPosition, mProperty, bLargeScreen);
          }

        }// end onActivityCreated

        //Load the custom list adapter
        public void loadAdsAdapter(){
          adsList = new ArrayList<>();
          propertyAdapter = new PropertyAdapter(getActivity(), R.layout.sales_ad_data_rows, adsList);
          adsList = getActivity().getIntent().getParcelableArrayListExtra(ARG_PROPERTY);

          if(adsList != null && !adsList.isEmpty()) {
            propertyAdapter.clear();
            propertyAdapter.notifyDataSetChanged();

            for(Property property : adsList){
               propertyAdapter.add(property);
            }
          }
        }// end loadAdsAdapter

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(ARG_POSITION, mCurCheckPosition);
            outState.putString(ARG_ADDRESS, mFullAddress);
            outState.putString(ARG_DESCRIPTION, mDescription);
            super.onSaveInstanceState(outState);
        }

        /**
         * Called by PropertyList when a list item is selected
         *    /**
         * Reads the sample JSON data and loads it into a table.
         *  @param l ListView rows
         *  @param v View
         *  @param position of element in the list
         */

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            final Property ads = propertyAdapter.getItem(position);
            overviewSelected(position, ads, bLargeScreen);

        }// end onListItemClick method

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        public void overviewSelected(int position, Property property, boolean largeScreen) {
            mCurCheckPosition = position;


            if(propertyAdapter.getCount() > 0){
                mFullAddress = property.getFullAddress();
                mDescription = property.getDescription();
                mDaftUrl = property.getDaftPropertyUrl();
                mMediumImageUrl = property.getMediumThumbnailUrl();
                mLargeImageUrl = property.getLargeThumbnailUrl();
            }

            boolean dualPane = isItDualPain();

            if(dualPane){
              getListView().setItemChecked(mCurCheckPosition, true);

              PropertyOverView currentOverview = (PropertyOverView)
                getActivity().getFragmentManager().findFragmentById(R.id.overview);
              if(currentOverview == null || currentOverview.getShownIndex() != mCurCheckPosition) {

                currentOverview = PropertyOverView.newInstance
                    (mFullAddress, mDescription, mDaftUrl, mMediumImageUrl, mLargeImageUrl, mCurCheckPosition,
                    largeScreen, property);

                FragmentTransaction ft = getFragmentManager().beginTransaction();

                if(mCurCheckPosition == 0){
                  ft.replace(R.id.overview, currentOverview);
                }else{
                  ft.replace(R.id.overview, currentOverview);
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
              }
            }else{
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected property.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DescriptionActivity.class);
                intent.putExtra(ARG_POSITION, mCurCheckPosition);
                intent.putExtra(ARG_ADDRESS, mFullAddress);
                intent.putExtra(ARG_DESCRIPTION, mDescription);
                intent.putExtra(ARG_DAFT_URL, mDaftUrl);
                intent.putExtra(ARG_MEDIUM_IMAGE, mMediumImageUrl);
                intent.putExtra(ARG_LARGE_IMAGE, mLargeImageUrl);
                intent.putExtra(ARG_LARGESCREEN, largeScreen);
                intent.putExtra(ARG_PROPERTY, property);
                startActivity(intent);
            }
        }

        private boolean isItDualPain(){
          if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            View overviewFrame = getActivity().findViewById(R.id.overview);
            return overviewFrame != null && overviewFrame.getVisibility() == View.VISIBLE;
          }
          return false;
        }

    }// end class propertyList

}// end main PropertyListActivity class