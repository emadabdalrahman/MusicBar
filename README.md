<p align="center">
  <img src="https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/sound-bars-pulse.png?raw=true" alt="MusicBar Logo"/>
</p>

# MusicBar  [ ![Download](https://api.bintray.com/packages/emad/maven/MusicBar/images/download.svg) ](https://bintray.com/emad/maven/MusicBar/_latestVersion) ![](https://img.shields.io/badge/minSdkVersion-15-orange.svg)

![](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/full-optimize.gif?raw=true)

## Setup

```groovy
dependencies {
      implementation 'com.oze.music:MusicBar:1.0.1'
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
setProgress(int position) | move to specified position (in milisecand) 
getPosition() | return current progress position
setLoadedBarColor(int color) | change progressed bar color **default RED**
setBackgroundBarColor(int color) | change unprogressed bar color **default #dfd6d6**
setSpaceBetweenBar(int spaceBetweenBar) | change distance between bars (in px) **default 2** Recommend to make spaceBetweenBar equal barWidth
setBarWidth(float barWidth) | change bar width (in px) **default 2** Recommend to make barWidth equal spaceBetweenBar


**XML** 

for ScrollableMusicBar

![BigMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/BigMusicBar.png?raw=true)
```XML
   <com.oze.music.musicbar.ScrollableMusicBar
        android:id="@+id/ScrollableMusicBar"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@android:color/white"
        android:padding="8dp" />
```
OR FixedMusicBar 

![MiniMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/MiniMusicBar.png?raw=true) 
```XML
    <com.oze.music.musicbar.FixedMusicBar
        android:id="@+id/FixedMusicBar"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:padding="8dp"
        android:background="@android:color/white" />
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
        
        //change progress 
        musicBar.setProgress(50)
        
        //start show animation
        musicBar.show()
        
        //start hide animation
        musicBar.hide()
        
        //change bar width
        musicBar.setBarWidth(2);
        
        //change Space Between Bars
        musicBar.setSpaceBetweenBar(2); //Recommend to make spaceBetweenBar equal barWidth
       
        // String path = the music file path
        // int duration = the music file duration time in millisecond [mediaPlayer.getDuration()]
        musicBar.loadFrom(path,duration)
        //or use inputstream 
        musicBar.loadFrom(getResources().openRawResource(R.raw.music),duration());

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
