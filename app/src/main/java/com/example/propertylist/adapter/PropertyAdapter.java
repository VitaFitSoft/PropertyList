package com.example.propertylist.adapter;

import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.propertylist.R;
import com.example.propertylist.controller.AppController;
import com.example.propertylist.model.Property;


public class PropertyAdapter  extends ArrayAdapter<Property> {
    private List<Property> adsLists;
    private Context context;
    private int iResourceId;
    ImageLoader imageLoader;

    public PropertyAdapter(Context context, int textViewResourceId, List<Property> adsLists) {
      super(context, textViewResourceId, adsLists);
      this.context = context;
      this.adsLists = adsLists;
      this.iResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;
       Handler mHandler = new Handler();
       final AdsViewHolder adsHolder;

       if(view == null) {
         LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         view = vi.inflate(iResourceId, parent, false);
         adsHolder = new AdsViewHolder();

         if(imageLoader == null) {
           imageLoader = AppController.getInstance().getImageLoader();
         }

         //address
         adsHolder.addressTextView = (TextView) view.findViewById(R.id.full_address_text);

         // thumb image
         adsHolder.thumbNail = (NetworkImageView) view.findViewById(R.id.property_thumb_icon);

         view.setTag(adsHolder);
       }else {
         adsHolder = (AdsViewHolder) view.getTag();
       }

       final Property ads = adsLists.get(position);

       if(ads != null) {
         mHandler.post(new Runnable() {
           public void run() {
             adsHolder.thumbNail.setImageUrl(ads.getThumbnailUrl(), imageLoader);
             adsHolder.addressTextView.setText(ads.getFullAddress());
           } // end runnable
         }); // end handler
       }
       return view;
    }

    static class AdsViewHolder {
      TextView addressTextView;
      NetworkImageView thumbNail;
    }// end anonomous class PropertyViewHolder

}