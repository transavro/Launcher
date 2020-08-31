package api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface ApiInterface {
    @Headers({"accept-version: 1.0.0"})
    @GET("cats")
    Call<ResponseBody> getHomeScreenData(@Header("kidsafe") Boolean kidsafe);
}