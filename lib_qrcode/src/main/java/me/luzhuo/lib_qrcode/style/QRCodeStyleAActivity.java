/* Copyright 2022 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.lib_qrcode.style;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.hmsscankit.OnLightVisibleCallBack;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import me.luzhuo.lib_core.app.base.CoreBaseActivity;
import me.luzhuo.lib_core.ui.calculation.UICalculation;
import me.luzhuo.lib_core.ui.statusbar.StatusBarManager;
import me.luzhuo.lib_qrcode.QRCodeCallback;
import me.luzhuo.lib_qrcode.R;

public class QRCodeStyleAActivity extends CoreBaseActivity implements View.OnClickListener {
    private final int[] flashLights = new int[]{R.mipmap.qrcode_flashlight_off, R.mipmap.qrcode_flashlight_on};
    private FrameLayout qrcode_rim;
    private TextView qrcode_flashlight;
    private RemoteView remoteView;
    @Nullable
    private static QRCodeCallback callback;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static void start(Context context, @Nullable QRCodeCallback callback) {
        QRCodeStyleAActivity.callback = callback;
        Intent intent = new Intent(context, QRCodeStyleAActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StatusBarManager.getInstance().transparent(this, false);
        setContentView(R.layout.qrcode_activity_style_a);

        qrcode_rim = findViewById(R.id.qrcode_rim);
        qrcode_flashlight = findViewById(R.id.qrcode_flashlight);
        qrcode_flashlight.setOnClickListener(this);

        View qrcode_toolbar = findViewById(R.id.qrcode_toolbar);
        ((ViewGroup.MarginLayoutParams) qrcode_toolbar.getLayoutParams()).topMargin = new UICalculation(this).getStatusBarHeight(qrcode_toolbar);

        initView(bundle);
    }

    private void initView(Bundle bundle) {
        int[] display = new UICalculation(this).getDisplay();
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = display[0];
        rect.bottom = display[1];

        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build();
        remoteView.setOnLightVisibleCallback(new OnLightVisibleCallBack() {
            @Override
            public void onVisibleChanged(boolean visible) {
                if (visible) qrcode_flashlight.setVisibility(View.VISIBLE);
                else qrcode_flashlight.setVisibility(View.INVISIBLE);
            }
        });

        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] hmsScans) {
                if (hmsScans == null) return;
                if (hmsScans.length <= 0) return;
                if (hmsScans[0] == null) return;

                String originalValue = hmsScans[0].originalValue;
                if (TextUtils.isEmpty(originalValue)) return;

                if (callback != null) callback.onQRResult(originalValue);
                finish();
            }
        });

        remoteView.onCreate(bundle);
        qrcode_rim.addView(remoteView, new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onClick(View view) {
        if (view == qrcode_flashlight) {
            if (remoteView.getLightStatus()) {
                remoteView.switchLight();
                qrcode_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, flashLights[0], 0, 0);
            } else {
                remoteView.switchLight();
                qrcode_flashlight.setCompoundDrawablesWithIntrinsicBounds(0, flashLights[1], 0, 0);
            }
        }
    }

    @Override
    protected void onStart() {
        remoteView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        remoteView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        remoteView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        remoteView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        remoteView.onDestroy();
        callback = null;
        super.onDestroy();
    }
}
