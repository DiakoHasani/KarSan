package ir.tdaapp.karsanjob.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import ir.tdaapp.karsanjob.Services.ISliderClick;
import ir.tdaapp.karsanjob.ViewModel.VM_SliderHome;

/**
 * Created by Diako on 8/27/2019.
 */

//این آداپتر برای اسلایدر صفحه اول برنامه می باشد
public class SlidersHomeAdapter extends PagerAdapter {

    List<VM_SliderHome> values;
    Context context;
    ISliderClick setSliderClick;

    public SlidersHomeAdapter(List<VM_SliderHome> values, Context context, ISliderClick setSliderClick) {
        this.values = values;
        this.context = context;
        this.setSliderClick = setSliderClick;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView = new ImageView(context);

        Glide.with(context)
                .asBitmap()
                .load(values.get(position).getImageUrl())
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        //Play with bitmap
                        super.setResource(resource);
                    }
                });
        container.addView(imageView, 0);

        imageView.setOnClickListener(view -> setSliderClick.onClickSlider(view, values.get(position)));

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
