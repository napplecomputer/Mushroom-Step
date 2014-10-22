package co.natsuhi.mushroomstep;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

/**
 * SimpleCursorAdapterにDrawableで画像を設定できるようにしたやつです
 */
public class ShortcutListAdapter extends SimpleCursorAdapter {
    private final PackageManager mPackageManager;
    private final ImageGenerator mImageGenerator;
    private final int mImageTo;

    public interface ImageGenerator {
        public Drawable generateImage(Cursor cursor, PackageManager packageManager);
    }

    public ShortcutListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int imageTo, ImageGenerator imageGenerator, int flags) {
        super(context, layout, c, from, to, flags);
        mPackageManager = context.getPackageManager();
        mImageTo = imageTo;
        mImageGenerator = imageGenerator;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        final View imageView = view.findViewById(mImageTo);
        if (imageView == null || !(imageView instanceof ImageView)) {
            return;
        }

        Drawable drawable = mImageGenerator.generateImage(cursor, mPackageManager);
        if (drawable == null) {
            return;
        }

        ((ImageView) imageView).setImageDrawable(drawable);
    }
}
