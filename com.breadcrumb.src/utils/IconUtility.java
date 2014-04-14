package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.breadcrumb.R;

public class IconUtility {

	public static Drawable getIcon(int imageId, Context context){
    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageId); 
    	Drawable icon = new BitmapDrawable(context.getResources(),bitmap);
		icon.setColorFilter(Color.parseColor("#02798b"),Mode.MULTIPLY);
		return icon;
	}
}
