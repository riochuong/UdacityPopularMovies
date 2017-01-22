package movies.popular.jd.com.udacitypopularmovies.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import movies.popular.jd.com.udacitypopularmovies.tasks.MovieDetailLoaderInfo;

/**
 * Created by chuondao on 1/21/17.
 */

public class StorageHelper {

   // private static final
    private static final String TAG = "STORAGE_HELPER";
    private static final String MOVIE_DISPLAY_IMG_FOLDER = "display";
    private static final String MOVIE_TRAILER_IMG_FOLDER = "trailer";
    private  static final String PNG_FORMAT =".png";

    public static final int TRAILER_IMG = 1;
    private static final int NO_LOSS = 100;
    public static final int DISP_IMG = 0;


    /**
     * get the path to save the disp. img
     * @param movieId
     * @param ctx
     * @return
     */
    public static String getPathToStoreFavImg(String movieId, Context ctx, int type ){

        String root = (type == DISP_IMG) ? MOVIE_DISPLAY_IMG_FOLDER : MOVIE_TRAILER_IMG_FOLDER;
        File file = ctx.getDir(root, Context.MODE_PRIVATE);
        File finalPath = new File(file, movieId+PNG_FORMAT);
        if (finalPath.exists()){
            return null;
        }
        return file.getAbsolutePath()+"/"+movieId+PNG_FORMAT;
    }


    /**
     * path to load either trailer or disp. img
     * @param movieId
     * @param ctx
     * @param type
     * @return
     */
    public static File getPathToLoadFavImg(String movieId, Context ctx, int type ){
        String root = (type == DISP_IMG) ? MOVIE_DISPLAY_IMG_FOLDER : MOVIE_TRAILER_IMG_FOLDER;
        File file = ctx.getDir(root, Context.MODE_PRIVATE);
        File finalPath = new File(file, movieId+PNG_FORMAT);
        // return if path exists
        if (finalPath.exists()){
            return finalPath;
        }
        return null;
    }

    public static void storeBitmapToInternal (final String path, final Bitmap bitmap){

        // if bitmap is not available then do nothing
        if (bitmap != null){
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(new File(path));
                        bitmap.compress(Bitmap.CompressFormat.PNG, NO_LOSS, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute();
        }

    }

    /**
     * serialize data to store in db
     * @param l1
     * @param l2
     * @return
     */
    public static Pair<String,String>
    serializeMovieInfo(MovieDetailLoaderInfo mvInfo){
        String reviewJson = new Gson().toJson(mvInfo.getmReviewList());
        String trailerJson = new Gson().toJson(mvInfo.getmTrailerList());
        return new Pair(trailerJson,reviewJson);
    }


    public static List<MovieReview> getMovieReviewFromJson(String orig){
        Type type = new TypeToken<List<MovieReview>>(){}.getType();
        List<MovieReview> listObjs = new Gson().fromJson(orig, type);
        return listObjs;
    }

    public static List<MovieTrailer> getMovieTrailerFromJson(String orig){
        Type type = new TypeToken<List<MovieTrailer>>(){}.getType();
        List<MovieTrailer> listObjs = new Gson().fromJson(orig, type);
        return listObjs;
    }

}
