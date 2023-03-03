package com.example.timecapsule.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timecapsule.R;

import java.util.ArrayList;
import java.util.Stack;

public class CanvasActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap copyBitmap;
    private Paint paint;
    private Canvas canvas;
    private float startX;
    private float startY;
    private ArrayList<Bitmap> historyBitmap;
    private int index;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        imageView = (ImageView) findViewById(R.id.diary_canvas_image);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.canvas);
        copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        paint = new Paint();
        paint.setStrokeWidth(10);
        canvas = new Canvas(copyBitmap);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        imageView.setImageBitmap(copyBitmap);
        historyBitmap = new ArrayList<>();
        historyBitmap.add(copyBitmap.copy(copyBitmap.getConfig(), true));
        index = 0;
        //设置画笔颜色
        findViewById(R.id.black).setOnClickListener(e -> paint.setColor(0xff000000));
        findViewById(R.id.red).setOnClickListener(e -> paint.setColor(0xffF44336));
        findViewById(R.id.blue).setOnClickListener(e -> paint.setColor(0xff03A9F4));
        findViewById(R.id.green).setOnClickListener(e -> paint.setColor(0xff66AF4C));
        findViewById(R.id.purple).setOnClickListener(e -> paint.setColor(0xff6a5acd));
        findViewById(R.id.orange).setOnClickListener(e -> paint.setColor(0xffFF9800));
        findViewById(R.id.yellow).setOnClickListener(e -> paint.setColor(0xffFFEB3B));

        findViewById(R.id.diary_canvas_back).setOnClickListener(e -> {
            Intent intent = getIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.silde_left_in, R.anim.slide_right_out);
        });

        findViewById(R.id.diary_canvas_save).setOnClickListener(e -> {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            imageView.setDrawingCacheEnabled(true);
            Bitmap b = imageView.getDrawingCache().copy(Bitmap.Config.RGB_565, false);
            imageView.destroyDrawingCache();
            intent.putExtra("canvas", Utils.BitmapToString(b));
            finish();
            overridePendingTransition(R.anim.silde_left_in, R.anim.slide_right_out);
        });

        findViewById(R.id.diary_canvas_backward).setOnClickListener(e -> {
            if (index > 0) {
                index--;
                Bitmap b = historyBitmap.get(index);
                copyBitmap = b.copy(b.getConfig(), true);
                canvas = new Canvas(copyBitmap);
                imageView.setImageBitmap(copyBitmap);
            }
        });

        findViewById(R.id.diary_canvas_forward).setOnClickListener(e -> {
            if (index < historyBitmap.size() - 1) {
                index++;
                Bitmap b = historyBitmap.get(index);
                copyBitmap = b.copy(b.getConfig(), true);
                canvas = new Canvas(copyBitmap);
                imageView.setImageBitmap(copyBitmap);
            }
        });

        imageView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    startX = event.getX();
                    startY = event.getY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX();
                    float y = event.getY();
                    canvas.drawLine(startX, startY, x, y, paint);
                    imageView.setImageBitmap(copyBitmap);
                    startX = x;
                    startY = y;
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    index++;
                    while (historyBitmap.size() > index)
                        historyBitmap.remove(historyBitmap.size() - 1);
                    historyBitmap.add(copyBitmap.copy(copyBitmap.getConfig(), true));
                }
            }
            return true;
        });

    }
}
