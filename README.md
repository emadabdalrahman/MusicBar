# MusicBar


## Setup

```groovy
dependencies {
      'implementation 'com.oze.music:MusicBar:1.0.0'
}
```
```groovy
allprojects {
    repositories {
        ... 
        maven { url  "https://dl.bintray.com/emad/maven" }
    }
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
```XML
   <com.oze.music.musicbar.BigMusicBar
        android:id="@+id/BigMusicBar"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@color/colorPrimaryDark"
        android:padding="8dp" />
```
OR
```XML
    <com.oze.music.musicbar.MiniMusicBar
        android:id="@+id/MiniMusicBar"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        android:padding="8dp"
        android:layout_marginTop="16dp"
        android:background="@color/colorPrimaryDark" />
```
