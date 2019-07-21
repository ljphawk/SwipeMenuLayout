package com.ljp.swipemenulayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> mShowItems = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mShowItems.add("item = " + i);
        }

//        recyclerView.setAdapter(new ItemAdapter(this));
        ItemAdapter2 itemAdapter2 = new ItemAdapter2(mShowItems);
        recyclerView.setAdapter(itemAdapter2);

        itemAdapter2.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d("MainActivity", "onItemClick: "+1111);
                ToastUtil.showToast(MainActivity.this,position+"");
            }
        });

    }

}
