package com.twyst.app.android.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.twyst.app.android.R;

/**
 * Created by satish on 20/06/15.
 */
public class TwystProgressHUD extends Dialog {
    public TwystProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    public static TwystProgressHUD show(Context context, boolean cancelable, OnCancelListener cancelListener) {
        TwystProgressHUD dialog = new TwystProgressHUD(context, R.style.ProgressHUD);
        dialog.setTitle("");
        dialog.setContentView(R.layout.twyst_progress_hud);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        //lp.dimAmount = 0.2f;
        lp.dimAmount = 0f;
        dialog.getWindow().setAttributes(lp);

        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        dialog.getWindow().setWindowAnimations(R.style.DialogNoAnimation);


        dialog.show();
        return dialog;


    }
}
