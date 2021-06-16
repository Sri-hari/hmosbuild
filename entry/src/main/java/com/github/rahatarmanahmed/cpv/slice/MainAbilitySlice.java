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

package com.github.rahatarmanahmed.cpv.slice;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.github.rahatarmanahmed.cpv.CircularProgressViewAdapter;
import com.github.rahatarmanahmed.cpv.LogUtil;
import com.github.rahatarmanahmed.cpv.ResourceTable;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.RoundProgressBar;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.miscservices.timeutility.Time;

public class MainAbilitySlice extends AbilitySlice {
    private static final String TAG = "MainAbilitySlice";

    CircularProgressView progressView;

    RoundProgressBar nativeProgressBar;

    Thread updateThread;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        progressView = (CircularProgressView) findComponentById(ResourceTable.Id_progress_view);
        nativeProgressBar = (RoundProgressBar) findComponentById(ResourceTable.Id_native_progress_bar);

        // Test the listener with logcat messages
        progressView.addListener(new CircularProgressViewAdapter() {
            @Override
            public void onProgressUpdate(float currentProgress) {
                LogUtil.debug(TAG, "onProgressUpdate: " + currentProgress);
            }

            @Override
            public void onProgressUpdateEnd(float currentProgress) {
                LogUtil.debug(TAG, "onProgressUpdateEnd: " + currentProgress);
            }

            @Override
            public void onAnimationReset() {
                LogUtil.debug(TAG, "onAnimationReset");
            }

            @Override
            public void onModeChanged(boolean isIndeterminate) {
                LogUtil.debug(TAG, "onModeChanged: " + (isIndeterminate ? "indeterminate" : "determinate"));
            }
        });

        // Test loading animation
        startAnimationThreadStuff(1000);
        final Button button = (Button) findComponentById(ResourceTable.Id_button);
        setShapeElement(button, Color.GRAY.getValue());
        String btnTxt = getButtonText();
        if (btnTxt != null) {
            button.setText(btnTxt);
        }
        button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (progressView.isIndeterminate()) {
                    progressView.setIndeterminate(false);
                    button.setText("Switch to indeterminate");
                } else {
                    progressView.setIndeterminate(true);
                    button.setText("Switch to determinate");
                }
                startAnimationThreadStuff(0);
            }
        });
    }

    private String getButtonText() {
        String txt = null;
        if (null != progressView) {
            if (!progressView.isIndeterminate()) {
                txt = "Switch to indeterminate";
            } else {
                txt = "Switch to determinate";
            }
        }
        return txt;
    }

    public final ShapeElement getShapeElement(int shape, int color, float radius) {
        ShapeElement shapeElement = new ShapeElement();
        shapeElement.setShape(shape);
        shapeElement.setRgbColor(RgbColor.fromArgbInt(/*Utils.getColor(getContext(), color)*/color));
        shapeElement.setCornerRadius(radius);
        return shapeElement;
    }

    private void setShapeElement(Component component, int colorRes) {
        component.setBackground(getShapeElement(ShapeElement.RECTANGLE, colorRes, 5.0f));
    }

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive()) {
            updateThread.interrupt();
        }
        // Start animation after a delay so there's no missed frames while the app loads up
        EventRunner eventRunner = EventRunner.getMainEventRunner();
        EventHandler eh = new EventHandler(eventRunner) {
            @Override
            public void processEvent(InnerEvent event) {
            }
        };

        eh.postTask(new Runnable() {
            @Override
            public void run() {
                if (!progressView.isIndeterminate()) {
                    progressView.setProgress(0f);
                    // Run thread to update progress every quarter second until full
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (progressView.getProgress() < progressView.getMaxProgress()
                                && !Thread.interrupted()) {
                                // Must set progress in UI thread
                                eh.postTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressView.setProgress(progressView.getProgress() + 10);
                                    }
                                });
                                Time.sleep(250);
                            }
                        }
                    });
                    updateThread.start();
                }
                // Alias for resetAnimation, it's all the same
                progressView.startAnimation();
            }
        }, delay);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
