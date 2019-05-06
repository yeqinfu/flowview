package com.example.flowviewtest;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flowviewtest.entity.DetectedVideoInfo;
import com.example.flowviewtest.entity.VideoInfo;
import com.example.flowviewtest.utils.VideoFormatUtil;
import com.example.flowviewtest.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    TextView tv_msg;
    LinearLayout ll_msg;
    DraggableFrameLayout ll_view;
    ImageView iv_show;
    WebView web_view;
    EditText et_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ll_msg = findViewById(R.id.ll_msg);
        web_view = findViewById(R.id.web_view);
        et_input = findViewById(R.id.et_input);
        ll_msg.setVisibility(View.INVISIBLE);
        ll_view = findViewById(R.id.ll_view);
        iv_show = findViewById(R.id.iv_show);
        tv_msg = findViewById(R.id.tv_msg);
        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_msg.setText("找到123123123123");
                //  ll_view.textChange();
                startAnimotion();

            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web_view.goBack();
            }
        });
        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_input.getText())){
                    Toast.makeText(MainActivity.this,"空输入",Toast.LENGTH_SHORT).show();
                    return;
                }
                web_view.loadUrl(et_input.getText().toString());
            }
        });
      mainInit();


    }
    private VideoSniffer videoSniffer;
    private SortedMap<String, VideoInfo> foundVideoInfoMap = Collections.synchronizedSortedMap(new TreeMap<String, VideoInfo>());

    private void initMain() {
        videoSniffer=new VideoSniffer(detectedTaskUrlQueue, foundVideoInfoMap, MainApplication.appConfig.videoSnifferThreadNum, MainApplication.appConfig.videoSnifferRetryCountOnFail);

    }

    private void initWebView() {
        WebSettings settings = web_view.getSettings();
        web_view.setWebViewClient(webViewClient);
        web_view.setWebChromeClient(webChromeClient);
    }
    private LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue = new LinkedBlockingQueue<DetectedVideoInfo>();
    private String currentTitle = "";
    private String currentUrl = "";
    private WebChromeClient webChromeClient=new WebChromeClient(){
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            currentTitle=title;
        }
    };

    private  WebViewClient  webViewClient=new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            currentUrl=url;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url=request.getUrl().toString();
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                //非http https协议 不动作
                return true;
            }

            //http https协议 在本webView中加载

            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if(VideoFormatUtil.containsVideoExtension(extension)){
                detectedTaskUrlQueue.add(new DetectedVideoInfo(url,currentUrl,currentTitle));
                Log.d("MainActivity", "shouldOverrideUrlLoading detectTaskUrlList.add(url):" + url);
                return true;
            }
            Log.d("MainActivity", "shouldOverrideUrlLoading url="+url);
            view.loadUrl(url);
            return true;

        }
    };

    AnimatorSet animSet = new AnimatorSet();

    private void startAnimotion() {
        if (animSet.isRunning()) {
            return;
        }

        ll_msg.setVisibility(View.VISIBLE);
        float length = tv_msg.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(tv_msg, "translationX", length, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(500);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(tv_msg, "translationX", 0, length);
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateInterpolator());
        /*****************/
      /*  ObjectAnimator rotationX = ObjectAnimator.ofFloat(iv_show, "rotation", 0,180f);
        rotationX.setDuration(500);
        rotationX.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator rotationX2 = ObjectAnimator.ofFloat(iv_show, "rotation", 0,360f);
        rotationX2.setDuration(500);
        rotationX2.setInterpolator(new AccelerateInterpolator());*/

        /*****************/


        animSet.play(animator2).after(2000).after(animator);

        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ll_msg.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    protected void onStop() {
        if (web_view != null) {
            web_view.pauseTimers();
        }
        if(videoSniffer!=null) {
            videoSniffer.stopSniffer();
        }
       // EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * 为权限赋予一个唯一的标示码
     */
    public static final int WRITE_EXTERNAL_STORAGE = 1001;
    private boolean initReady = false;


    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
    private void requireAllPermissionForInit() {
        //可以只获取写或者读权限，同一个权限Group下只要有一个权限申请通过了就都可以用了
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            if(!initReady){
                mainInit();
                initReady = true;
            }
            if(videoSniffer != null) {
                videoSniffer.startSniffer();
            }
            if (web_view != null) {
                web_view.resumeTimers();
                //web_view.onShow();
            }
         //   startRefreshGoBackButtonStateThread();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "下载需要读写外部存储权限",
                    WRITE_EXTERNAL_STORAGE, perms);
        }
    }

    private void mainInit() {
        initWebView();
        initMain();
        web_view.loadUrl("http://www.okzyw.com/");
    }

    @Override
    protected void onStart() {
        super.onStart();
        requireAllPermissionForInit();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ViewUtil.openConfirmDialog(this,
                    "必需权限",
                    "没有该权限，此应用程序可能无法正常工作。打开应用设置屏幕以修改应用权限",
                    "去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(
                                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.fromParts("package", getPackageName(), null)));
                        }
                    },
                    "退出",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
            return;
        }
        ViewUtil.openConfirmDialog(this,
                "必需权限",
                "没有该权限，此应用程序可能无法正常工作。",
                "再试一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requireAllPermissionForInit();
                    }
                },
                "退出",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }
        );
    }
}
