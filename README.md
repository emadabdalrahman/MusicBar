<p align="center">
  <img src="https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/sound-bars-pulse.png?raw=true" alt="MusicBar Logo"/>
</p>

# MusicBar  [ ![](https://api.bintray.com/packages/emad/maven/MusicBar/images/download.svg) ](https://bintray.com/emad/maven/MusicBar/_latestVersion) ![](https://img.shields.io/badge/minSdkVersion-15-orange.svg)  [![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-MusicBar-green.svg?style=flat )]( https://android-arsenal.com/details/1/7371 )

![](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/full-optimize.gif?raw=true)

## Setup

```groovy
dependencies {
      implementation 'com.oze.music:MusicBar:1.0.5'
}
```
## Usage

Function | Description
------------ | -------------
setAnimationChangeListener(OnMusicBarAnimationChangeListener listener) | animation listener
setProgressChangeListener(OnMusicBarProgressChangeListener listener) | progress listener
removeAllListener() | remove Progress and Animation listener
loadFrom(InputStream stream, int duration) | take the music file InputStream with music duration in millisecond
loadFrom(String pathname, int duration) | take the music file path with music duration in millisecond
show() | start show animation
hide() | start hide animation
startAutoProgress(float playbackSpeed) | start auto play animation should be called after loadFrom() and media player finished prepare if startAutoProgress() called before loadFrom() it will throw exception because duration is 0. every time loadFrom() call you will need to recall startAutoProgress()  **playbackSpeed** playback speed from media player default value 1.0F for MediaPlayer and ExoPlayer
stopAutoProgress() | stop auto progress animation
isHide() | return true if hide
isShow() | return true if show
isAutoProgress() | return true if auto progress 
setProgress(int position) | move to specified position (in milisecand) 
getPosition() | return current progress position
setSpaceBetweenBar(int spaceBetweenBar) | change distance between bars (in px) **default 2** Recommend to make spaceBetweenBar equal barWidth if you use FixedMusicBar
setBarWidth(float barWidth) | change bar width (in px) **default 2 for FixedMusicBar and 3 for ScrollableMusicBar** Recommend to make barWidth equal spaceBetweenBar if you use FixedMusicBar 
setLoadedBarPrimeColor(int color) | change top progressed bar color **default #fb4c01** 
setBackgroundBarPrimeColor(int color) | change top unprogressed bar color **default #dfd6d6**


Only in ScrollableMusicBar 

Function | Description
------------ | -------------
setDivided(boolean divided) | Set if music bar divided or not **default false**
setDividerSize(float size) | Set divider size in px **default 2** use when view is divided
setLoadedBarSecondaryColor(int color) | change bottom progressed bar color **default #eca277** use when view is divided
setBackgroundBarSecondaryColor(int color) | change bottom unprogressed bar color **default #c4bbbb** use when view is divided

**XML** 

for ScrollableMusicBar

![ScrollableMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/imgonline-com-ua-collage-6MmaF0pDG9O9.jpg?raw=true)

```XML
   <com.oze.music.musicbar.ScrollableMusicBar
        android:id="@+id/ScrollableMusicBar"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@android:color/white"
        android:padding="8dp"
        app:divided="true"                               // to divide each bar default false
        app:dividerSize="2"                              // divide sice in px default 2
        app:barWidth="3"                                 // bar width default 3
        app:spaceBetweenBar="2"                          // distance between each bar default 2
        app:backgroundBarPrimeColor="#dfd6d6"
        app:backgroundBarSecondaryColor="#c4bbbb"
        app:LoadedBarPrimeColor="#fb4c01"
        app:LoadedBarSecondaryColor="#eca277"/>
```
OR FixedMusicBar 

![FixedMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/MiniMusicBar.png?raw=true) 
```XML
    <com.oze.music.musicbar.FixedMusicBar
        android:id="@+id/FixedMusicBar"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:padding="8dp"
        android:background="@android:color/white"
        app:barWidth="2"                           // bar width default 2
        app:spaceBetweenBar="2"                    // distance between each bar default 2
        app:backgroundBarPrimeColor="#dfd6d6"
        app:LoadedBarPrimeColor="#fb4c01"/>
```
**Java**
```java
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ScrollableMusicBar musicBar = findViewById(R.id.ScrollableMusicBar);
        //or  
        FixedMusicBar musicBar = findViewById(R.id.FixedMusicBar);
        
        //add animation listener
        musicBar.setAnimationChangeListener(mOnMusicBarAnimationChangeListener);
        
        //add progress listener
        musicBar.setProgressChangeListener(mOnMusicBarProgressChangeListener);
        
         // String path = the music file path
        // int duration = the music file duration time in millisecond [mediaPlayer.getDuration()]
        musicBar.loadFrom(path,duration)
        //or use inputstream 
        musicBar.loadFrom(getResources().openRawResource(R.raw.music),duration());
        
        //change progress 
        musicBar.setProgress(50)
        
        //start show animation
        musicBar.show()
        
        //start hide animation
        musicBar.hide()
        
        //change bar width
        musicBar.setBarWidth(2);
        
        //change Space Between Bars
        musicBar.setSpaceBetweenBar(2); //Recommend to make spaceBetweenBar equal barWidth if you use FixedMusicBar
       
        // Set if music bar divided or not default value false.
        musicBar.setDivided(true);
        
        //Set divider size in px default value 2
        musicBar.setDividerSize();
        
        // for changing color scheme
        musicBar.setBackgroundBarPrimeColor(getResources().getColor(R.color.BackgroundBarPrimeColor);
        musicBar.setBackgroundBarSecondaryColor(getResources().getColor(R.color.BackgroundBarSecondaryColor);
        musicBar.setLoadedBarPrimeColor(getResources().getColor(R.color.LoadedBarPrimeColor);
        musicBar.setLoadedBarSecondaryColor(getResources().getColor(R.color.LoadedBarSecondaryColor);


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                musicBar.startAutoProgress(1.0f);
            }
        });
        
        //stop auto progress animation
        musicBar.stopAutoProgress();
        
    }

```


**AnimationListener**
```Java
MusicBar.OnMusicBarAnimationChangeListener mOnMusicBarAnimationChangeListener = new MusicBar.OnMusicBarAnimationChangeListener() {
        @Override
        public void onHideAnimationStart() {
            Log.i(TAG, "onHideAnimationStart");
        }

        @Override
        public void onHideAnimationEnd() {
            Log.i(TAG, "onHideAnimationEnd");
        }

        @Override
        public void onShowAnimationStart() {
            Log.i(TAG, "onShowAnimationStart");

        }

        @Override
        public void onShowAnimationEnd() {
            Log.i(TAG, "onShowAnimationEnd");
        }
    };
```
**ProgressListener**
```Java
 MusicBar.OnMusicBarProgressChangeListener mOnMusicBarProgressChangeListener = new MusicBar.OnMusicBarProgressChangeListener() {
        @Override
        public void onProgressChanged(MusicBar musicBar, int progress, boolean fromUser) {
            Log.i(TAG, "onProgressChanged");
        }

        @Override
        public void onStartTrackingTouch(MusicBar musicBar) {
         Log.i(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(MusicBar musicBar) {
            Log.i(TAG, "onStopTrackingTouch");
        }
    };
```
**Animation**

![animation](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/animation-optimize.gif?raw=true)
