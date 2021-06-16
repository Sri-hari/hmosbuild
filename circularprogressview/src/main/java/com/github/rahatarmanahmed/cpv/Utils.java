/*
 *  * Copyright (C) 2021 Huawei Device Co., Ltd.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.rahatarmanahmed.cpv;

import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;

public final class Utils {

    public static final String cpv_progress = "cpv_progress";

    public static final String cpv_maxProgress = "cpv_maxProgress";

    public static final String cpv_animDuration = "cpv_animDuration";

    public static final String cpv_animSwoopDuration = "cpv_animSwoopDuration";

    public static final String cpv_animSyncDuration = "cpv_animSyncDuration";

    public static final String cpv_color = "cpv_color";

    public static final String cpv_thickness = "cpv_thickness";

    public static final String cpv_indeterminate = "cpv_indeterminate";

    public static final String cpv_animAutostart = "cpv_animAutostart";

    public static final String cpv_animSteps = "cpv_animSteps";

    public static final String cpv_startAngle = "cpv_startAngle";

    public static final boolean cpv_default_is_indeterminate = false;

    public static final boolean cpv_default_anim_autostart = false;

    public static final int cpv_default_progress = 0;

    public static final int cpv_default_max_progress = 100;

    public static final int cpv_default_anim_duration = 4000;

    public static final int cpv_default_anim_swoop_duration = 5000;

    public static final int cpv_default_anim_sync_duration = 500;

    public static final int cpv_default_anim_steps = 3;

    public static final int cpv_default_start_angle = -90;

    private static final String TAG = "Utils";

    /**
     * get the dimen value
     *
     * @param context the context
     * @param id the id
     * @return get the float dimen value
     */
    public static float getDimen(Context context, int id) {
        float result = 0;
        if (context == null) {
            LogUtil.error(TAG, "getDimen -> get null context");
            return result;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            LogUtil.error(TAG, "getDimen -> get null ResourceManager");
            return result;
        }
        try {
            result = manager.getElement(id).getFloat();
        } catch (IOException e) {
            LogUtil.error(TAG, "getDimen -> IOException");
        } catch (NotExistException e) {
            LogUtil.error(TAG, "getDimen -> NotExistException");
        } catch (WrongTypeException e) {
            LogUtil.error(TAG, "getDimen -> WrongTypeException");
        }
        return result;
    }

    /**
     * get the color
     *
     * @param context the context
     * @param id the id
     * @return the color
     */
    public static int getColor(Context context, int id) {
        int result = 0;
        if (context == null) {
            LogUtil.error(TAG, "getColor -> get null context");
            return result;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            LogUtil.error(TAG, "getColor -> get null ResourceManager");
            return result;
        }
        try {
            result = manager.getElement(id).getColor();
        } catch (IOException e) {
            LogUtil.error(TAG, "getColor -> IOException");
        } catch (NotExistException e) {
            LogUtil.error(TAG, "getColor -> NotExistException");
        } catch (WrongTypeException e) {
            LogUtil.error(TAG, "getColor -> WrongTypeException");
        }
        return result;
    }
}