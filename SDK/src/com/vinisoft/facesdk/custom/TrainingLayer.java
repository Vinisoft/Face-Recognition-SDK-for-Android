package com.vinisoft.facesdk.custom;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;

public class TrainingLayer {
	private final float tsv = 1.618f;
	private float pathPart;
	private float pathLength;
	private int max;
	private int x, y, w, h;
	private Path path;
	private Paint paintLine;
	private Paint paintCircleLine;
	private Paint paintCircleFill;
	private RectF oval;
	private DashPathEffect dpe;
	private PathDashPathEffect pdpe;
	private PathMeasure measure;

	public TrainingLayer(int widthScr, int heightScr, int max) {
		this.max = max;
		w = (int) (2 * widthScr / 4);
		h = (int) (w * tsv);
		x = (int) ((widthScr - w) / 2);
		y = (int) ((heightScr - h) / 2);

		oval = new RectF(x, y, x + w, y + h);
		path = new Path();
		path.addOval(oval, Direction.CW);

		measure = new PathMeasure(path, true);
		pathLength = measure.getLength();

		pathPart = pathLength / max;

		dpe = new DashPathEffect(new float[] { 5, 10 }, 0);
		pdpe = new PathDashPathEffect(makeCirclePathDash(10), pathPart, 0, PathDashPathEffect.Style.ROTATE);

		initPaint();
	}

	private void initPaint() {
		this.paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintLine.setDither(true);
		this.paintLine.setColor(0xFF00FFF0);
		this.paintLine.setStyle(Paint.Style.STROKE);
		this.paintLine.setStrokeJoin(Paint.Join.ROUND);
		this.paintLine.setStrokeCap(Paint.Cap.ROUND);
		this.paintLine.setStrokeWidth(2);

		this.paintCircleLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintCircleLine.setColor(0xFFFFFFFF);
		this.paintCircleLine.setARGB(255, 255, 255, 255);
		this.paintCircleLine.setStyle(Paint.Style.STROKE);
		this.paintCircleLine.setStrokeWidth(2);

		this.paintCircleFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintCircleFill.setColor(0xFF00FF00);
		this.paintCircleFill.setStyle(Paint.Style.FILL);
	}

	private Path makeCirclePathDash(float r) {
		Path path = new Path();
		path.addCircle(r, r, r, Direction.CW);
		return path;
	}

	public void drawBorderPath(Canvas canvas) {
		paintLine.setPathEffect(dpe);
		canvas.drawPath(path, paintLine);
	}

	public void drawCirclePath(Canvas canvas) {
		paintLine.setPathEffect(pdpe);
		canvas.drawPath(path, paintLine);
	}

	public void drawCircleArc(Canvas canvas, int number) {
		for (int i = 0; i < max; i++) {
			float[] p = getPoint(i);
			canvas.drawCircle(p[0], p[1], 7, paintCircleLine);
		}

		for (int i = 0; i < number && i < max; i++) {
			int tmp = i + 15;
			int pos = (tmp < max ? tmp : tmp - max);
			float[] p = getPoint(pos);
			canvas.drawCircle(p[0], p[1], 6, paintCircleFill);
		}
	}

	private float[] getPoint(int index) {
		float pos[] = { 0f, 0f };
		measure.getPosTan(pathLength * index / max, pos, null);
		return pos;
	}
}