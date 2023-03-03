package com.example.timecapsule.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timecapsule.Bean.Diary;
import com.example.timecapsule.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryActivity extends AppCompatActivity {
    private EditText title;
    private EditText content;
    private TextView date;
    private ImageButton back;
    private ImageButton save;
    private ImageButton canvas;
    private ImageButton choose;
    private ImageView image;
    private boolean chosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        //根据查看/编辑进入不同界面
        if (type.equals("check")){
            setContentView(R.layout.activity_diary_check);
            title = findViewById(R.id.diary_check_title);
            content = findViewById(R.id.diary_check_content);
            date = findViewById(R.id.diary_check_date);
            back = findViewById(R.id.diary_check_back);
            image = findViewById(R.id.diary_check_image);
            //查看日记时禁用编辑
            title.setKeyListener(null);
            content.setKeyListener(null);

            long id  = Long.parseLong(intent.getStringExtra("diaryID"));
            Diary diary = MainActivity.dbAdapter.queryOneData(id)[0];
            title.setText(diary.getTitle());
            content.setText(diary.getContent());
            date.setText(diary.getDate());
            String imgData = diary.getImage();
            //根据是否存在图片设置image
            if (!imgData.equals(""))
                image.setImageBitmap(Utils.StringToBitmap(diary.getImage()));
            else
                image.setVisibility(View.GONE);
        }
        else {
            setContentView(R.layout.activity_diary_write);
            chosen = false;
            title = findViewById(R.id.diary_write_title);
            content = findViewById(R.id.diary_write_content);
            date = findViewById(R.id.diary_write_date);
            back = findViewById(R.id.diary_write_back);
            save = findViewById(R.id.diary_write_save);
            image = findViewById(R.id.diary_write_image);
            image.setVisibility(View.GONE);
            choose = findViewById(R.id.diary_write_choose);
            choose.setOnClickListener(v -> {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 200);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            });

            canvas = findViewById(R.id.diary_write_canvas);
            canvas.setOnClickListener(v -> {
                Intent i = new Intent(DiaryActivity.this, CanvasActivity.class);
                startActivityForResult(i, 201);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            });
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        date.setText(currentDate);
        back.setOnClickListener(e -> {
            finish();
            overridePendingTransition(R.anim.silde_left_in, R.anim.slide_right_out);
        });
        if (save != null) {
            save.setOnClickListener(e -> {
                String img = "";
                if (chosen){
                    image.setDrawingCacheEnabled(true);
                    Bitmap bitmap = image.getDrawingCache().copy(Bitmap.Config.RGB_565, false);
                    image.destroyDrawingCache();
                    img = Utils.BitmapToString(bitmap);
                }
                Diary diary = new Diary(title.getText().toString(), content.getText().toString(), currentDate, img);
                MainActivity.dbAdapter.insert(diary);
                finish();
                overridePendingTransition(R.anim.silde_left_in, R.anim.slide_right_out);
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri)
                    image.setImageURI(selectedImageUri);
            }
            else if (requestCode == 201)
                image.setImageBitmap(Utils.StringToBitmap(data.getStringExtra("canvas")));
            chosen = true;
            image.setVisibility(View.VISIBLE);
        }

    }


}
