# MusicBar


## Setup

```groovy
dependencies {
      implementation 'com.oze.music:MusicBar:1.0.0'
}
```
## Usage

Function | Description
------------ | -------------
setAnimationChangeListener(OnMusicBarAnimationChangeListener listener) | animation listener
setProgressChangeListener(OnMusicBarProgressChangeListener listener) | progress listener
removeAllListener() | remove Progress and Animation listener
loadFrom(byte[] file, int durationInSec) | load from music file as byte[] with duration in sec
show() | start show animation
hide() | start hide animation
setProgress(int position) | move to specified position (in milisecand) 
getPosition() | return current progress position
setLoadedBarColor(int color) | change progressed bar color
setBackgroundBarColor(int color) | change unprogressed bar color
setSpaceBetweenBar(int spaceBetweenBar) | change distance between bars
setBarWidth(float barWidth) | change bar width


**XML** 

for BigMusicBar

![BigMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/BigMusicBar.png?raw=true)
```XML
   <com.oze.music.musicbar.BigMusicBar
        android:id="@+id/BigMusicBar"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@color/colorPrimaryDark"
        android:padding="8dp" />
```
OR MiniMusicBar 

![MiniMusicBar](https://github.com/emadabdalrahman/MusicBar/blob/master/ScreenShots/MiniMusicBar.png?raw=true)
```XML
    <com.oze.music.musicbar.MiniMusicBar
        android:id="@+id/MiniMusicBar"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:padding="8dp"
        android:layout_marginTop="16dp"
        android:background="@color/colorPrimaryDark" />
```

**AnimationListener**
```Java
MusicBar.OnMusicBarAnimationChangeListener onMusicBarAnimationChangeListener = new MusicBar.OnMusicBarAnimationChangeListener() {
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
 MusicBar.OnMusicBarProgressChangeListener onMusicBarProgressChangeListener = new MusicBar.OnMusicBarProgressChangeListener() {
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



