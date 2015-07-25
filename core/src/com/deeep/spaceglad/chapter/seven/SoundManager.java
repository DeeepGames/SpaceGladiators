package com.deeep.spaceglad.chapter.seven;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Andreas on 7/25/2015.
 */
public class SoundManager {

    public static final float DEFAULT_VOLUME = 1;

    // Singleton instance of the Audio class
    private static SoundManager instance = null;

    //Camera object for reference
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
     * @param position Position of the Sound. This will be referenced to the camera position.
     * @return ID of the Sound played.
     */
    public long playSoundAtPosition(Sound sound, float baseVolume, Vector3 position){
        if(camera == null) return 0;

        float d = Math.min(camera.viewportHeight, camera.position.dst(position));
        System.out.println(d);

        if(d == 0) return sound.play(baseVolume);

        float v = baseVolume / d;
        if(d < 0.01) v = 0;

        return sound.loop(v);
    }

    public void update(){

    }

    /**
     * Plays Music, which scales in volume based on distance and plays stereo according to the camera position.
     * @param music Music to play.
     * @param baseVolume Volume of Music.
     * @param position Position of the Music. This will be referenced to the camera position.
     * @return ID of the Music played.
     */
    public void playMusicAtPosition(Music music, float baseVolume, Vector3 position){
        if(camera == null) return;

        float d = Math.min(camera.viewportHeight, camera.position.dst(position));
        System.out.println(d);

        if(d == 0) {
            music.setVolume(baseVolume);
            music.play();
            return;
        }

        float v = baseVolume / d;
        if(d < 0.01) v = 0;

        music.setVolume(v);
        music.play();
        return;
    }

    class PositionalMusic implements Music{

        private Music music;
        private Vector3 position;
        private float volume;

        /**
         * Contructor
         * @param music Music object to be played
         */
        public PositionalMusic(Music music){
            this.music = music;
        }

        /**
         * Sets the Music position in the world
         * @param position
         */
        public void setPosition(Vector3 position){
            this.position = position;
        }

        /**
         * Updates the position in the world.
         * @param position
         */
        public void update(Vector3 position){
            this.position = position;
            float d = Math.min(camera.viewportHeight, camera.position.dst(position));
            System.out.println(d);

            if(d == 0) {
                music.setVolume(music.getVolume());
                music.play();
                return;
            }

            float v = volume / d;
            if(d < 0.01) v = 0;

            music.setVolume(v);
        }

        @Override
        public void play() {
            music.setVolume(volume);
            music.play();
        }

        @Override
        public void pause() {
            music.pause();
        }

        @Override
        public void stop() {
            music.stop();
        }

        @Override
        public boolean isPlaying() {
            return music.isPlaying();
        }

        @Override
        public void setLooping(boolean isLooping) {
            music.setLooping(isLooping);
        }

        @Override
        public boolean isLooping() {
            return music.isLooping();
        }

        @Override
        public void setVolume(float volume) {
            music.setVolume(volume);
        }

        @Override
        public float getVolume() {
            return music.getVolume();
        }

        @Override
        public void setPan(float pan, float volume) {
            music.setPan(pan, volume);
        }

        @Override
        public void setPosition(float position) {
            music.setPosition(position);
        }

        @Override
        public float getPosition() {
            return music.getPosition();
        }

        @Override
        public void dispose() {
            music.dispose();
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            music.setOnCompletionListener(listener);
        }
    }

}
