package tanvir.crimelogger_aust.HelperClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.ZoomListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import tanvir.crimelogger_aust.R;

/**
 * Created by USER on 20-Nov-17.
 */

public class CustomSwipeAdapterForPushNotification extends PagerAdapter {

    private ArrayList<String> images = new ArrayList<>();
    private LayoutInflater inflater;
    KProgressHUD hud;
    private Context context;
    private int num_pages;


    public CustomSwipeAdapterForPushNotification(Context context, ArrayList<String> images, int num_pages, KProgressHUD hud) {
        this.context = context;
        this.num_pages = num_pages;
        this.images = images;
        this.hud = hud;


    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);


    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup view, final int position) {


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item_view = inflater.inflate(R.layout.swipelayout, view, false);


        assert item_view != null;
        final ImageView imageView = item_view
                .findViewById(R.id.image);


        Zoomy.Builder builder = new Zoomy.Builder((Activity) context)
                .target(imageView)
                .enableImmersiveMode(false)
                .animateZooming(true)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {

                    }
                })
                .zoomListener(new ZoomListener() {
                    @Override
                    public void onViewStartedZooming(View view) {

                    }

                    @Override
                    public void onViewEndedZooming(View view) {

                    }
                });

        builder.register();

        String url = "http://www.farhandroid.com/CrimeLogger/Script/pushNotificationImage/" + images.get(position) + ".jpg";

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.error_image);

        hud.show();


        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        hud.dismiss();
                        TastyToast.makeText(context, "Problem in Image retrieve \n Please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        hud.dismiss();
                        return false;
                    }
                })
                .into(imageView);


        view.addView(item_view, 0);
        return item_view;
    }
}
