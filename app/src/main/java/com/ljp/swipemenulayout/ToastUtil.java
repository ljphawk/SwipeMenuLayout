package com.ljp.swipemenulayout;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast sToast;

    public static void showToast(final Context context, final String msg) {

        if (sToast == null) {
            initToast(context, msg);
        }
        //判断当前代码是否是主线程
        sToast.setText(msg);
        sToast.show();
    }

    private static void initToast(Context context, String msg) {
        sToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        sToast.setText(msg);
    }
}
