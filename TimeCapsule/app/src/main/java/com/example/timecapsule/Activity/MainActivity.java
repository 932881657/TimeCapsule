package com.example.timecapsule.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.timecapsule.Adapter.DBAdapter;
import com.example.timecapsule.Bean.Diary;
import com.example.timecapsule.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DiaryListAdapter diaryListAdapter;
    private RecyclerView recyclerView;
    public static DBAdapter dbAdapter ;
    private List<Diary> diaryListInfo;
    private Diary[] diaries;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video);
        videoView.setVisibility(View.GONE);
        ImageButton addDiaryButton = findViewById(R.id.addDiary);
        addDiaryButton.setOnClickListener(e -> {
            Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
            intent.putExtra("type", "write");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        });

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        diaryListInfo = new ArrayList<>();
        diaries = dbAdapter.queryAllData();
        if (diaries != null)
            diaryListInfo = new ArrayList<>(Arrays.asList(dbAdapter.queryAllData()));
        diaryListAdapter = new DiaryListAdapter(diaryListInfo);
        recyclerView = findViewById(R.id.diary_recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(diaryListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        diaries = dbAdapter.queryAllData();
        if (diaries != null) {
            diaryListInfo = new ArrayList<>(Arrays.asList(diaries));
            diaryListAdapter = new DiaryListAdapter(diaryListInfo);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(diaryListAdapter);
        }
    }

    //创建ViewHolder类
    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView title;
        ImageButton check;
        ImageButton lock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.diary_list_time);
            title = itemView.findViewById(R.id.diary_list_title);
            check = itemView.findViewById(R.id.diary_list_check);
            lock = itemView.findViewById(R.id.diary_list_lock);
        }
    }

    private class DiaryListAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<Diary> dataList;

        public DiaryListAdapter(List<Diary> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            Diary diary = dataList.get(position);
            holder.time.setText(diary.getDate());
            holder.title.setText(diary.getTitle());
            holder.check.setOnClickListener(e -> {
                if (!diary.getLock().equals("")){
                    String[] lockDate = diary.getLock().split("-");
                    int lockYear = Integer.parseInt(lockDate[0]);
                    int lockMonth = Integer.parseInt(lockDate[1]);
                    int lockDay = Integer.parseInt(lockDate[2]);
                    if ((currentYear - lockYear) * 10000 + (currentMonth - lockMonth) * 100 + currentDay - lockDay <= 0){
                        Toast.makeText(MainActivity.this, "还未到达设定日期！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
                intent.putExtra("type", "check");
                intent.putExtra("diaryID", diary.getID() + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            });
            holder.lock.setOnClickListener(e -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, 0, (view, year, month, dayOfMonth) -> {
                    if ((year - currentYear) * 10000 + (month - currentMonth) * 100 + dayOfMonth - currentDay <= 0){
                        view.updateDate(currentYear, currentMonth, currentDay);
                        Toast.makeText(MainActivity.this, "只能选择大于当前日期的时间!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    diary.setLock(year + "-" + month + "-" + dayOfMonth);
                    dbAdapter.updateOneData(diary.getID(), diary);

                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    for (String str : permissions) {
                        if (MainActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            MainActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            return;
                        } else {
                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "Android/video.mp4");
                            videoView.setOnCompletionListener(mp -> videoView.setVisibility(View.GONE));
                            videoView.setVisibility(View.VISIBLE);
                            MediaController mediaController = new MediaController(MainActivity.this);
                            mediaController.setVisibility(View.GONE);
                            videoView.setMediaController(mediaController);
                            videoView.setVideoPath(file.getPath());
                            videoView.requestFocus();
                            videoView.start();
                        }
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}