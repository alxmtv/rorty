package by.matveev.rorty.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.util.List;

public class Animation {


    public List<Frame> frames = new ArrayList<>();
    public Frame currentFrame;
    public int currentIndex;
    public float totalTime;
    public boolean repeat = true;
    public boolean completed;
    public float currentTime;
    private int frameWidth;
    private int frameHeight;

    public Animation() {
    }

    public Animation(int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public void update(float dt) {
        if (this.completed) {
            if (this.repeat) {
                this.completed = false;
                this.currentIndex = 0;
            }
        }

        if (this.frames.size() > 0) {
            currentTime += dt;

            if (this.currentTime >= this.totalTime) {
                this.currentTime = this.currentTime % this.totalTime;
                this.currentIndex = 0;
            }

            while (this.currentTime > this.frames.get(this.currentIndex).duration) {
                this.currentIndex++;
            }

            this.completed = this.currentIndex >= this.frames.size() - 1;
            this.currentFrame = frames.get(currentIndex);
        }
    }

    public void draw(Batch batch, Texture texture, float x, float y, float w, float h) {
        batch.draw(texture, x, y, w, h,
                currentFrame.indexX * frameWidth,
                currentFrame.indexY * frameHeight,
                frameWidth, frameHeight,
                currentFrame.flipX, currentFrame.flipY);
    }

    public void add(Frame frame) {
        totalTime += frame.duration;
        frame.duration = totalTime;
        this.frames.add(frame);
        currentFrame = frames.get(0);
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public static class Frame {
        public int indexX;
        public int indexY;
        public boolean flipX;
        public boolean flipY;
        private float duration;


        public Frame(int indexY, int indexX) {
            this(indexX, indexY, false, false);
        }

        public Frame(int indexX, int indexY, boolean flipX, boolean flipY) {
            this.indexX = indexX;
            this.indexY = indexY;
            this.flipX = flipX;
            this.flipY = flipY;
        }

        public Frame() {
        }

        public int getIndexY() {
            return indexY;
        }

        public void setIndexY(int indexY) {
            this.indexY = indexY;
        }

        public int getIndexX() {
            return indexX;
        }

        public void setIndexX(int indexX) {
            this.indexX = indexX;
        }

        public boolean isFlipX() {
            return flipX;
        }

        public void setFlipX(boolean flipX) {
            this.flipX = flipX;
        }

        public boolean isFlipY() {
            return flipY;
        }

        public void setFlipY(boolean flipY) {
            this.flipY = flipY;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }
    }

}