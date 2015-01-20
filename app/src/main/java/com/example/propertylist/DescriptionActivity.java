package com.example.propertylist;

/*
 * Copyright (C) 2014 Daft.ie and The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import com.example.propertylist.view.PropertyOverView;

public class DescriptionActivity extends FragmentActivity {
    private static final int minScreenwidth = 320;
    private static final int minScreenHeight = 480;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_portrait_layout);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mScreenWidth = metrics.widthPixels;
        int mScreenHeight = metrics.heightPixels;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // description dialog in-line with the list so we don't need this activity.
            if(mScreenWidth > minScreenwidth && mScreenHeight > minScreenHeight){
                finish();
                return;
            }
        }

        if(savedInstanceState == null) {
            // During initial setup, plug in the description fragment.
            PropertyOverView currentOverview = new PropertyOverView();
            currentOverview.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.portrait_overview,
                    currentOverview).commit();
        }
    }

}// end OverviewActivity inner class