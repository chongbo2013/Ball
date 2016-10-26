package com.moxiu.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;

import static com.moxiu.ball.PhysicsConfig.SHAPE_TYPE_CIRCLE;


/**
 * 尼玛有点一个，2个小球，为毛不发生碰撞，直接穿透过去了
 * Created by ferris.xu on 2016/10/21.
 */

public class BallView extends View {
    public static final float NO_GRAVITY = 0.0f;
    public static final float MOON_GRAVITY = -1.6f;
    public static final float EARTH_GRAVITY = -9.8f;
    public static final float JUPITER_GRAVITY = -24.8f;
    private World world;
    private float gravityX = 0.0f;
    private float gravityY = EARTH_GRAVITY;
    private int width;
    private int height;
    private float boundsSize;
    //Size in DP of the bounds (world walls) of the view
    private static final int BOUND_SIZE_DP = 20;
    private static final float FRAME_RATE = 1 / 60f;
    private float density;
    private float pixelsPerMeter;
    private ArrayList<Body> bounds = new ArrayList<>();
    private int velocityIterations = 8;
    private int positionIterations = 3;
    private Paint debugPaint;

    public BallView(Context context) {
        super(context);

    }

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public BallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void init() {
        density = getResources().getDisplayMetrics().density;
        boundsSize = BOUND_SIZE_DP * density;
        pixelsPerMeter = getResources()
                .getDimensionPixelSize(R.dimen.physics_layout_dp_per_meter);
        world = new World(new Vec2(gravityX, gravityY),true);
        createTopAndBottomBounds();
        createLeftAndRightBounds();
        bounds.add(createBall());
        bounds.add(createBall2());
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setStyle(Paint.Style.FILL);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        init();
    }

    //绘制墙壁的
    private void createTopAndBottomBounds() {
        int boundSize = Math.round(boundsSize);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        //多边形形状
        PolygonShape box = new PolygonShape();
        int boxWidth = (int) pixelsToMeters(width);
        int boxHeight = (int) pixelsToMeters(boundSize);
        box.setAsBox(boxWidth, boxHeight);
        //属性定义
        FixtureDef fixtureDef = new FixtureDef();
        //形状
        fixtureDef.shape = box;
        //密度
        fixtureDef.density = 0.5f;
        //摩擦力
        fixtureDef.friction = 0.3f;
        //弹力
        fixtureDef.restitution = 0.5f;


        fixtureDef.userData = "physics_layout_bound_top";
        //设置位置
        bodyDef.position.set(0,0);
        //根据创建出物理
        Body topBody = world.createBody(bodyDef);
        //设置属性
        topBody.createFixture(fixtureDef);
        //存起来
        bounds.add(topBody);

        //创建底部边界
        fixtureDef.userData = "physics_layout_body_bottom";
        bodyDef.position.set(0, pixelsToMeters(-height) );
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bounds.add(bottomBody);
    }

    private void createLeftAndRightBounds() {
        int boundSize = Math.round(boundsSize);
        int boxWidth = (int) pixelsToMeters(boundSize);
        int boxHeight = (int) pixelsToMeters(height);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        //密度
        fixtureDef.density = 0.5f;
        //摩擦力
        fixtureDef.friction = 0.3f;
        //弹力
        fixtureDef.restitution = 0.5f;

        fixtureDef.userData = "physics_layout_body_left";
        bodyDef.position.set(-boxWidth,0);
        Body leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);
        bounds.add(leftBody);

        fixtureDef.userData = "physics_layout_body_right";
        bodyDef.position.set(pixelsToMeters(width)+boxWidth, 0);
        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
        bounds.add(rightBody);
    }


    private Body createBall(){
        PhysicsConfig config = PhysicsConfig.create();

        config.shapeType=SHAPE_TYPE_CIRCLE;
        config.radius=pixelsToMeters(48f);
        config.bodyDef.type = BodyType.DYNAMIC;
        config.bodyDef.fixedRotation = false;
        //摩擦
        config.fixtureDef.friction =  0.5f;
        //弹性
        config.fixtureDef.restitution = 0.3f;
        config.fixtureDef.density = 1f;

        BodyDef bodyDef = config.bodyDef;
        bodyDef.position.set(pixelsToMeters(width/2),
                pixelsToMeters(0));
//        bodyDef.angularVelocity = degreesToRadians(3);
        FixtureDef fixtureDef = config.fixtureDef;
        fixtureDef.shape = config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE
                ? createBoxShape(96f,96f) : createCircleShape(96f,96f, config);
        fixtureDef.userData ="physics_layout_body_ball_a";

        Body body = world.createBody(bodyDef);
        body.setBullet(true);

        body.createFixture(fixtureDef);


        return body;
    }

    private Body createBall2(){
        PhysicsConfig config = PhysicsConfig.create();

        config.shapeType=SHAPE_TYPE_CIRCLE;
        config.radius=pixelsToMeters(48f);
        config.bodyDef.type = BodyType.DYNAMIC;
        config.bodyDef.fixedRotation = false;
        //摩擦
        config.fixtureDef.friction =  0.5f;
        //弹性
        config.fixtureDef.restitution = 0.3f;
        config.fixtureDef.density = 1f;

        BodyDef bodyDef = config.bodyDef;
        bodyDef.position.set(pixelsToMeters(width/2),
                pixelsToMeters(-300));
//        bodyDef.angularVelocity = degreesToRadians(3);
        FixtureDef fixtureDef = config.fixtureDef;
        fixtureDef.shape = config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE
                ? createBoxShape(pixelsToMeters(96f),pixelsToMeters(96f)) : createCircleShape(pixelsToMeters(96f),pixelsToMeters(96f), config);
        fixtureDef.userData ="physics_layout_body_ball_b";
        Body body = world.createBody(bodyDef);
        body.setBullet(true);
        body.createFixture(fixtureDef);


        return body;
    }

    private PolygonShape createBoxShape(float w,float h) {
        PolygonShape box = new PolygonShape();
        float boxWidth = pixelsToMeters(w / 2);
        float boxHeight = pixelsToMeters( h / 2);
        box.setAsBox(boxWidth, boxHeight);
        return box;
    }

    private CircleShape createCircleShape(float w,float h, PhysicsConfig config) {
        CircleShape circle = new CircleShape();
        //radius was not set, set it to max of the width and height
        if (config.radius == -1) {
            config.radius = Math.max(w / 2,h / 2);
        }

        circle.m_radius = pixelsToMeters(48f);
        return circle;
    }

    public float pixelsToMeters(float pixels) {
        return pixels / pixelsPerMeter;
    }


    /**
     * Call this every time your view gets a call to onSizeChanged so that the world can
     * respond to this change.
     */
    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }


    /**
     * Set the number of velocity iterations the world will perform at each step.
     * Default is 8
     *
     * @param velocityIterations number of iterations
     */
    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public int getVelocityIterations() {
        return velocityIterations;
    }

    public float metersToPixels(float meters) {
        return meters * pixelsPerMeter;
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

    boolean debugDraw = true;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        world.step(FRAME_RATE, 10, 8);

        //                float x=metersToPixels(body.getPosition().x) - boxWidth / 2;
//                float y=metersToPixels(body.getPosition().y) - boxHeight / 2;
//               float rotation=radiansToDegrees(body.getAngle()) % 360;
        for (int i = 0; i < bounds.size(); i++) {
            Body body = bounds.get(i);
            if (debugDraw) {
                Fixture mFixtrue = body.getFixtureList();
                int boundSize = Math.round(boundsSize);
                int boxWidth = (int) pixelsToMeters(width);
                int boxHeight = (int) pixelsToMeters(boundSize);
                if (mFixtrue.getUserData().equals("physics_layout_bound_top")) {
                    float x = body.getPosition().x;
                    float y = body.getPosition().y;
                    canvas.drawRect(metersToPixels(body.getPosition().x), metersToPixels(body.getPosition().y), metersToPixels(body.getPosition().x) + width, metersToPixels(body.getPosition().y) + boundSize, debugPaint);
                }else if(mFixtrue.getUserData().equals("physics_layout_body_bottom")){
                    float x = body.getPosition().x;
                    float y = body.getPosition().y;
                    float releateX=metersToPixels(body.getPosition().x);
                    float releateY=-metersToPixels(body.getPosition().y);
                    canvas.drawRect(releateX, releateY-boundSize, releateX + width, releateY , debugPaint);
                }else if(mFixtrue.getUserData().equals("physics_layout_body_ball_a")){
                    float x = body.getPosition().x;
                    float y = body.getPosition().y;
                    float releateX=metersToPixels(body.getPosition().x);
                    float releateY=-metersToPixels(body.getPosition().y);
                    float rotation=radiansToDegrees(body.getAngle()) % 360;
                    canvas.save();
                    float centerX=(releateX + 96f)/2;
                    float centerY=(releateY + 96f)/2;
                    canvas.rotate(rotation,-centerX,-centerY);
                    canvas.drawRect(releateX, releateY-96, releateX + 96f, releateY , debugPaint);
                    canvas.restore();
                }else if(mFixtrue.getUserData().equals("physics_layout_body_ball_b")){
                    float x = body.getPosition().x;
                    float y = body.getPosition().y;
                    float releateX=metersToPixels(body.getPosition().x);
                    float releateY=-metersToPixels(body.getPosition().y);
                    float rotation=radiansToDegrees(body.getAngle()) % 360;
                    canvas.save();
                    float centerX=(releateX + 96f)/2;
                    float centerY=(releateY + 96f)/2;
                    canvas.rotate(rotation,-centerX,-centerY);
                    canvas.drawRect(releateX, releateY-96, releateX + 96f, releateY , debugPaint);
                    canvas.restore();
                }

            }
        }
        canvas.restore();
        postInvalidate();

    }

    public void onReleased(Body body,float x,float y,float xvel, float yvel){
        if (body != null) {
            translateBodyToView(body, x,y);
            body.setLinearVelocity(new Vec2(pixelsToMeters(xvel), pixelsToMeters(yvel)));
            body.setAwake(true);
        }
    }
    private void translateBodyToView(@NonNull Body body, float x,float y) {
        body.setTransform(
                new Vec2(pixelsToMeters(x),
                        pixelsToMeters(y)),
                body.getAngle());
    }
    private void disableBounds() {
        for (Body body : bounds) {
            world.destroyBody(body);
        }
        bounds.clear();
    }
}
