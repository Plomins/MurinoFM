// src/contexts/PlayerContext.jsx
import { createContext, useContext, useState, useRef, useEffect } from 'react';

const PlayerContext = createContext(null);

export function PlayerProvider({ children }) {
  const [currentTrack, setCurrentTrack] = useState(null);
  const [playing, setPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [playbackRate, setPlaybackRate] = useState(1);
  const [loop, setLoop] = useState(false);
  const audioRef = useRef(new Audio());

  const [isFullScreen, setIsFullScreen] = useState(false);
  const toggleFullScreen = () => setIsFullScreen(prev => !prev);

  useEffect(() => {
    const audio = audioRef.current;

    const onTimeUpdate = () => setCurrentTime(audio.currentTime);
    const onLoadedMetadata = () => setDuration(audio.duration);
    const onEnded = () => {
      if (loop) {
        audio.currentTime = 0;
        audio.play();
      } else {
        setPlaying(false);
      }
    };

    audio.addEventListener('timeupdate', onTimeUpdate);
    audio.addEventListener('loadedmetadata', onLoadedMetadata);
    audio.addEventListener('ended', onEnded);

    return () => {
      audio.removeEventListener('timeupdate', onTimeUpdate);
      audio.removeEventListener('loadedmetadata', onLoadedMetadata);
      audio.removeEventListener('ended', onEnded);
    };
  }, [loop]);  // зависимость loop нужна, чтобы пересоздать обработчик с актуальным loop

  useEffect(() => {
    const audio = audioRef.current;
    if (currentTrack?.audioUrl) {
      audio.src = currentTrack.audioUrl;
      audio.load();
      audio.playbackRate = 1;
      setPlaybackRate(1);
      setLoop(false);
      audio.play().then(() => setPlaying(true)).catch(() => setPlaying(false));
    } else {
      audio.pause();
      setPlaying(false);
      audio.src = '';
      setCurrentTime(0);
      setDuration(0);
    }
  }, [currentTrack]);

  useEffect(() => {
    audioRef.current.playbackRate = playbackRate;
  }, [playbackRate]);

  const togglePlayPause = () => {
    const audio = audioRef.current;
    if (playing) {
      audio.pause();
      setPlaying(false);
    } else if (currentTrack) {
      audio.play().then(() => setPlaying(true)).catch(() => setPlaying(false));
    }
  };

  const seek = (time) => {
    const audio = audioRef.current;
    audio.currentTime = time;
    setCurrentTime(time);
  };

  const skip = (seconds) => {
    const audio = audioRef.current;
    const newTime = Math.min(Math.max(audio.currentTime + seconds, 0), audio.duration || 0);
    audio.currentTime = newTime;
    setCurrentTime(newTime);
  };

  const toggleSpeed = () => {
    setPlaybackRate(prev => (prev === 1 ? 2 : 1));
  };

  const toggleLoop = () => {
    setLoop(prev => !prev);
  };

  const playTrack = (track) => {
    setCurrentTrack(track);
  };

  return (
    <PlayerContext.Provider value={{
      currentTrack,
      playing,
      currentTime,
      duration,
      togglePlayPause,
      seek,
      skip,
      playbackRate,
      toggleSpeed,
      loop,
      toggleLoop,
      playTrack,
      isFullScreen,
      toggleFullScreen,
    }}>
      {children}
      <audio ref={audioRef} preload="auto" />
    </PlayerContext.Provider>
  );
}

export function usePlayer() {
  return useContext(PlayerContext);
}