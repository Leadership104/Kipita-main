package com.mytum.proectUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mytum.MyApplication;
import com.mytum.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProjectUtil {

    //    public static final Double PLACES_SEARCH_RADIUS = Double.valueOf(24140);
//    public static final int PLACES_SEARCH_MAX_RESULT = 20;
//    public static final String PLACES_SEARCH_SORT_BY = "DISTANCE";
    public static final String LOTITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CURRENT_LOTITUDE = "current_latitude";
    public static final String CURRENT_LONGITUDE = "current_longitude";
    public static final String LOCATIONNAME = "locationName";
    public static final String RADIUS = "radius";
    public static final String SESSION = "session";
    public static final String FIRSTNAME = "FIRSTNAME";
    public static final String USERID = "USERID";
    public static final String LASTNAME = "LASTANME";
    public static final String SIRNAME = "SIRNAME";
    public static final String ZIPCODE = "ZIPCODE";
    public static final String MOBILEUMBER = "MOBILEUMBER";
    public static final String PHOTOPATH = "PHOTOPATH";
    public static final String PHOTOMETADTA = "PHOTOMETADTA";
    public static final String DATETIMEMETADTA = "DATETIMEMETADTA";
    public static final String MIALID = "MIALID";
    public static final String ZIP_CODE = "zipcode";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String COUNTRY_NAME = "countryName";
    public static final String IS_TEMP_IN_FAHRENHEIT = "tempInFarh";
    public static final String IS_DISTANCE_IN_MILES = "distanceInMiles";
    public static final int RESOPNSECODE_OK = 200;
    public static final int RESOPNSECODE_ERROR = 500;
    public static final int RESOPNSECODE_UNAUTH = 401;
    public static final int RESOPNSECODE_NOCONTENT = 204;
    public static final String PUBLIC_PRIVATE = "PUBLIC_PRIVATE";
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.0");


    public static String getRadius(Context context) {
        return context.getSharedPreferences(RADIUS, 0).getString(RADIUS, "15");
    }

    public static void setRadius(Context context, String terminal_id) {
        context.getSharedPreferences(RADIUS, 0).edit().putString(RADIUS, terminal_id).commit();
    }


    public static String getLocationName(Context context) {
        return context.getSharedPreferences(LOCATIONNAME, 0).getString(LOCATIONNAME, "");
    }

    public static void setLocationName(Context context, String terminal_id) {
        context.getSharedPreferences(LOCATIONNAME, 0).edit().putString(LOCATIONNAME, terminal_id).commit();
    }


    public static String getLongitude(Context context) {
        return context.getSharedPreferences(LONGITUDE, 0).getString(LONGITUDE, "");
    }

    public static void setLongitude(Context context, String terminal_id) {
        context.getSharedPreferences(LONGITUDE, 0).edit().putString(LONGITUDE, terminal_id).commit();
    }

    public static String getLotitude(Context context) {
        return context.getSharedPreferences(LOTITUDE, 0).getString(LOTITUDE, "");
    }

    public static void setLotitude(Context context, String terminal_id) {
        context.getSharedPreferences(LOTITUDE, 0).edit().putString(LOTITUDE, terminal_id).commit();
    }

    public static String getCurrentLongitude(Context context) {
        return context.getSharedPreferences(CURRENT_LONGITUDE, 0).getString(LONGITUDE, "");
    }

    public static void setCurrentLongitude(Context context, String terminal_id) {
        context.getSharedPreferences(CURRENT_LONGITUDE, 0).edit().putString(LONGITUDE, terminal_id).commit();
    }

    public static String getCurrentLotitude(Context context) {
        return context.getSharedPreferences(CURRENT_LOTITUDE, 0).getString(LOTITUDE, "");
    }

    public static void setCurrentLotitude(Context context, String terminal_id) {
        context.getSharedPreferences(CURRENT_LOTITUDE, 0).edit().putString(LOTITUDE, terminal_id).commit();
    }

    public static void setFirstName(Context context, String terminal_id) {
        context.getSharedPreferences(FIRSTNAME, 0).edit().putString(FIRSTNAME, terminal_id).commit();
    }

    public static String getFirstName(Context context) {
        return context.getSharedPreferences(FIRSTNAME, 0).getString(FIRSTNAME, "");
    }

    public static void setUserId(Context context, String terminal_id) {
        context.getSharedPreferences(USERID, 0).edit().putString(USERID, terminal_id).commit();
    }

    public static String getUserId(Context context) {
        return context.getSharedPreferences(USERID, 0).getString(USERID, "0");
    }

    public static void setLasttName(Context context, String terminal_id) {
        context.getSharedPreferences(LASTNAME, 0).edit().putString(LASTNAME, terminal_id).commit();
    }


    public static String getSirName(Context context) {
        return context.getSharedPreferences(SIRNAME, 0).getString(SIRNAME, "");
    }

    public static void setSirName(Context context, String terminal_id) {
        context.getSharedPreferences(SIRNAME, 0).edit().putString(SIRNAME, terminal_id).commit();
    }

    public static String getLastName(Context context) {
        return context.getSharedPreferences(LASTNAME, 0).getString(LASTNAME, "");
    }

    public static void setZipCode(Context context, String terminal_id) {
        context.getSharedPreferences(ZIPCODE, 0).edit().putString(ZIPCODE, terminal_id).commit();
    }

    public static String getZiCode(Context context) {
        return context.getSharedPreferences(ZIPCODE, 0).getString(ZIPCODE, "");
    }


    public static void setMailId(Context context, String terminal_id) {
        context.getSharedPreferences(MOBILEUMBER, 0).edit().putString(MOBILEUMBER, terminal_id).commit();
    }

    public static String getMailId(Context context) {
        return context.getSharedPreferences(MOBILEUMBER, 0).getString(MOBILEUMBER, "");
    }


    public static void setPhotoPath(Context context, String terminal_id) {
        context.getSharedPreferences(PHOTOPATH, 0).edit().putString(PHOTOPATH, terminal_id).commit();
    }

    public static String getPhotoPath(Context context) {
        return context.getSharedPreferences(PHOTOPATH, 0).getString(PHOTOPATH, "");
    }


    public static void setPhotoMetaData(Context context, String terminal_id) {
        context.getSharedPreferences(PHOTOMETADTA, 0).edit().putString(PHOTOMETADTA, terminal_id).commit();
    }

    public static String getPhotoMetaData(Context context) {
        return context.getSharedPreferences(PHOTOMETADTA, 0).getString(PHOTOMETADTA, "");
    }

    public static void setPhotoDateTimeData(Context context, String terminal_id) {
        context.getSharedPreferences(DATETIMEMETADTA, 0).edit().putString(DATETIMEMETADTA, terminal_id).commit();
    }

    public static String getPhotoDateTimeData(Context context) {
        return context.getSharedPreferences(DATETIMEMETADTA, 0).getString(DATETIMEMETADTA, "");
    }


    public static void setMobileNumber(Context context, String terminal_id) {
        context.getSharedPreferences(MIALID, 0).edit().putString(MIALID, terminal_id).commit();
    }

    public static String getMobileNumber(Context context) {
        return context.getSharedPreferences(MIALID, 0).getString(MIALID, "");
    }


    public static void setSession(Context context, String terminal_id) {
        context.getSharedPreferences(SESSION, 0).edit().putString(SESSION, terminal_id).commit();
    }

    public static String getSession(Context context) {
        return context.getSharedPreferences(SESSION, 0).getString(SESSION, "");
    }

    public static void setIsTempInFahrenheit(Context context, boolean isTempInCel) {
        context.getSharedPreferences(SESSION, 0).edit().putBoolean(IS_TEMP_IN_FAHRENHEIT, isTempInCel).commit();
    }

    public static boolean getIsTempInFahrenheit(Context context) {
        return context.getSharedPreferences(SESSION, 0).getBoolean(IS_TEMP_IN_FAHRENHEIT, false);
    }

    public static void setIsDistanceInMiles(Context context, boolean isDistanceInMiles) {
        context.getSharedPreferences(SESSION, 0).edit().putBoolean(IS_DISTANCE_IN_MILES, isDistanceInMiles).commit();
    }

    public static boolean getIsDistanceInMiles(Context context) {
        return context.getSharedPreferences(SESSION, 0).getBoolean(IS_DISTANCE_IN_MILES, false);
    }

    public static String changeTempInCelsiusFromFreh(double tempInFreh) {
        double temp = (tempInFreh - 32) * 5 / 9;
        return decimalFormat.format(temp);
    }

    public static String changeTempInFrehFromCelsius(double tempInCelsius) {
        double temp = (tempInCelsius * 9 / 5) + 32;
        return decimalFormat.format(temp);
    }

    public static String changeDistanceInMilesFromKm(double DistanceInKM) {
        double dist = DistanceInKM / 1.609;
        return decimalFormat.format(dist);
    }

    public static String changeDistanceInKmFromMiles(double DistanceInMiles) {
        double dist = DistanceInMiles * 1.609;
        return decimalFormat.format(dist);
    }

    public static String dateTimeToMillis(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        try {
            Date mDate = sdf.parse(dateTime);
            long timeInMilliseconds = mDate.getTime();
            return "" + timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "" + System.currentTimeMillis();
    }

    public static String cureentDate1() {
        Calendar c = Calendar.getInstance();
        PrintStream printStream = System.out;
        printStream.println("Current time => " + c.getTime());
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(c.getTime());
    }

    public static String cureentDate() {
        Calendar c = Calendar.getInstance();
        c.add(5, -15);
        PrintStream printStream = System.out;
        printStream.println("Current time => " + c.getTime());
        return new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
    }

    public static void customToast(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tost, (ViewGroup) null);
        ((TextView) view.findViewById(R.id.txt_message)).setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(80, 0, 0);
        toast.setView(view);
        toast.show();
    }

    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static void showDialog(Dialog customDialog, String msg) {
        try {
            customDialog.setContentView(R.layout.custom_dialog);
            customDialog.setCanceledOnTouchOutside(false);
            TextView dilogmsg = customDialog.findViewById(R.id.dilogmsg);
            dilogmsg.setText(msg);
            customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customDialog.show();
        } catch (Exception e) {

        }
    }

    public static void dismissDialog(Dialog customDialog) {
        try {
            customDialog.setContentView(R.layout.custom_dialog);
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {

        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public static void getAddress(String latitude, String longitude, TextView text, Activity ctx) {
//        StringBuilder result = new StringBuilder();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
//                    List<Address> addresses = null;
//                    try {
//                        addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    assert addresses != null;
//                    if (addresses.get(0).getPostalCode() != null) {
//                        String address = addresses.get(0).getAddressLine(0);
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String zip = addresses.get(0).getPostalCode();
//                        String country = addresses.get(0).getCountryName();
//                        result.append(city).append(", ").append(state).append(", ").append(country).append(", ").append(zip);
//                    } else if (!addresses.isEmpty()) {
//                        String address = addresses.get(1).getAddressLine(0);
//                        String city = addresses.get(1).getLocality();
//                        String state = addresses.get(1).getAdminArea();
//                        String zip = addresses.get(1).getPostalCode();
//                        String country = addresses.get(1).getCountryName();
//                        result.append(city).append(", ").append(state).append(", ").append(country).append(", ").append(zip);
//                    }
//
//                    ctx.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            text.setText(result.toString());
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }
    @SuppressLint("SetTextI18n")
    public static void getAddress(String latitude, String longitude, TextView text, Activity ctx) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        1
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Try to get city in fallback order
                    String city = address.getLocality();
                    if (city == null) city = address.getSubAdminArea();
                    if (city == null) city = address.getSubLocality();
                    if (city == null) city = address.getFeatureName();
                    String state = address.getAdminArea();
                    String country = address.getCountryName();
                    String zip = address.getPostalCode();
                    StringBuilder result = new StringBuilder();
                    if (city != null) result.append(city);
                    if (state != null) result.append(", ").append(state);
                    if (country != null) result.append(", ").append(country);
                    if (zip != null) result.append(", ").append(zip);

                    String finalResult = result.toString();
                    ctx.runOnUiThread(() -> text.setText(finalResult));
                } else {
                    ctx.runOnUiThread(() -> text.setText("Address not found"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                ctx.runOnUiThread(() -> text.setText("Error getting address"));
            }
        }).start();
    }

    public static void setChangedLocationZipCode(Context context, String zipCode) {
        context.getSharedPreferences(ZIP_CODE, 0).edit().putString(ZIP_CODE, zipCode).commit();
    }

    public static String getChangedLocationZipCode(Context context) {
        return context.getSharedPreferences(ZIP_CODE, 0).getString(ZIP_CODE, "");
    }

    public static void setChangedLocationCountryCode(Context context, String zipCode) {
        context.getSharedPreferences(COUNTRY_CODE, 0).edit().putString(COUNTRY_CODE, zipCode).commit();
    }

    public static String getChangedLocationCountryCode(Context context) {
        return context.getSharedPreferences(COUNTRY_CODE, 0).getString(COUNTRY_CODE, "");
    }

    public static void setChangedLocationCountryName(Context context, String zipCode) {
        context.getSharedPreferences(COUNTRY_NAME, 0).edit().putString(COUNTRY_NAME, zipCode).commit();
    }

    public static String getChangedLocationCountryName(Context context) {
        return context.getSharedPreferences(COUNTRY_NAME, 0).getString(COUNTRY_NAME, "");
    }

    public static int getScreenWidth(Context ctx) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) ctx).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        return width;
    }

    public static int getDpiSize(Context ctx, int width) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int pixels = (int) (width * scale + 0.5f);

        return pixels;
    }

    public static void setPublicOrPrivate(Context context, boolean terminal_id) {
        context.getSharedPreferences(PUBLIC_PRIVATE, 0).edit().putBoolean(PUBLIC_PRIVATE, terminal_id).commit();
    }

    public static boolean getPublicOrPrivate(Context context) {
        return context.getSharedPreferences(PUBLIC_PRIVATE, 0).getBoolean(PUBLIC_PRIVATE, false);
    }

    public static String getDistanceInMileFromMeter(double distance) {
        double temp = (distance * (0.000621));
        String distanceString = String.format("%.2f", temp);
        return distanceString;
    }

    public static String getCurrentTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz.getID();
    }

    public static String getZipCode(Double latitude, Double longitude) {
        String zipcode = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            String addr = "";

            String city = "";
            String state = "";
            if (addresses != null && addresses.size() > 0) {
                addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();

                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        zipcode = address.getPostalCode();
                        break;
                    }
                }
            }

        } catch (Exception e) {
        }
        return zipcode;
    }

    public static String getCountryCode(Double latitude, Double longitude) {
        String countryCode = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            String addr = "";

            String city = "";
            String state = "";
            if (addresses != null && addresses.size() > 0) {
                addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();

                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        countryCode = address.getCountryCode();
                        break;
                    }
                }
            }

        } catch (Exception e) {
        }
        return countryCode;
    }

    public static String getCountryName(Double latitude, Double longitude) {
        String countryName = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            String addr = "";

            String city = "";
            String state = "";
            if (addresses != null && addresses.size() > 0) {
                addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();

                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        countryName = address.getCountryName();
                        break;
                    }
                }
            }

        } catch (Exception e) {
        }
        return countryName;
    }

    public static String getCityName(Double latitude, Double longitude) {
        String countryCity = "";
        try {
            Geocoder geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = null;
            String addr = "";

            String city = "";
            String state = "";
            if (addresses != null && !addresses.isEmpty()) {
                addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                    if (address.getPostalCode() != null) {
                        countryCity = address.getLocality();
                        break;
                    }
                }
            }

        } catch (Exception e) {
        }
        return countryCity;
    }

    public static String calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint = new Location("locationB");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

        double distance = startPoint.distanceTo(endPoint);
        return getDistanceInMileFromMeter(distance);
    }


}
