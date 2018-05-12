package tanvir.crimelogger_ewu.HelperClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.ZoomListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import tanvir.crimelogger_ewu.R;

/**
 * Created by USER on 20-Nov-17.
 */

public class CustomSwipeAdapterForEditPost extends PagerAdapter {

    private ArrayList<String> images = new ArrayList<>();
    private LayoutInflater inflater;
    String defaultEmpTyImage;
    private Context context;
    private int num_pages;

    public CustomSwipeAdapterForEditPost(Context context, ArrayList<String> images, int num_pages,String defaultEmpTyImage) {
        this.context = context;
        this.num_pages = num_pages;
        this.images = images;
        this.defaultEmpTyImage = defaultEmpTyImage;
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
        container.removeView((RelativeLayout) object);
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
        final ProgressBar progressBar = item_view.findViewById(R.id.progressBarInSwipeLayout);
        assert item_view != null;
        final ImageView imageView = item_view.findViewById(R.id.image);

        Zoomy.Builder builder = new Zoomy.Builder((Activity) context).target(imageView).enableImmersiveMode(false).animateZooming(true).tapListener(new TapListener() {
            @Override
            public void onTap(View v) {

                final AlertDialog alertDialog;
                final View imageDeleteDialogView;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                imageDeleteDialogView = inflater.inflate(R.layout.delete_image, null);
                dialogBuilder.setView(imageDeleteDialogView);
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                Button yesBTN = imageDeleteDialogView.findViewById(R.id.yesBTN);
                Button noBTN = imageDeleteDialogView.findViewById(R.id.noBTN);
                yesBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent i = new Intent("positionTobeDeleted");
                        i.putExtra("positionTobeDeleted", position);
                        context.sendBroadcast(i);
                    }
                });

                noBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        }).zoomListener(new ZoomListener() {
            @Override
            public void onViewStartedZooming(View view) {
            }
            @Override
            public void onViewEndedZooming(View view) {
            }
        });

        builder.register();

        String url;
        String path = images.get(position);
        if (path.charAt(0) == '`') {
            String sbstrng = path.substring(1);
            if (images.get(0).contains(defaultEmpTyImage)) {
                Glide.with(context).load(R.drawable.empty_image).into(imageView);
            } else {
                Uri uri = Uri.parse(sbstrng);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.drawable.error_image);
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(context).load(uri).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        TastyToast.makeText(context, "Problem in Image retrieve in uri \n Please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
            }

        } else {
            url = "http://www.farhandroid.com/CrimeLogger/Script/UserPostPic/" + images.get(position) + ".jpg";

            RequestOptions options = new RequestOptions();
            options.signature(new ObjectKey(System.currentTimeMillis()));
            progressBar.setVisibility(View.VISIBLE);

            Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    TastyToast.makeText(context, "Problem in Image retrieve \n Please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    return false;
                }
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        }
        view.addView(item_view, 0);
        return item_view;
    }
}
