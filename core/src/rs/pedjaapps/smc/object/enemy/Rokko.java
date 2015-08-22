package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.Maryo;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Rokko extends Enemy
{
    public static final float POSITION_Z = 0.03f;
    public String direction;
    public float speed;
    private boolean staying = true;
    private float rotation;
    private boolean flipX;
    private float mMinDistanceFront, mMaxDistanceFront, mMaxDistanceSides;
    private ParticleEffect effect;

    public Rokko(World world, Vector2 size, Vector3 position,  String direction)
    {
        super(world, size, position);
        this.speed = 3.5f;//speed;
        this.direction = direction;
        if("left".equals(direction))
        {
            flipX = true;
        }
        else if("down".equals(direction))
        {
            rotation = 90f;
        }
        else if("up".equals(direction))
        {
            rotation = 270f;
        }
        position.z = POSITION_Z;
        mKillPoints = 250;
        mMinDistanceFront = 3.125f;
        mMaxDistanceFront = 15.625f;
        mMaxDistanceSides = 6.25f;
        Assets.manager.load("data/animation/particles/rokko_trail_emitter.p", ParticleEffect.class, Assets.particleEffectParameter);
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.manager.get(textureName);
    }

    @Override
    public void initAssets()
    {
        effect = new ParticleEffect(Assets.manager.get("data/animation/particles/rokko_trail_emitter.p", ParticleEffect.class));
        effect.start();
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        Texture frame = Assets.manager.get(textureName);
        float width = Utility.getWidth(frame, mDrawRect.height);
        float originX = width * 0.5f;
        float originY = mDrawRect.height * 0.5f;
        spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, originX, originY, width, mDrawRect.height, 1, 1, rotation, 0, 0, frame.getWidth(), frame.getHeight(), flipX, false);

        if(!staying)
        {
            effect.setPosition(position.x, position.y);
            effect.draw(spriteBatch/*, Gdx.graphics.getDeltaTime()*/);
        }
    }

    public void update(float deltaTime)
    {
        if (deadByBullet)//TODO not like this
        {
            // Setting initial vertical acceleration
            acceleration.y = Constants.GRAVITY;

            // Convert acceleration to frame time
            acceleration.scl(deltaTime);

            // apply acceleration to change velocity
            velocity.add(acceleration);

            checkCollisionWithBlocks(deltaTime, false, false);
            return;
        }

        if(!staying)
            effect.update(deltaTime);

        stateTime += deltaTime;
        if(staying)
        {
            if(checkMaryoInFront())
            {
                staying = false;
            }
        }
        else if("up".equals(direction))
        {
            velocity.y = speed;
        }
        else if("down".equals(direction))
        {
            velocity.y = -speed;
        }
        else if("right".equals(direction))
        {
            velocity.x = speed;
        }
        else if("left".equals(direction))
        {
            velocity.x = -speed;
        }

        updatePosition(deltaTime);
    }

    private boolean checkMaryoInFront()
    {
        Maryo maryo = world.maryo;
        if(maryo == null)return false;
        Rectangle rect = World.RECT_POOL.obtain();
        float x = 0, y = 0, w = 0, h = 0;
        if("up".equals(direction))
        {
            x = mColRect.x - (mMaxDistanceSides - mColRect.width) * 0.5f;
            y = mColRect.y + mColRect.height + mMinDistanceFront;
            w = mMaxDistanceSides;
            h = mMaxDistanceFront;
        }
        else if("down".equals(direction))
        {
            x = mColRect.x - (mMaxDistanceSides - mColRect.width) * 0.5f;
            y = mColRect.y - mMaxDistanceFront - mMaxDistanceFront;
            w = mMaxDistanceSides;
            h = mMaxDistanceFront;
        }
        else if("left".equals(direction))
        {
            x = mColRect.x - mMinDistanceFront - mMaxDistanceFront;
            y = mColRect.y - (mMaxDistanceSides - mColRect.height) * 0.5f;
            w = mMaxDistanceFront;
            h = mMaxDistanceSides;
        }
        else if("right".equals(direction))
        {
            x = mColRect.x + mColRect.width + mMaxDistanceSides;
            y = mColRect.y - (mMaxDistanceSides - mColRect.height) * 0.5f;
            w = mMaxDistanceFront;
            h = mMaxDistanceSides;
        }
        rect.set(x, y, w, h);
        return maryo.mColRect.overlaps(rect);
    }
}