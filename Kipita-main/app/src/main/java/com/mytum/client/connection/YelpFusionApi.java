package com.mytum.client.connection;

//import com.dawata.client.models.AutoComplete;
//import com.dawata.client.models.Business;
//import com.dawata.client.models.Reviews;
//import com.dawata.client.models.SearchResponse;

import com.mytum.model.Application.ApplicationResponse;
import com.mytum.model.Application.CategoryRequest;
import com.mytum.model.CallResponse.CallResponse;
import com.mytum.model.ImageList.ImageListPuclicRequest;
import com.mytum.model.ImageList.ImageListRequest;
import com.mytum.model.ImageList.ImageListResponse;
import com.mytum.model.ImageList.ImageUploadRequest;
import com.mytum.model.ImageList.ImageUploadResponse;
import com.mytum.client.models.AdvisoryResponse;
import com.mytum.client.models.AutoComplete;
import com.mytum.client.models.Business;
import com.mytum.client.models.Reviews;
import com.mytum.client.models.SearchResponse;
import com.mytum.model.NewPlaceGoResponse.NewPlaceResponse;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Ranga on 2/22/2017.
 */

public interface YelpFusionApi {
    @GET("/v3/businesses/search")
    Call<SearchResponse> getBusinessSearch(@QueryMap Map<String, String> params);

    @GET("{fullUrl}")
    Call<NewPlaceResponse> getGoogleBussnies(@Path(value = "fullUrl", encoded = true) String fullUrl);


    @GET("{fullUrl}")
    Call<CallResponse> getMobileNumber(@Path(value = "fullUrl", encoded = true) String fullUrl);


    @GET("/v3/businesses/search/phone")
    Call<SearchResponse> getPhoneSearch(@Query("phone") String phone);

    @GET("/v3/transactions/{transaction_type}/search")
    Call<SearchResponse> getTransactionSearch(@Path("transaction_type") String transactionType, @QueryMap Map<String, String> params);

    @GET("/v3/businesses/{id}")
    Call<Business> getBusiness(@Path("id") String id);

    @GET("/v3/businesses/{id}/reviews")
    Call<Reviews> getBusinessReviews(@Path("id") String id, @Query("locale") String locale);

    @GET("/v3/autocomplete")
    Call<AutoComplete> getAutocomplete(@QueryMap Map<String, String> params);

    @GET("restrictions.php")
    Call<ArrayList<AdvisoryResponse>> getAdvisoryResponse();

    @POST("public_html/restrictions")
    Call<ApplicationResponse> getApplicationResponse(@Header("Content-Type") String content_type, @Body CategoryRequest verifyOTPRequest);


    @POST("public_html/imageSearch")
    Call<ImageListResponse> imageSearch(@Header("Content-Type") String content_type, @Body ImageListPuclicRequest imageListRequest);

    @POST("public_html/imageList")
    Call<ImageListResponse> imageList(@Header("Content-Type") String content_type, @Body ImageListRequest imageListRequest);

    @POST("public_html/imageUpload")
    Call<ImageUploadResponse> imageUpload(@Header("Content-Type") String content_type, @Body ImageUploadRequest imageUploadRequest);

    @POST("public_html/imageUploadGroup")
    Call<ImageUploadResponse> imageUploadGroup(@Header("Content-Type") String content_type, @Body ImageUploadRequest imageUploadRequest);


    @POST("public_html/imageUpload")
    Call<ImageListResponse> imageDelete(@Header("Content-Type") String content_type, @Body ImageUploadRequest imageUploadRequest);

    @POST("public_html/imageUpload")
    Call<ImageListResponse> imageEdit(@Header("Content-Type") String content_type, @Body ImageUploadRequest imageUploadRequest);

    @Multipart
    @POST("fileUpload.php")
    Call<ImageListResponse>  imageUploadMultiPart(@Header("Content-Type") String content_type, @Body ImageUploadRequest imageUploadRequest, @Part MultipartBody.Part file);

//    @POST("/restrictions")
//    Call<ImageListResponse> advisoryData(@Header("Content-Type") String content_type, @Body ImageListPuclicRequest imageListRequest);


}

