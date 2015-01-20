package com.example.propertylist.handler;

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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;

import com.example.propertylist.model.Property;

public class JsonPropertyHandler implements Parcelable{

    private List<Property> propertyList;


    public JsonPropertyHandler() {
    }

    public JsonPropertyHandler(Parcel in) {
        propertyList = new ArrayList<Property>();
        readFromParcel(in);
    }

    public static final Creator CREATOR =  new Creator() {
      public JsonPropertyHandler createFromParcel(Parcel in) {
        return new JsonPropertyHandler(in);
      }
      public JsonPropertyHandler[] newArray(int size) {
        return new JsonPropertyHandler[size];
      }
    };

    /**
     * Reads the sample JSON data and loads it into a table.
     *  @param sResponse returned internet response data
     * @throws org.json.JSONException
     */
    public List<Property> loadPropertyData(String sResponse)
            throws JSONException, IOException{
      InputStream stream = new ByteArrayInputStream(sResponse.getBytes("UTF-8"));
      propertyList = new ArrayList<Property>();
      InputStreamReader inputStreamReader;
      BufferedReader bufferedReader;
      JsonReader reader;
      inputStreamReader  = new InputStreamReader(stream, "UTF-8");
      bufferedReader = new BufferedReader(inputStreamReader);
      reader = new JsonReader(bufferedReader);
      propertyList = populatePropertySales(reader);
      reader.close();
      return  propertyList;
    }

    /**
     * Populates property data with the JSON data.
     *
     * @param reader
     *             the {@link android.util.JsonReader} used to read in the data
     * @return
     *          the propertyResults
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public List<Property> populatePropertySales(JsonReader reader)
            throws JSONException,IOException {
        //   Property property = new Property();
        List<Property> propertyResults = new ArrayList<Property>();

        reader.beginObject();
        while( reader.hasNext() ){
            final String name = reader.nextName();
            final boolean isNull = reader.peek() == JsonToken.NULL;
            if( name.equals("result")&& !isNull) {
                propertyResults = populateResultsSales(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return propertyResults;
    }

    /**
     * Populates property data with the JSON data.
     *
     * @param reader
     *            the {@link android.util.JsonReader} used to read in the data
     * @return
     *              the propertyResults
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public List<Property> populateResultsSales(JsonReader reader)
            throws JSONException,IOException {
        List<Property> propertyResults = new ArrayList<Property>();

        reader.beginObject();
        while( reader.hasNext() ){
            final String name = reader.nextName();
            final boolean isNull = reader.peek() == JsonToken.NULL;
            if( name.equals("results")&& !isNull) {
                propertyResults = populateAdsSales(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return propertyResults;
    }


    /**
     * Populates properties data with the JSON data.
     *
     * @param reader
     *            the {@link android.util.JsonReader} used to read in the data
     * @return
     *         the propertyAds
     * @throws org.json.JSONException
     * @throws java.io.IOException
     */
    public List<Property> populateAdsSales(JsonReader reader)
            throws JSONException,IOException {
        List<Property> propertyAds = new ArrayList<Property>();
        reader.beginObject();
        while( reader.hasNext() ){
            final String name = reader.nextName();
            final boolean isNull = reader.peek() == JsonToken.NULL;
            if( name.equals("ads")&& !isNull) {
                propertyAds = parseDataArray(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return propertyAds;
    }

    /**
     * Parses an array in the JSON data stream.
     *
     * @param reader
     *            the {@link android.util.JsonReader} used to read in the data
     * @return
     *           the propertySalesPopulated
     * @throws java.io.IOException
     */

    public List<Property> parseDataArray(JsonReader reader) throws IOException {
        List<Property> propertySalesPopulated = new ArrayList<Property>();
        Property allProperty;
        reader.beginArray();
        reader.peek();
        while(reader.hasNext()) {
            allProperty = parseSalesData(reader);
            if((allProperty.getFullAddress() != null  && allProperty.mFullAddress.length() > 0) &&
                    (allProperty.getThumbnailUrl() != null  && allProperty.mThumbnailUrl.length() > 0)){
                propertySalesPopulated.add(allProperty);
            }
            reader.peek();
        }
        reader.endArray();
        return propertySalesPopulated;
    }

    /**
     * Loads the next observation into the property class.
     *
     * @param reader
     *            the {@link android.util.JsonReader} containing the observation
     * @throws java.io.IOException
     */
    private Property parseSalesData(JsonReader reader) throws IOException {
        Property property = new Property();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("full_address") && reader.peek() != JsonToken.NULL){
                property.setFullAddress(reader.nextString());
            }else if(name.equals("daft_url") && reader.peek() != JsonToken.NULL){
                property.setDaftPropertyUrl(reader.nextString());
            }else if(name.equals("description") && reader.peek() != JsonToken.NULL){
                property.setDescription(reader.nextString());
            }else if(name.equals("small_thumbnail_url") && reader.peek() != JsonToken.NULL){
                property.setThumbnailUrl(reader.nextString());
            }else if(name.equals("medium_thumbnail_url") && reader.peek() != JsonToken.NULL){
                property.setMediumThumbnailUrl(reader.nextString());
            }else if(name.equals("large_thumbnail_url") && reader.peek() != JsonToken.NULL){
                property.setLargeThumbnailUrl(reader.nextString());
            }else{
                reader.skipValue();
            }// end if hasnext
        }// end while
        reader.endObject();
        return property;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(propertyList);

    }

    private void readFromParcel(Parcel in) {
      in.readTypedList(propertyList, Property.CREATOR);
    }

}