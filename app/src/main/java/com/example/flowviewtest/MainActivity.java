package com.example.flowviewtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    TextView tv_msg;
    LinearLayout ll_msg;
    DraggableFrameLayout ll_view;
    ImageView iv_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ll_msg=findViewById(R.id.ll_msg);
        ll_msg.setVisibility(View.INVISIBLE);
        ll_view=findViewById(R.id.ll_view);
        iv_show=findViewById(R.id.iv_show);
        tv_msg=findViewById(R.id.tv_msg);
        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_msg.setText("找到");
              //  ll_view.textChange();
                startAnimotion();

            }
        });


    }
    AnimatorSet animSet = new AnimatorSet();
    private void startAnimotion(){
        if (animSet.isRunning()){
            return;
        }

        ll_msg.setVisibility(View.VISIBLE);
        float length=tv_msg.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(tv_msg, "translationX", length,0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(500);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(tv_msg, "translationX", 0,length);
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

}
