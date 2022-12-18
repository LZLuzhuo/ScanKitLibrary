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
package me.luzhuo.lib_qrcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.FragmentActivity;
import me.luzhuo.lib_permission.Permission;
import me.luzhuo.lib_permission.PermissionCallback;
import me.luzhuo.lib_qrcode.style.QRCodeStyleAActivity;

import static android.app.Activity.RESULT_OK;

/**
 * zxing 识别不了特殊QRCode
 * zbar 识别不了特殊QRCode
 * ML Kit 识别不了特殊QRCode
 * Scan Kit 识别不了特殊QRCode
 * Scan Kit Plus 可以识别特殊QRCode
 *
 *
 * Scan Kit Plus:
 * Android 4.4+ (api19+)
 * iOS 9.0+
 */
public class QRCodeManager {
    public static final int REQUEST_CODE_SCAN_DEFAULT_MODE = 0x001;
    public static final String REQUEST_CODE_SCAN_DEFAULT_MODE_RESULT = ScanUtil.RESULT;
    private static final QRCodeManager instance = new QRCodeManager();

    public static final int QRCodeStyleA = 0x01;

    @IntDef({QRCodeStyleA})
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public @interface QRStyle { }

    public static QRCodeManager getInstance() {
        return instance;
    }

    private QRCodeManager() { }

    /**
     * 使用默认界面调用扫描库
     * @param activity Activity
     */
    @RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void openScanDefaultView(FragmentActivity activity) {
        Permission.request(activity, new PermissionCallback(){
            @Override
            public void onGranted() {
                HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create();
                ScanUtil.startScan(activity, REQUEST_CODE_SCAN_DEFAULT_MODE, options);
            }
        }, Manifest.permission.CAMERA);
    }

    /**
     * 解析 openScanDefaultView() 返回的数据
     */
    @Nullable
    public String parseScanDefaultViewData(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK || data == null) return null;

        if (requestCode == QRCodeManager.REQUEST_CODE_SCAN_DEFAULT_MODE) {
            HmsScan hmsScan = data.getParcelableExtra(QRCodeManager.REQUEST_CODE_SCAN_DEFAULT_MODE_RESULT);
            if (hmsScan == null) return null;

            String originalValue = hmsScan.getOriginalValue();
            if (TextUtils.isEmpty(originalValue)) return null;
            else return originalValue;
        }
        return null;
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void openScan(@NonNull Context context, @QRStyle int style, @Nullable QRCodeCallback callback) {
        if (style == QRCodeStyleA) QRCodeStyleAActivity.start(context, callback);
    }

    /**
     * 创建二维码
     * @param content 文本内容
     * @param margin 外边距
     * @param color 二维码颜色
     * @param backgroundColor 二维码背景色
     * @param width 二维码宽度, 单位px
     * @param height 二维码高度, 单位px
     * @return 返回Bitmap类型的二维码图片, 或null
     */
    @Nullable
    public Bitmap createQrCode(@Nullable String content, int margin, @ColorInt int color, @ColorInt int backgroundColor, int width, int height) {
        if (TextUtils.isEmpty(content)) return null;

        try {
            HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator().setBitmapMargin(margin).setBitmapColor(color).setBitmapBackgroundColor(backgroundColor).create();
            return ScanUtil.buildBitmap(content, HmsScan.QRCODE_SCAN_TYPE, width, height, options);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Bitmap createQrCode(@Nullable String content, int width, int height) {
        return createQrCode(content, 0, 0xFF000000, 0xFFFFFFFF, width, height);
    }
}
