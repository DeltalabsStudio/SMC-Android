package rs.pedjaapps.smc.object.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import rs.pedjaapps.smc.Assets;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.Utility;

/**
 * Created by pedja on 18.5.14..
 */
public class Gee extends Enemy
{
    public static final float POSITION_Z = 0.088f;
    public static String DEAD_KEY;
    public String direction;
    public float flyDistance, flySpeed;
    private Vector3 mOriginPosition;
    private boolean forward, dirForward, staying = true, fireResistant;
    private float currWaitTime, waitTime;
    private Color color;
    public int mKillPoints;
    private boolean flipx;

    public Gee(World world, Vector2 size, Vector3 position, float flyDistance, String color, String direction, float waitTime)
    {
        super(world, size, position);
        this.direction = direction;
        mOriginPosition = new Vector3(position);
        this.flyDistance = flyDistance;
        this.waitTime = waitTime;
        switch (color)
        {
            case "yellow":
                this.color = new Color(1, 0.960784314f, 0.039215686f, 1);
                flySpeed = 2.881f;
                mKillPoints = 50;
                fireResistant = false;
                break;
            case "red":
                this.color = new Color(1, 0.156862745f, 0.078431373f, 1);
                flySpeed = 3.865f;
                mKillPoints = 100;
                fireResistant = true;
                break;
            case "green":
                this.color = new Color(0.078431373f, 0.992156863f, 0.078431373f, 1);
                flySpeed = 4.831f;
                mKillPoints = 200;
                fireResistant = false;
                break;
        }
        position.z = POSITION_Z;
    }

    @Override
    protected TextureRegion getDeadTextureRegion()
    {
        return Assets.loadedRegions.get(DEAD_KEY);
    }

    @Override
    public void initAssets()
    {
        DEAD_KEY = textureAtlas + ":dead";
        TextureAtlas atlas = Assets.manager.get(textureAtlas);
        Array<TextureAtlas.AtlasRegion> frames = atlas.getRegions();
        frames.add(frames.removeIndex(1));
        Assets.loadedRegions.put(DEAD_KEY, frames.get(4));
        Assets.animations.put(textureAtlas, new Animation(0.14f, frames));
    }

    @Override
    public void draw(SpriteBatch spriteBatch)
    {
        TextureRegion frame = Assets.animations.get(textureAtlas).getKeyFrame(stateTime, true);
        float width = Utility.getWidth(frame, mDrawRect.height);
        frame.flip(flipx, false);//flip
        spriteBatch.draw(frame, mDrawRect.x, mDrawRect.y, width, mDrawRect.height);
        frame.flip(flipx, false);//return
    }

    public void update(float deltaTime)
    {
        if (deadByBullet)
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

        stateTime += deltaTime;
        if (staying)
        {
            currWaitTime += deltaTime;
            if (currWaitTime >= waitTime)
            {
                staying = false;
                currWaitTime = 0;
                if(forward)
                {
                    forward = false;
                }
                else
                {
                    forward = true;
                    dirForward = MathUtils.randomBoolean();
                }
            }
        }
        else if ("horizontal".equals(direction))
        {
            if (dirForward)//right
            {
                float remainingDistance = (mOriginPosition.x + flyDistance) - position.x;
                if (forward)
                {
                    flipx = true;
                    if(remainingDistance <= 0)
                    {
                        staying = true;
                        velocity.x = 0;
                    }
                    else
                    {
                        velocity.x = flySpeed;
                    }

                }
                else
                {
                    flipx = false;
                    if (remainingDistance >= flyDistance)
                    {
                        staying = true;
                        velocity.x = 0;
                    }
                    else
                    {
                        velocity.x = -flySpeed;
                    }
                }
            }
            else//left
            {
                float remainingDistance = position.x - (mOriginPosition.x - flyDistance);
                if (forward)
                {
                    flipx = false;
                    if(remainingDistance <= 0)
                    {
                        staying = true;
                        velocity.x = 0;
                    }
                    else
                    {
                        velocity.x = -flySpeed;
                    }
                }
                else
                {
                    flipx = true;
                    if (remainingDistance >= flyDistance)
                    {
                        staying = true;
                        velocity.x = 0;
                    }
                    else
                    {
                        velocity.x = flySpeed;
                    }
                }
            }
        }
        else if ("vertical".equals(direction))
        {
            if (dirForward)//up
            {
                float remainingDistance = mOriginPosition.y + flyDistance - position.y;
                if (forward)
                {
                    flipx = true;
                    if(remainingDistance <= 0)
                    {
                        staying = true;
                        velocity.y = 0;
                    }
                    else
                    {
                        velocity.y = flySpeed;
                    }
                }
                else
                {
                    flipx = false;
                    if (remainingDistance >= flyDistance)
                    {
                        staying = true;
                        velocity.y = 0;
                    }
                    else
                    {
                        velocity.y = -flySpeed;
                    }
                }
            }
            else//down
            {
                float remainingDistance = flyDistance - (mOriginPosition.y - position.y);
                if (forward)
                {
                    flipx = false;
                    if(remainingDistance <= 0)
                    {
                        staying = true;
                        velocity.y = 0;
                    }
                    else
                    {
                        velocity.y = -flySpeed;
                    }
                }
                else
                {
                    flipx = true;
                    if (remainingDistance >= flyDistance)
                    {
                        staying = true;
                        velocity.y = 0;
                    }
                    else
                    {
                        velocity.y = flySpeed;
                    }
                }
            }
        }

        updatePosition(deltaTime);
    }
}