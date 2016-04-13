package com.example.particles;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	ReverseCircularParticlesView particlesView;
	ValueAnimator particleAnim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		particlesView = (ReverseCircularParticlesView) findViewById(R.id.particle);
		
		playParticleAnim();
		
	}
	
	
	void playParticleAnim() {
		if (particleAnim != null) {
			particleAnim.cancel();
			particleAnim = null;
		}

		particlesView.setCircle(500, 500, 300);
		particlesView.setColor(Color.argb(255, 92, 241, 34));
		particlesView.prepare();

		particleAnim = ValueAnimator.ofInt(0, 1);
		particleAnim.setRepeatCount(ValueAnimator.INFINITE);
		particleAnim.setDuration(40);
		particleAnim.setInterpolator(new LinearInterpolator());

		particleAnim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				particlesView.move();
			}

			@Override
			public void onAnimationEnd(Animator arg0) {

			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		particleAnim.start();
	}

}
