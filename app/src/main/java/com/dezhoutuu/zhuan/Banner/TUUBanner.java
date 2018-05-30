package com.dezhoutuu.zhuan.Banner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dezhoutuu.zhuan.Banner.View.BannerViewPager;
import com.dezhoutuu.zhuan.Banner.View.RoundAngleImageView;
import com.dezhoutuu.zhuan.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fatJiang on 2018/5/29.
 */

public class TUUBanner extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private BannerViewPager bannerViewPager;
    private RoundAngleImageView roundImageToLeft;
    private RoundAngleImageView roundImageToRight;
    private FrameLayout flBg;
//    private ImageView imBg;
    private List<View> myViews;
    private List<ImageView> bgImageViews;

    private List<BannerEntity> mBannerEntitys;
    private ViewPagerAdapter mViewPagerAdapter;
    private Context context;
    private int count = 0;
    private int currentItem;
    private AnimatorSet animatorSetFangda;


    private WeakHandler handler = new WeakHandler();
    private long delayTime = 3000;
    private boolean isAutoPlay = true;

    private int mMyDuration = 300;          //持续时间
    private FixedSpeedScroller mScroller;
    private boolean scrollOneTime = false;

    private BannerItemClickListener mBannerItemClickListener;

    //以下背景
    //手指按住
    private boolean touch = false;

    //手放下没有归为
    private boolean touchover = false;
    private int mViewPagerIndex = 0;
    public TUUBanner(Context context) {
        super(context);
        init(context);
    }

    public TUUBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TUUBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_banner, this, true);
        bannerViewPager = (BannerViewPager) view.findViewById(R.id.bannerViewPager);
        roundImageToLeft = (RoundAngleImageView) view.findViewById(R.id.roundImageToLeft);
        roundImageToRight = (RoundAngleImageView) view.findViewById(R.id.roundImageToRight);
//        imBg = (ImageView) view.findViewById(R.id.im_bg);
        flBg = (FrameLayout)view.findViewById(R.id.fl_bg);

        myViews = new ArrayList<>();
        bgImageViews = new ArrayList<>();
        roundImageToLeft.setVisibility(VISIBLE);
        roundImageToLeft.setLeftOrRight(false);
        roundImageToRight.setVisibility(INVISIBLE);
        roundImageToRight.setLeftOrRight(true);
    }

    public TUUBanner start() {
        setImageList(mBannerEntitys);
        setData();
        return this;
    }

    private void setImageList(List<BannerEntity> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            return;
        }
        for (int i = 0; i <= imagesUrl.size() + 1; i++) {
            int index = 0;
            if (i == 0) {
                index = imagesUrl.size() - 1;
            } else if (i == imagesUrl.size() + 1) {
                index = 0;
            } else {
                index = i - 1;
            }
            final int indexForClick = index;

            ImageView imageView = null;
            imageView = new ImageView(context);
            imageView.setPadding(50, 0, 50, 0);
            String url = imagesUrl.get(index).getImg();
//            if (i == 0) {
//                url = imagesUrl.get(imagesUrl.size() - 1).getImg();
//            } else if (i == imagesUrl.size() + 1) {
//                url = imagesUrl.get(0).getImg();
//            } else {
//                url = imagesUrl.get(i - 1).getImg();
//            }
            Glide.with(context.getApplicationContext()).load(url).into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBannerItemClickListener != null){
                        mBannerItemClickListener.clickItem(indexForClick);
                    }
                }
            });
            myViews.add(imageView);

            ImageView imageViewBg = new ImageView(context);
            String urlBg = imagesUrl.get(index).getBg();
//            if (i == 0) {
//                urlBg = imagesUrl.get(imagesUrl.size() - 1).getBg();
//            } else if (i == imagesUrl.size() + 1) {
//                urlBg = imagesUrl.get(0).getBg();
//            } else {
//                urlBg = imagesUrl.get(i - 1).getBg();
//            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
            imageViewBg.setLayoutParams(layoutParams);
            imageViewBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context.getApplicationContext()).load(urlBg).into(imageViewBg);
            flBg.addView(imageViewBg);
            bgImageViews.add(imageViewBg);
        }
    }

    private void setData() {
        currentItem = 1;
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new ViewPagerAdapter();
            bannerViewPager.addOnPageChangeListener(this);
        }
        bannerViewPager.setAdapter(mViewPagerAdapter);
        bannerViewPager.setFocusable(true);
        bannerViewPager.setCurrentItem(1);
        setSpeed();
        if (isAutoPlay)
            startAutoPlay();
    }

    private void setSpeed() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            //<span style="color:#ff0000;">设置加速度 ，通过改变FixedSpeedScroller这个类中的mDuration来改变动画时间（如mScroller.setmDuration(mMyDuration);）   </span>
            mScroller = new FixedSpeedScroller(bannerViewPager.getContext(), new AccelerateInterpolator());
            mField.set(bannerViewPager, mScroller);
            mScroller.setmDuration(mMyDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageUrls(List<BannerEntity> imageUrls) {
        this.mBannerEntitys = imageUrls;
        this.count = imageUrls.size();
    }


    private void showBg(int index){
//        bgImageViews.get(0).setVisibility(View.VISIBLE);
        for(int i = 0;i < bgImageViews.size();i++){
            if (i == index+1){
                bgImageViews.get(i).setVisibility(View.VISIBLE);
            }else {
                bgImageViews.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
//                roundImageView.setVisibility(VISIBLE);
//                roundImagetwo.setVisibility(INVISIBLE);
                touch = false;
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
//                touchover = true;
                scrollOneTime = true;
                mScroller.setmDuration(100);
                touch = true;
                suoxiaAll();
                stopAutoPlay();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //以下背景切换
        int num = 0;
        Log.e("onPageScrolled", "position: " + position + "   positionOffset:  " + positionOffset + "    positionOffsetPixels:" + positionOffsetPixels);
        if (positionOffset > 0) {

            if (touch) {
                if (currentItem == mBannerEntitys.size() + 1) {
                    Glide.with(context).load(mBannerEntitys.get(0).getBg()).into(roundImageToLeft);
                    Glide.with(context).load(mBannerEntitys.get(0).getBg()).into(roundImageToRight);
                } else {
                    if (currentItem == 0) {
                        Glide.with(context).load(mBannerEntitys.get(mBannerEntitys.size() - 1).getBg()).into(roundImageToLeft);
                        Glide.with(context).load(mBannerEntitys.get(mBannerEntitys.size() - 1).getBg()).into(roundImageToRight);
                    } else {
                        Glide.with(context).load(mBannerEntitys.get(currentItem - 1).getBg()).into(roundImageToLeft);
                        Glide.with(context).load(mBannerEntitys.get(currentItem - 1).getBg()).into(roundImageToRight);
                    }
                }
                if (position == mViewPagerIndex) {

                    if (position == mBannerEntitys.size()) {
                        num = 0;
                    } else {
                        num = position;
                    }
//                    Glide.with(context).load(mBannerEntitys.get(num).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                    showBg(num);
                    roundImageToLeft.setVisibility(VISIBLE);
                    roundImageToRight.setVisibility(INVISIBLE);

                } else {
                    if (position == 0) {
                        num = mBannerEntitys.size();
                    } else {
                        num = position;
                    }
//                    Glide.with(context).load(mBannerEntitys.get(num - 1).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                    showBg(num - 1);
                    roundImageToRight.setVisibility(VISIBLE);
                    roundImageToLeft.setVisibility(INVISIBLE);
                }
            } else {
                if (!touchover) {
                    if (position < currentItem) {
                        roundImageToLeft.setVisibility(VISIBLE);
                        roundImageToRight.setVisibility(INVISIBLE);
                    }
                }
//                if(position > 0 && position < mBannerEntitys.size()+1){
////                    Glide.with(context).load(mBannerEntitys.get(position - 1).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(roundImageToLeft);
//                    Glide.with(context).load(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).into(roundImageToRight);
//                    Glide.with(context).load(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).into(roundImageToLeft);
//                }
                if (position == mBannerEntitys.size()) {
//                    Glide.with(context).load(mBannerEntitys.get(0).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                    showBg(0);
                } else if (position == 0) {
                    num = mBannerEntitys.size();
//                    Glide.with(context).load(mBannerEntitys.get(num - 1).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                    showBg(num - 1);
                } else {
                    if (position == mViewPagerIndex) {
//                        Glide.with(context).load(mBannerEntitys.get(position).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                        showBg(position);
                    } else {
                        if (!touchover) {
//                            Glide.with(context).load(mBannerEntitys.get(position).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                            showBg(position);
                        } else {
//                            Glide.with(context).load(mBannerEntitys.get(position - 1).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
                            showBg(position - 1);
                            roundImageToLeft.setVisibility(INVISIBLE);
                            roundImageToRight.setVisibility(VISIBLE);
                        }
                    }
                }
            }
        } else {
            roundImageToRight.setVisibility(VISIBLE);
            roundImageToLeft.setVisibility(VISIBLE);

            if (currentItem == mBannerEntitys.size() + 1) {
                Glide.with(context).load(mBannerEntitys.get(0).getBg()).into(roundImageToLeft);
                Glide.with(context).load(mBannerEntitys.get(0).getBg()).into(roundImageToRight);
            } else {
                if (currentItem == 0) {
                    Glide.with(context).load(mBannerEntitys.get(mBannerEntitys.size() - 1).getBg()).into(roundImageToLeft);
                    Glide.with(context).load(mBannerEntitys.get(mBannerEntitys.size() - 1).getBg()).into(roundImageToRight);
                } else {
                    Glide.with(context).load(mBannerEntitys.get(currentItem - 1).getBg()).into(roundImageToLeft);
                    Glide.with(context).load(mBannerEntitys.get(currentItem - 1).getBg()).into(roundImageToRight);
                }
            }
//                if (position == count) {
//                    num = 0;
//                } else {
//                    num = position;
//                }
        }
        roundImageToRight.setRound(1f - positionOffset);
        roundImageToLeft.setRound(positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        if (scrollOneTime) {
            scrollOneTime = false;
            mScroller.setmDuration(mMyDuration);
        }
//        if (position > 0 && position < mBannerEntitys.size() + 1) {
//            Glide.with(context).load(mBannerEntitys.get(position - 1).getBg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imBg);
//        }
        currentItem = position;
        Log.e("position", "position:  " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 0:
                touchover = false;
                if (currentItem == 0) {
                    bannerViewPager.setCurrentItem(count, false);
                } else if (currentItem == count + 1) {
                    bannerViewPager.setCurrentItem(1, false);
                }
                break;
            case 1://start Sliding
                touchover = true;
                mViewPagerIndex = bannerViewPager.getCurrentItem();
                if(!touch){
                    suoxiaAll();
                }
                if (currentItem == count + 1) {
                    bannerViewPager.setCurrentItem(1, false);
                } else if (currentItem == 0) {
                    bannerViewPager.setCurrentItem(count, false);
                }
                break;
            case 2://end Sliding
                for (int i = 0; i < myViews.size(); i++) {
                    fada(myViews.get(i));
                }

                break;
        }
    }

    public void startAutoPlay() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }

    public void stopAutoPlay() {
        handler.removeCallbacks(task);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                for (int i = 0; i < myViews.size(); i++) {
                    if (i == 0) {
                        suoxia(myViews.get(i), new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                currentItem = currentItem % (count + 1) + 1;
//                Log.i(tag, "curr:" + currentItem + " count:" + count);
                                if (currentItem == 1) {
                                    bannerViewPager.setCurrentItem(currentItem, false);
                                    handler.post(task);
                                } else {
                                    bannerViewPager.setCurrentItem(currentItem);
                                    handler.postDelayed(task, delayTime);
                                }
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);

                            }
                        });
                    } else {
                        suoxia(myViews.get(i));
                    }
                }
            }
        }
    };

    private class ViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return myViews.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(myViews.get(position));
            return myViews.get(position);
        }

    }
    private void fada(View view, AnimatorListenerAdapter mAnimatorListenerAdapter) {
        animatorSetFangda = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f);
        animatorSetFangda.setStartDelay(300);
        animatorSetFangda.setDuration(300);
        animatorSetFangda.setInterpolator(new LinearInterpolator());
        animatorSetFangda.play(scaleX).with(scaleY);//两个动画同时开始
        if (mAnimatorListenerAdapter != null) {
            animatorSetFangda.addListener(mAnimatorListenerAdapter);
        }
        animatorSetFangda.start();
    }

    private void fada(View view) {
        fada(view, null);
    }

    private void suoxia(View view, AnimatorListenerAdapter mAnimatorListenerAdapter) {
        AnimatorSet animatorSetsuofang = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f);
        animatorSetsuofang.setDuration(200);
        animatorSetsuofang.setInterpolator(new LinearInterpolator());
        animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
        if (mAnimatorListenerAdapter != null) {
            animatorSetsuofang.addListener(mAnimatorListenerAdapter);
        }
        animatorSetsuofang.start();
    }

    private void suoxia(View view) {
        suoxia(view, null);
    }

    private void suoxiaAll() {
        for (int i = 0; i < myViews.size(); i++) {
            suoxia(myViews.get(i));
        }
    }

    public void setmBannerItemClickListener(BannerItemClickListener mBannerItemClickListener) {
        this.mBannerItemClickListener = mBannerItemClickListener;
    }
}
