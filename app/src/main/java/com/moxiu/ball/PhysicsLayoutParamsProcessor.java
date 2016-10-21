package com.moxiu.ball;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.jbox2d.dynamics.BodyType;

/**
 * Processes attributes from any PhysicsLayout and returns a {@link com.jawnnypoo.physicslayout.PhysicsConfig}
 */
public class PhysicsLayoutParamsProcessor {


    /**
     * Processes the attributes on children
     * @param c context
     * @param attrs attributes
     * @return the PhysicsConfig
     */
    public static PhysicsConfig process(Context c) {
        PhysicsConfig config = PhysicsConfig.create();
        processCustom(config);
        processBodyDef( config);
        processFixtureDef( config);
        return config;
    }

    @SuppressWarnings("WrongConstant")
    private static void processCustom(PhysicsConfig config) {
            config.shapeType = PhysicsConfig.SHAPE_TYPE_CIRCLE;
            config.radius = 48f;

    }

    private static void processBodyDef(PhysicsConfig config) {
            config.bodyDef.type =  BodyType.DYNAMIC;
            config.bodyDef.fixedRotation = false;
    }

    private static void processFixtureDef( PhysicsConfig config) {

            config.fixtureDef.friction = 0.2f;


            config.fixtureDef.restitution = 0.3f;


            config.fixtureDef.density = 7;

    }
}