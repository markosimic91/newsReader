package com.example.newsreader;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

     @BindView(R.id.backdrop) ImageView imageView;
     @BindView(R.id.title_on_appbar) TextView appbar_title;
     @BindView(R.id.subtitle_on_appbar) TextView appbar_subtitle;
     @BindView(R.id.date) TextView date;
     @BindView(R.id.time) TextView time;
     @BindView(R.id.title) TextView title;

     @BindView(R.id.date_behavior) FrameLayout date_behavior;
     @BindView(R.id.title_appbar) LinearLayout titleAppbar;
     @BindView(R.id.appbar) AppBarLayout appBarLayout;
     @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

     Toolbar toolbar;
     String mUrl, mImg, mTitle, mDate, mSource, mAuthor;
     boolean isHideToolbarView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        collapsingToolbarLayout.setTitle("");
        appBarLayout.addOnOffsetChangedListener(this);


        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mImg = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("title");
        mDate = intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());

        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appbar_title.setText(mSource);
        appbar_subtitle.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author;
        if (mAuthor != null) {
            author = " \u2022 " + mAuthor;
        } else {
            author = "";
        }

        time.setText(mSource + author + " \u2022 " + Utils.DateToTimeFormat(mDate));

        initWebView(mUrl);

    }

    private void initWebView(String url) {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            date_behavior.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            date_behavior.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.view_web){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mUrl));
            startActivity(intent);
            return true;
        }else if(id == R.id.share){
            try{
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plan");
                i.putExtra(Intent.EXTRA_SUBJECT,mSource);
                String body = mTitle + "\n" + mUrl + "\n" + "Share from NewsReader" + "\n";
                i.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(i,"Share wit :"));
            }catch (Exception e){
                Toast.makeText(this, "Hmm...Sorry, \nCannot be share", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
