/*
 * Copyright (c) 2016. Saiy Ltd. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ai.saiy.android.recognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import java.util.regex.Pattern;

import ai.saiy.android.utils.MyLog;
import ai.saiy.android.utils.UtilsString;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

public class SaiyHotwordListener implements RecognitionListener {

    private final boolean DEBUG = MyLog.DEBUG;
    private final String CLS_NAME = SaiyHotwordListener.class.getSimpleName();

    protected static final String OKAY_GOOGLE = "okaygoogle";
    protected static final String WAKEUP_SAIY = "wakeupsay";
    protected static final String STOP_LISTENING = "stoplistening";

    private static final Pattern pOKAY_GOOGLE = Pattern.compile(OKAY_GOOGLE);
    private static final Pattern pWAKEUP_SAIY = Pattern.compile(WAKEUP_SAIY);
    private static final Pattern pSTOP_LISTENING = Pattern.compile(STOP_LISTENING);

    public static final int ERROR_NULL = 1;
    public static final int ERROR_INITIALISE = 2;
    public static final int ERROR_PERMISSIONS = 3;

    private boolean hotwordDetected;

    public void onHotwordInitialised() {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onHotwordInitialised");
        }
        hotwordDetected = false;
    }

    public void onHotwordStarted() {}

    public void onHotwordDetected(@NonNull final String hotword) {}

    public void onHotwordError(final int errorCode) {}

    public void onHotwordShutdown() {}

    @Override
    public void onBeginningOfSpeech() {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onBeginningOfSpeech");
        }
    }

    @Override
    public void onEndOfSpeech() {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onEndOfSpeech");
        }
        System.gc();
    }

    @Override
    public void onPartialResult(final Hypothesis hypothesis) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onPartialResult: hotwordDetected: " + hotwordDetected);
        }

        if (hypothesis != null) {
            if (!hotwordDetected) {
                final String detected = hypothesis.getHypstr();

                if (DEBUG) {
                    MyLog.i(CLS_NAME, "onPartialResult: detected: " + detected);
                }

                if (UtilsString.notNaked(detected)) {
                    hotwordDetected = true;

                    if (pOKAY_GOOGLE.matcher(detected.trim()).matches()) {
                        onHotwordDetected(OKAY_GOOGLE);
                    } else if (pWAKEUP_SAIY.matcher(detected.trim()).matches()) {
                        onHotwordDetected(WAKEUP_SAIY);
                    } else if (pSTOP_LISTENING.matcher(detected.trim()).matches()) {
                        onHotwordDetected(STOP_LISTENING);
                    } else {
                        hotwordDetected = false;
                    }
                }
            }
        }
    }

    @Override
    public void onResult(final Hypothesis hypothesis) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onResult");
        }

        if (hypothesis != null) {
            final String fullText = hypothesis.getHypstr().trim().toLowerCase();

            if (UtilsString.notNaked(fullText)) {
                MyLog.i(CLS_NAME, "Final recognized text: " + fullText);

                Context context = ai.saiy.android.utils.Global.getContext();

                // ============ Call Contact by Name ============
                if (fullText.startsWith("call ")) {
                    String nameToFind = fullText.replaceFirst("call ", "").trim();

                    Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

                    Cursor cursor = context.getContentResolver().query(
                            uri,
                            projection,
                            null,
                            null,
                            null
                    );

                    boolean found = false;

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String contactName = cursor.getString(0).toLowerCase();
                            String phoneNumber = cursor.getString(1);

                            if (contactName.contains(nameToFind)) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phoneNumber));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);

                                MyLog.i(CLS_NAME, "Calling contact: " + contactName + " - " + phoneNumber);
                                found = true;
                                break;
                            }
                        }
                        cursor.close();
                    }

                    if (!found) {
                        MyLog.e(CLS_NAME, "Contact '" + nameToFind + "' not found.");
                    }
                }

                // ============ OTG On Command ============
                if (fullText.equals("otg on")) {
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$StorageSettingsActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        MyLog.i(CLS_NAME, "Opening OTG settings.");
                    } catch (Exception e) {
                        MyLog.e(CLS_NAME, "Failed to open OTG settings: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onError(final Exception e) {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onError");
        }
    }

    @Override
    public void onTimeout() {
        if (DEBUG) {
            MyLog.i(CLS_NAME, "onTimeout");
        }
    }
}
