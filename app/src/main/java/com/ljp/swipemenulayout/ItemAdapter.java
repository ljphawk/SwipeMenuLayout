package com.ljp.swipemenulayout;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
 *@创建者       L_jp
 *@创建时间     2019/6/9 13:31.
 *@描述
 *
 *@更新者         $Author$
 *@更新时间         $Date$
 *@更新描述
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mShowItems = new ArrayList<>();

    public ItemAdapter(Context context) {
        mContext = context;
        for (int i = 0; i < 50; i++) {
            mShowItems.add("item = " + i);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String item = mShowItems.get(i);
        if (i % 4 == 0) {
            viewHolder.mSwipe.setEnableRightSwipe(true);
            viewHolder.mTv1.setText(item + "，菜单在左");
        } else {
            viewHolder.mSwipe.setEnableRightSwipe(false);
            viewHolder.mTv1.setText(item + "，菜单在右");
        }
        viewHolder.mLl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "1111", Toast.LENGTH_SHORT).show();
                viewHolder.mSwipe.expandMenuAnim();
            }
        });
        viewHolder.mLl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "我长按了" + item, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        viewHolder.mTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "2222", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.mTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除了", Toast.LENGTH_SHORT).show();
                mShowItems.remove(i);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShowItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View mLl_item;
        private final TextView mTv1;
        private final View mTv2;
        private final View mTv3;
        private final SwipeMenuLayout mSwipe;

        public ViewHolder(@NonNull View view) {
            super(view);
            mLl_item = view.findViewById(R.id.ll_item);
            mTv1 = view.findViewById(R.id.tv1);
            mTv2 = view.findViewById(R.id.tv2);
            mTv3 = view.findViewById(R.id.tv3);
            mSwipe = view.findViewById(R.id.swipe);
        }
    }
}

