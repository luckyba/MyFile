package com.luckyba.myfile.common;

import android.view.View;

public interface CommonListener {
    interface CommunicationActivity {
        void onBackPressed(int navItemIndex);
    }

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
