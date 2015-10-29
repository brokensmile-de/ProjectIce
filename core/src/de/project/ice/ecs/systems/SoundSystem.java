package de.project.ice.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by Phil & Marco on 23.10.2015.
 */
public class SoundSystem extends IceSystem
{
    private ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
    private LongMap<String> ids = new LongMap<String>();
    private Music music = null;
    private DelayedRemovalArray<Fade> faders = new DelayedRemovalArray<Fade>();
    private String musicname = new String();

    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);

        faders.begin();

        for (Fade fade : faders)
        {
            if (fade.update(deltaTime))
            {
                faders.removeValue(fade, true);
            }
        }

        faders.end();


    }

    public boolean loadSound(String name)
    {
        FileHandle file = Gdx.files.internal("sounds/" + name + ".mp3");

        if (!file.exists())
        {
            Gdx.app.log(getClass().getSimpleName(), "Sound file doesn'T exist: " + file.path());
            return false;
        }

        Sound sound = Gdx.audio.newSound(file);
        sounds.put(name, sound);

        return true;
    }

    public long playSound(String name)
    {
        if (!sounds.containsKey(name))
        {
            if (!loadSound(name))
            {
                return -1;
            }
        }
        Sound sound = sounds.get(name);
        long playID = sound.play();
        ids.put(playID, name);
        return playID;
    }

    public void stopSound(long playID)
    {
        sounds.get(ids.get(playID)).stop(playID);
    }

    public void resumeSound(long playID)
    {
        sounds.get(ids.get(playID)).resume(playID);
    }

    public void pauseSound(long playID)
    {
        sounds.get(ids.get(playID)).pause(playID);
    }

    public void unloadSounds()
    {
        ids.clear();

        for (Sound sound : sounds.values())
        {
            sound.dispose();
        }

        sounds.clear();
    }


    public void playMusic(String name)
    {
        playMusic(name, true);
    }

    public String getMusic()
    {
        return musicname;
    }

    public Array<String> getSounds()
    {
        return sounds.keys().toArray();
    }


    public void playMusic(String name, boolean loop)
    {
        FileHandle file = Gdx.files.internal("music/" + name + ".mp3");

        if (!file.exists())
        {
            Gdx.app.log(getClass().getSimpleName(), "Music file doesn'T exist: " + file.path());
            return;
        }

        musicname = name;
        Music music = Gdx.audio.newMusic(file);
        music.setLooping(loop);


        if (this.music != null)
        {
            faders.add(Fade.fadeCross(this.music, music));

        }
        else
        {
            faders.add(Fade.fadeIn(music));
        }
        this.music = music;

        music.play();
    }

    public void stopMusic()
    {
        if (music != null)
        {
            faders.add(Fade.fadeOut(music));
            music = null;
        }
    }

    public void pauseMusic()
    {
        music.pause();
    }

    public void resumeMusic()
    {
        music.play();
    }

    private static class Fade
    {
        protected static float DURATION = 3f;
        protected Music music;
        protected float start, end;
        protected float alpha;

        private Fade(Music music, float start, float end)
        {
            this.music = music;
            this.start = start;
            this.end = end;
            this.alpha = 0;
            music.setVolume(start);
        }

        public static Fade fadeIn(Music music)
        {
            return new Fade(music, 0f, 1f);
        }

        public static Fade fadeOut(Music music)
        {
            return new FadeOut(music, 1f, 0f);
        }

        public static Fade fadeCross(Music first, Music second)
        {
            return new FadeCross(first, second);
        }

        public boolean update(float delta)
        {
            alpha += delta / DURATION;
            float volume = Interpolation.pow2.apply(start, end, alpha);
            music.setVolume(volume);

            return alpha >= 1f;
        }
    }

    private static class FadeOut extends Fade
    {
        protected FadeOut(Music music, float start, float end)
        {
            super(music, start, end);
        }

        @Override
        public boolean update(float delta)
        {
            if (super.update(delta))
            {
                music.dispose();
                return true;
            }
            return false;
        }
    }

    private static class FadeCross extends FadeOut
    {
        private Fade fadeIn;

        private FadeCross(Music first, Music second)
        {
            super(first, first.getVolume(), 0f);
            fadeIn = Fade.fadeIn(second);
        }

        @Override
        public boolean update(float delta)
        {
            fadeIn.update(delta);
            return super.update(delta);
        }
    }

}
