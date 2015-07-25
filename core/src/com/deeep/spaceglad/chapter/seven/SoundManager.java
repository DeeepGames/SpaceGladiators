package com.deeep.spaceglad.chapter.seven;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import javafx.scene.PerspectiveCamera;

/**
 * Created by Andreas on 7/25/2015.
 */
public class SoundManager {

    // Singleton instance of the Audio class
    private static SoundManager instance = null;

    //Camera object
    private static PerspectiveCamera camera = null;

    // Static sound objects
    public Music defaultMusic;

    // Dynamic sound objects
    public Sound enemyGrowl;

    /***
     * Sets the camera for use in positional play.
     * @param camera Player camera.
     */
    public static void setCamera(PerspectiveCamera camera) { SoundManager.camera = camera; }

    /**
     * @return The singleton instance of the SoundManager class.
     */
    public static SoundManager getInstance(){
        if(instance == null) instance = new SoundManager();
        return instance;
    }

    /**
     * Private constructor. Must only be called from inside the class and only one instance (private static Audio instance).
     */
    private SoundManager(){
        initialize();
    }

    /**
     * Initializes all objects. This is not done in the constructor as NULL exceptions will occur if
     * the singleton is ever referenced in the initialization process.
     */
    private void initialize(){
        defaultMusic = Gdx.audio.newMusic(Gdx.files.internal("defaultMusic.mp3"));
        defaultMusic.setLooping(true);

        enemyGrowl = Gdx.audio.newSound(Gdx.files.internal("enemyGrowl.mp3"));
    }

    /**
     * Disposes all the music objects to free memory. This is efficient as Sound objects load their
     * audio files fully into RAM.
     */
    private void disposeSounds(){
        enemyGrowl.dispose();
    }

    /**
     * Disposes all the music objects to free memory. This is not efficient as Music objects stream
     * their audio files rather than load it fully into RAM.
     */
    private void disposeMusic(){
        defaultMusic.dispose();
    }

    /**
     * Disposes both Music and Sound objects to free up memory.
     */
    private void disposeAll(){
        disposeMusic();
        disposeSounds();
    }

    /**
     * Plays Sound, which scales in volume based on distance and plays stereo according to the camera position.
     * @param sound Sound to play.
     * @param baseVolume Volume of Sound.
     * @param position Position of the camera.
     * @return ID of the Sound played.
     */
    private int playSoundAtPosition(Sound sound, float baseVolume, Vector3 position){
        if(camera == null) return 0;
        return 0;
    }

    /**
     * Plays Music, which scales in volume based on distance and plays stereo according to the camera position.
     * @param sound Music to play.
     * @param baseVolume Volume of Music.
     * @param position Position of the camera.
     * @return ID of the Music played.
     */
    private int playMusicAtPosition(Sound sound, float baseVolume, Vector3 position){
        if(camera == null) return 0;
        camera.position
        return 0;
    }

}
