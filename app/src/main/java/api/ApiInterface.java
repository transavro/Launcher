package api;

import model.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiInterface {
    @Headers({"Accept-Version: 1.0.0"})
    @GET("cats")
    Call<MovieResponse> getHomeScreenData();
}