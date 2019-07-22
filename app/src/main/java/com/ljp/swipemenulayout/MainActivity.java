package com.ljp.swipemenulayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int index = 0;

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

        recyclerView.setAdapter(new ItemAdapter(this));

//        ItemAdapter2 itemAdapter2 = new ItemAdapter2(mShowItems);
//        recyclerView.setAdapter(itemAdapter2);
//
//        itemAdapter2.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Log.d("MainActivity", "点击了item   :   " + (++index));
//            }
//        });
//        itemAdapter2.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                if (view.getId() == R.id.tv_menu1) {
//                    Log.d("MainActivity", "点击菜单1   " + (++index));
//                } else {
//                    Log.d("MainActivity", "点击菜单2   " + (++index));
//                }
//            }
//        });
//        itemAdapter2.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//                Log.d("MainActivity", "长按了" + (++index));
//                return true;
//            }
//        });
    }

}
