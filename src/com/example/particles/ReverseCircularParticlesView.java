package com.example.particles;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.example.particles.ReverseCircularParticlesView.Particle.QUADRANT;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 反向圆形粒子View
 * 
 * @author Ruby
 *
 */
public class ReverseCircularParticlesView extends View {

	public ReverseCircularParticlesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private static final String TAG = ReverseCircularParticlesView.class.getSimpleName();
	private static final boolean DEBUG = true;

	// 差值最大默认值
	private static final int DEFAULT_MAX_STEP = 20;
	// 差值最大默认值
	private static final int DEFAULT_MIN_STEP = 5;
	// 粒子数默认值
	private static final int DEFAULT_PARTICLES_COUNT = 100;
	// 粒子圆外起始位置
	private static final int DEFAULT_OUT_START_RANGE = 20;
	// 粒子院内起始位置
	private static final int DEFAULT_IN_START_RANGE = 20;
	// 粒子默认颜色
	private static final int DEFAULT_COLOR = Color.RED;
	// 默认的粒子最大半径
	private static final int DEFAULT_PARTICLE_MAX_RADIUS = 10;

	// 圆心
	int mCenterX = Integer.MIN_VALUE;
	int mCenterY = Integer.MIN_VALUE;

	int mRadius = -1;

	int mColor = DEFAULT_COLOR;

	int mOutStartRange = DEFAULT_OUT_START_RANGE;
	int mInStartRange = DEFAULT_IN_START_RANGE;

	// 最大差值
	int mMaxStep = DEFAULT_MAX_STEP;
	// 最小差值
	int mMinStep = DEFAULT_MIN_STEP;

	// 粒子数量
	int mParticlesCount = DEFAULT_PARTICLES_COUNT;

	// 粒子集合
	Set<Particle> mParticles;

	int mParticleMaxRadius = DEFAULT_PARTICLE_MAX_RADIUS;

	static class Particle {
		enum QUADRANT {
			ONE, TWO, THREE, FOUR
		};

		int x;
		int y;
		Paint paint;
		int radius;
		QUADRANT qr;
		int nowRadius;
		double angle;
		int step;

		public Particle(int x, int y, int radius, Paint paint) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.paint = paint;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mParticles == null || mParticles.size() == 0) {
			return;
		}

		for (Particle p : mParticles) {
			canvas.drawCircle(p.x, p.y, p.radius, p.paint);
		}
	}

	public void move() {
		int removedCount = 0;
		Iterator<Particle> pIterator = mParticles.iterator();
		// 移动
		while (pIterator.hasNext()) {
			Particle p = pIterator.next();
			int nowRadius = p.nowRadius - p.step;
			if (nowRadius <= 0) {
				pIterator.remove();
				removedCount++;
				continue;
			}

			int nowX, nowY;

			if (p.qr == QUADRANT.ONE) {
				nowX = (int) (mCenterX + Math.cos(p.angle) * nowRadius);
				nowY = (int) (mCenterY - Math.sin(p.angle) * nowRadius);
			} else if (p.qr == QUADRANT.TWO) {
				nowX = (int) (mCenterX - Math.cos(p.angle) * nowRadius);
				nowY = (int) (mCenterY - Math.sin(p.angle) * nowRadius);
			} else if (p.qr == QUADRANT.THREE) {
				nowX = (int) (mCenterX - Math.cos(p.angle) * nowRadius);
				nowY = (int) (mCenterY + Math.sin(p.angle) * nowRadius);
			} else {
				nowX = (int) (mCenterX + Math.cos(p.angle) * nowRadius);
				nowY = (int) (mCenterY + Math.sin(p.angle) * nowRadius);
			}
			

			if (DEBUG) {
				Log.d(TAG, "o.nowRadius=" + p.nowRadius + "  nowRadius=" + nowRadius);
				Log.d(TAG, "o.x=" + p.x + "o.y=" + p.y + "  now.x=" + nowX + " now.y=" + nowY);
			}
			
			p.x = nowX;
			p.y = nowY;
			p.nowRadius = nowRadius;
			
		}

		// 重新添加
		while (removedCount > 0) {
			mParticles.add(generage());
			removedCount--;
		}
		
		postInvalidate();
	}

	/**
	 * 设置圆心基本数据
	 * 
	 * @param x
	 * @param y
	 * @param r
	 */
	public void setCircle(int x, int y, int r) {
		this.mCenterX = x;
		this.mCenterY = y;
		this.mRadius = r;
	}

	/**
	 * 设置粒子数量
	 * 
	 * @param count
	 */
	public void setParticlesCount(int count) {
		this.mParticlesCount = count;
	}

	/**
	 * 设置粒子每次最大移动的距离
	 * 
	 * @param step
	 */
	public void setMaxStep(int step) {
		this.mMaxStep = step;
	}
	
	/**
	 * 设置粒子每次最小移动的距离
	 * 
	 * @param step
	 */
	public void setMinStep(int step) {
		this.mMinStep = step;
	}

	/**
	 * 圆外起始范围
	 * 
	 * @param range
	 */
	public void setOutStartRange(int range) {
		this.mOutStartRange = range;
	}

	/**
	 * 设置颜色
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		this.mColor = color;
	}

	/**
	 * 设置粒子最大半径
	 * 
	 * @param radius
	 */
	public void setParticlesMaxRadius(int radius) {
		this.mParticleMaxRadius = radius;
	}

	/**
	 * 院内起始范围
	 * 
	 * @param range
	 */
	public void setInOutStartRange(int range) {
		this.mInStartRange = range;
	}

	public void prepare() {
		mParticles = new HashSet<ReverseCircularParticlesView.Particle>(mParticlesCount);
		while (mParticles.size() < mParticlesCount) {
			mParticles.add(generage());
		}
	}

	private Particle generage() {
		// 通过quadrant确定象限
		int quadrantX = Math.random() >= 0.5 ? 1 : -1;
		int quadrantY = Math.random() >= 0.5 ? 1 : -1;
		// Range
		int rangeFactor = Math.random() >= 0.5 ? 1 : -1;

		// Angle
		double angle = Math.PI / 2 * Math.random();

		double nowRadius = mRadius
				+ (rangeFactor == 1 ? mOutStartRange * Math.random() : -mInStartRange * Math.random());

		int centerX = (int) (mCenterX + Math.cos(angle) * nowRadius * quadrantX);
		int centerY = (int) (mCenterY + Math.sin(angle) * nowRadius * quadrantY);

		int particleRadius = (int) (Math.random() * mParticleMaxRadius);

		Paint paint = new Paint();
		paint.setColor(mColor);

		Particle particle = new Particle(centerX, centerY, particleRadius, paint);

		// 确定象限
		if (quadrantX > 0) {
			particle.qr = quadrantY > 0 ? QUADRANT.ONE : QUADRANT.FOUR;
		} else {
			particle.qr = quadrantY > 0 ? QUADRANT.TWO : QUADRANT.THREE;
		}

		particle.nowRadius = (int) nowRadius;
		particle.angle = angle;
		particle.step = (int) (Math.random() * mMaxStep) + mMinStep;

		if (DEBUG) {
			Log.d(TAG, "angle=" + angle + " centerX=" + centerX + "  centerY=" + centerY + " nowRadius=" + nowRadius);
		}

		return particle;
	}

}
