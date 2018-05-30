package com.dezhoutuu.zhuan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dezhoutuu.zhuan.Banner.BannerEntity;
import com.dezhoutuu.zhuan.Banner.TUUBanner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TUUBanner banner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = (TUUBanner)findViewById(R.id.banner);
        initBanner();
    }

    private void initBanner(){
        List<BannerEntity> bannerList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BannerEntity mBannerEntity = new BannerEntity();
            String url = "";
            if (i == 0) {
                url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526929052620&di=fe1d4b47a1f803366586b729516382d4&imgtype=0&src=http%3A%2F%2Fnews.youth.cn%2Fyl%2F201412%2FW020141214362909268739.jpg";
            } else if (i == 1) {
                url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527069414919&di=4be144aa2966b48d7c4362daf7c22947&imgtype=0&src=http%3A%2F%2Fent.northtimes.com%2Fu%2Fcms%2Fwww%2F201711%2F300913434d4d.jpg";
            } else if (i == 2) {
                url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527069441345&di=1c810b4392e5a1ddb8a7a71acc23fa5a&imgtype=0&src=http%3A%2F%2Fpic2.52pk.com%2Ffiles%2F170731%2F7777784_1I6232M.png";
            } else {
                url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526929052623&di=fdbb7da424acfb6f5d32b97c461e5676&imgtype=0&src=http%3A%2F%2Fqimg.hxnews.com%2F2018%2F0329%2F1522291784816.jpg";
            }
            mBannerEntity.setBg(url);
            mBannerEntity.setImg(url);
            bannerList.add(mBannerEntity);
        }
        banner.setImageUrls(bannerList);
        banner.start();
    }
}
