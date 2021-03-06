/* Copyright 2015 Samsung Electronics Co., LTD
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

package org.gearvrf.modelviewer2;

import android.util.Log;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimator;
import org.gearvrf.util.BoundingBoxCreator;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {
    String name;
    String location;

    GVRSceneObject model;
    ArrayList<GVRMaterial> originalMaterial;
    GVRAnimator animation;
    private float currentZoom = 0;


    private static final String TAG = "Model";

    public Model(String name, String location) {
        this.name = name;
        this.location = location;
    }

    String getModelName() {
        return name;
    }

    private void saveRenderData() {
        originalMaterial = new ArrayList<GVRMaterial>();
        ArrayList<GVRRenderData> rdata = model.getAllComponents(GVRRenderData.getComponentType());
        for (GVRRenderData r : rdata) {
            originalMaterial.add(r.getMaterial());
        }
    }

    private void loadModel(GVRContext context) {
        try {
            Log.d(TAG, "Absent so loading" + name);
            model = context.getAssetLoader().loadModel("sd:" + location);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to load model");
            return;
        }

        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        model.attachComponent(new GVRMeshCollider(context, true));

        // Adding Pointee to Model
        bv = model.getBoundingVolume();
        float originalRadius = bv.radius;
        Log.i(TAG, "Radius" + Float.toString(originalRadius));

        // TODO Scale Appropriately
        if (originalRadius > 7.0f || originalRadius < 5.0f) {
            float scaleFactor = 7 / originalRadius;
            model.getTransform().setScale(scaleFactor, scaleFactor, scaleFactor);
        }

        // Make Copy of Original Render Data
        saveRenderData();

        // Load Animations
        animation = (GVRAnimator) model.getComponent(GVRAnimator.getComponentType());
    }

    public GVRAnimator getAnimation() {
        return animation;
    }

    public GVRSceneObject getModel(GVRContext context) {
        if (model == null) {
            loadModel(context);
        }
        return model;
    }

    public float getCurrentZoom() {
        return currentZoom;
    }

    public void setCurrentZoom(float zoom) {
        currentZoom = zoom;
    }
}
