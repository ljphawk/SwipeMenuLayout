package com.ljp.swipemenulayout;


import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ljp.swipemenu.SwipeMenuLayout;

/*
 *@创建者       L_jp
 *@创建时间     2019/7/21 13:36.
 *@描述
 *
 *@更新者         $Author$
 *@更新时间         $Date$
 *@更新描述
 */
public class ItemAdapter2 extends BaseQuickAdapter<String, BaseViewHolder> {

    public ItemAdapter2(List<String> mShowItems) {
        super(R.layout.layout_item, mShowItems);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        SwipeMenuLayout mSwipe = helper.getView(R.id.swipe_menu_layout);
        int position = helper.getLayoutPosition();
        mSwipe.setEnableLeftMenu(position % 4 == 0);
        String text = item + "，菜单在" + (position % 4 == 0 ? "左； " : "右； ");
        if (position % 3 == 0) {
            mSwipe.setOpenChoke(false);
            text += "我是无阻塞的； ";
        } else {
            mSwipe.setOpenChoke(true);
            text += "我是有阻塞的； ";
        }
        if (position % 5 == 0) {
            mSwipe.setClickMenuAndClose(true);
            text += "点击我可以展开菜单";
        } else {
            mSwipe.setClickMenuAndClose(false);
        }
        ((TextView) helper.getView(R.id.tv_content)).setText(text);
        helper.addOnClickListener(R.id.tv_menu1).addOnClickListener(R.id.tv_menu2);
    }
}
