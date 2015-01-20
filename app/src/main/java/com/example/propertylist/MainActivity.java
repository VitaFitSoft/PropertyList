package com.example.propertylist;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.propertylist.model.Property;
import com.example.propertylist.controller.AppController;
import com.example.propertylist.handler.JsonPropertyHandler;
import com.example.propertylist.view.PropertyListActivity;

public class MainActivity extends FragmentActivity {
    private static boolean largeScreen = false;
    private static final int minScreenwidth = 320;
    private static final int minLandScreenwidth = 480;
    private static ProgressDialog pDialog;
    private static final String PLEASE_WAIT = "Please wait...";
    private static final String RETRIEVING = "Retrieving data...";
    public static List<Property> propertyList;
    private static Handler handler;
    private static String api_key;
    private static final String PREFS_NAME = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sales_list);

        ImageView mImageView = new ImageView(this);

        DisplayMetrics metrics;
        int mScreenWidth;
        int mScreenHeight;

        api_key = getResources().getString(R.string.key);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        if(mScreenWidth >= minScreenwidth){
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                if(mScreenWidth  >= minLandScreenwidth) {
                    largeScreen = true;

                }
            }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                if(mScreenHeight > minLandScreenwidth) {
                    largeScreen = true;
                }
            }
        }else{
          largeScreen = false;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.daft_logo_image, options);

        mImageView.setImageBitmap( // ensure image size is 100 x 100.
                decodeSampledBitmapFromResource(getResources(), R.id.daft_logo_image, 100, 100)
        );

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hideProgressDialog();
                startListActivity();
            }
        };
    }// end onCreate

    //Downscale a large image to an appropiate size for the device and avoid  "out Of Memory Errors"
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    } // end calculateInSampleSize method

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    } // end decodeSampledBitmapFromResource method

    // invoked when button pressed.
    public void getPropertySalesList(View view) {
        showProgressDialog();
        Thread downloadThread = new DownLoadThread();
        downloadThread.start();
    }

    public static class DownLoadThread extends Thread {
        @Override
        public void run() {
            makeJsonObjReq(api_key);
            try{
                new Thread().sleep(3000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Updates the user interface
            handler.sendEmptyMessage(0);
        }
    }

    private static void makeJsonObjReq(String api_key) {
      JsonObjectRequest jsonObjReq = new JsonObjectRequest(api_key, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          propertyList = new ArrayList<>();
          JsonPropertyHandler jsonPropertyHandler = new JsonPropertyHandler();
          String sResponse = response.toString();
          try {
               propertyList =  jsonPropertyHandler.loadPropertyData(sResponse);
              } catch (JSONException je) {
              je.printStackTrace();
              } catch (IOException ioe) {
                ioe.printStackTrace();
              }
        }
      }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
      VolleyLog.d(PREFS_NAME, "Error: " + error.getMessage());
       hideProgressDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void startListActivity(){
        Intent intent = new Intent(MainActivity.this, PropertyListActivity.class);
        intent.putParcelableArrayListExtra("PROPERTY", (ArrayList<? extends Parcelable>) propertyList);
        intent.putExtra("LARGESCREEN", largeScreen);
        startActivity(intent);
    }

    private void showProgressDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(PLEASE_WAIT + RETRIEVING);
        pDialog.setCancelable(true);
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private static void hideProgressDialog() {
        if(pDialog!=null) {
            try{
                pDialog.dismiss();
                pDialog = null;
            }catch(IllegalArgumentException e) {// nothing
                e.printStackTrace();
            }
        }
    }

}