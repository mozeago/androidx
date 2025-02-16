/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.car.app.hardware.climate;

import static android.car.VehiclePropertyIds.HVAC_POWER_ON;

import static androidx.car.app.hardware.climate.AutomotiveCarClimate.DEFAULT_SAMPLE_RATE_HZ;
import static androidx.car.app.hardware.climate.ClimateProfileRequest.FEATURE_HVAC_POWER;
import static androidx.car.app.hardware.common.CarValue.STATUS_SUCCESS;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.car.Car;
import android.car.hardware.property.CarPropertyManager;

import androidx.annotation.NonNull;
import androidx.car.app.hardware.common.CarPropertyResponse;
import androidx.car.app.hardware.common.CarValue;
import androidx.car.app.hardware.common.CarZone;
import androidx.car.app.hardware.common.OnCarPropertyResponseListener;
import androidx.car.app.hardware.common.PropertyManager;
import androidx.car.app.shadows.car.ShadowCar;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = {ShadowCar.class})
@DoNotInstrument
public class AutomotiveCarClimateTest {
    private List<CarPropertyResponse<?>> mResponse;
    private CountDownLatch mCountDownLatch;
    private final Executor mExecutor = directExecutor();
    private AutomotiveCarClimate mAutomotiveCarClimate;
    private CarZone mCarZone;
    @Mock
    private Car mCarMock;
    @Mock
    private CarPropertyManager mCarPropertyManagerMock;
    @Mock
    private PropertyManager mPropertyManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ShadowCar.setCar(mCarMock);
        when(mCarMock.getCarManager(anyString())).thenReturn(mCarPropertyManagerMock);
        mAutomotiveCarClimate = new AutomotiveCarClimate(mPropertyManager);
        mCountDownLatch = new CountDownLatch(1);
        mResponse = new ArrayList<>();
        mCarZone = new CarZone.Builder().build();
    }

    @Test
    public void getHvacPower_verifyResponse() throws InterruptedException {
        CarClimateFeature.Builder mCarClimateBuilder = new CarClimateFeature.Builder(
                FEATURE_HVAC_POWER);
        mCarClimateBuilder.addCarZones(mCarZone);
        CarClimateFeature mCarClimateFeature = new CarClimateFeature(mCarClimateBuilder);
        RegisterClimateStateRequest.Builder builder =
                new RegisterClimateStateRequest.Builder(false);
        builder.addClimateRegisterFeatures(mCarClimateFeature);

        AtomicReference<CarValue<Boolean>> loadedResult = new AtomicReference<>();
        CarClimateStateCallback listener = new CarClimateStateCallback() {
            @Override
            public void onHvacPowerStateAvailable(@NonNull CarValue<Boolean> hvacPowerState) {
                loadedResult.set(hvacPowerState);
                mCountDownLatch.countDown();
            }
        };

        mAutomotiveCarClimate.registerClimateStateCallback(mExecutor, builder.build(), listener);

        Map<Integer, List<CarZone>> propertyIdsWithCarZones =
                ImmutableMap.<Integer, List<CarZone>>builder().put(HVAC_POWER_ON,
                        Collections.singletonList(mCarZone)).buildKeepingLast();

        ArgumentCaptor<OnCarPropertyResponseListener> captor = ArgumentCaptor.forClass(
                OnCarPropertyResponseListener.class);
        verify(mPropertyManager).submitRegisterListenerRequest(eq(propertyIdsWithCarZones),
                eq(DEFAULT_SAMPLE_RATE_HZ), captor.capture(), eq(mExecutor));

        mResponse.add(CarPropertyResponse.builder().setPropertyId(HVAC_POWER_ON).setCarZones(
                Collections.singletonList(mCarZone)).setValue(true).setStatus(
                STATUS_SUCCESS).build());

        captor.getValue().onCarPropertyResponses(mResponse);
        mCountDownLatch.await();

        CarValue<Boolean> carValue = loadedResult.get();
        assertThat(carValue.getValue()).isEqualTo(true);
        assertThat(carValue.getCarZones()).isEqualTo(Collections.singletonList(mCarZone));
        assertThat(carValue.getStatus()).isEqualTo(STATUS_SUCCESS);
    }
}
