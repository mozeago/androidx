/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.core.i18n;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class MessageFormatBenchmarkTest {
    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final int REPEAT_COUNT = 100;

    @Test @MediumTest
    public void testTimePlurals() throws Exception {
        final Locale sr = new Locale("sr");
        for (int i = 0; i < REPEAT_COUNT; ++i) {
            String msg = "{num,plural,offset:1" +
                "  =1    {only {name}}" +
                "  =2    {{name} and one other}" +
                "  one   {{name} and #-one others}" +
                "  few   {{name} and #-few others}" +
                "  other {{name} and #... others}" +
                "}";
            MessageFormat.formatNamedArgs(appContext, sr, msg, "num", i % 9, "name", "Peter");
        }
    }

    @Test @SmallTest
    public void testTimeGenders() throws Exception {
        final String [] genders = { "female", "male", "no_match" };
        for (int i = 0; i < REPEAT_COUNT; ++i) {
            String msg = "{gender,select," +
                "  female {her book}" +
                "  male   {his book}" +
                "  other  {their book}" +
                "}";
            MessageFormat.formatNamedArgs(appContext, Locale.US, msg, "gender", genders[i % 3]);
        }
    }
}
