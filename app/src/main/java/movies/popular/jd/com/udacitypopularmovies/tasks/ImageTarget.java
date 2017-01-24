package movies.popular.jd.com.udacitypopularmovies.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import movies.popular.jd.com.udacitypopularmovies.util.StorageHelper;

/**
 * Helper classes to help store image and loaded it into Image view also
 */

public class ImageTarget implements Target {

    Context mContext;

    // view to load bitmap into
    ImageView mView;

    int mChoiceToStore; // either DISP_IMAG or TRAILER_IMG

    String mMovieId;

    public ImageTarget(int mChoiceToStore,
                       Context mContext,
                       String mMovieId,
                       ImageView mView) {
        this.mChoiceToStore = mChoiceToStore;
        this.mContext = mContext;
        this.mMovieId = mMovieId;
        this.mView = mView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        String fileToStore = StorageHelper.getPathToStoreFavImg(mMovieId, mContext,mChoiceToStore);
        StorageHelper.storeBitmapToInternal(fileToStore, bitmap);
        mView.setImageBitmap(bitmap);

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
