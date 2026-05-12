// src/components/FullScreenPlayer.jsx
import { useEffect, useState, useRef } from 'react';
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';
import { getPlaylists, createPlaylist as apiCreatePlaylist, addTrackToPlaylist } from '../services/api';

export default function FullScreenPlayer() {
  const {
    currentTrack, playing, currentTime, duration,
    togglePlayPause, seek, skip, playbackRate, toggleSpeed,
    loop, toggleLoop, isFullScreen, toggleFullScreen
  } = usePlayer();
  const { activeUser } = useUser();
  const { addToast } = useToast();

  const [likedPlaylistId, setLikedPlaylistId] = useState(null);
  const [playlists, setPlaylists] = useState([]);
  const [showAddMenu, setShowAddMenu] = useState(false);
  const addMenuRef = useRef(null);

  useEffect(() => {
    if (!activeUser) { setPlaylists([]); setLikedPlaylistId(null); return; }
    getPlaylists(activeUser.id).then((res) => {
      setPlaylists(res.data);
      const liked = res.data.find(p => p.name === 'Мне нравится' && !p.public);
      if (liked) setLikedPlaylistId(liked.id);
    }).catch(() => {});
  }, [activeUser, currentTrack]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (addMenuRef.current && !addMenuRef.current.contains(e.target)) setShowAddMenu(false);
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLike = async () => {
    if (!activeUser || !currentTrack) return;
    let targetId = likedPlaylistId;
    if (!targetId) {
      try {
        const newPl = await apiCreatePlaylist(activeUser.id, 'Мне нравится', [], false);
        targetId = newPl.data.id;
        setLikedPlaylistId(targetId);
        const res = await getPlaylists(activeUser.id);
        setPlaylists(res.data);
      } catch (e) { addToast('Не удалось создать плейлист «Мне нравится»', 'error'); return; }
    }
    try {
      await addTrackToPlaylist(targetId, currentTrack.id, activeUser.id);
      addToast('Добавлено в «Мне нравится»', 'success');
    } catch (e) { addToast('Ошибка при добавлении в «Мне нравится»', 'error'); }
  };

  const handleAddToPlaylist = async (playlistId) => {
    if (!activeUser || !currentTrack) return;
    try {
      await addTrackToPlaylist(playlistId, currentTrack.id, activeUser.id);
      addToast('Трек добавлен в плейлист!', 'success');
      setShowAddMenu(false);
    } catch (e) { addToast('Ошибка при добавлении', 'error'); }
  };

  const formatTime = (sec) => {
    const m = Math.floor(sec / 60);
    const s = Math.floor(sec % 60);
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  if (!isFullScreen || !currentTrack) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black/90 flex flex-col items-center justify-center text-white animate-fadeIn">
      <button onClick={toggleFullScreen} className="absolute top-4 right-4 text-gray-400 hover:text-white text-2xl">✕</button>

      <div className="w-64 h-64 md:w-96 md:h-96 mb-8 rounded-lg overflow-hidden shadow-2xl">
        {currentTrack.coverImageUrl ? (
          <img src={currentTrack.coverImageUrl} alt={currentTrack.title} className="w-full h-full object-cover" />
        ) : (
          <div className="w-full h-full bg-dark-700 flex items-center justify-center text-6xl">🎵</div>
        )}
      </div>

      <h2 className="text-2xl md:text-4xl font-bold mb-2">{currentTrack.title}</h2>
      {currentTrack.artistName && <p className="text-gray-400 text-lg mb-6">{currentTrack.artistName}</p>}

      {/* Прогресс-бар */}
      <div className="w-3/4 max-w-xl flex items-center gap-4 mb-6">
        <span className="text-sm text-gray-400">{formatTime(currentTime)}</span>
        <input
          type="range"
          min="0"
          max={duration || 0}
          value={currentTime}
          onChange={(e) => seek(parseFloat(e.target.value))}
          className="w-full h-1 bg-gray-500 rounded-lg appearance-none cursor-pointer accent-orange-500"
        />
        <span className="text-sm text-gray-400">{formatTime(duration)}</span>
      </div>

      {/* Блок кнопок */}
      <div className="flex items-center gap-6">
        {/* Перемотка -10с */}
        <button onClick={() => skip(-10)} className="text-gray-400 hover:text-white" title="Назад на 10с">
          <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
            <path d="M12 5V1L7 6l5 5V7c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6H4c0 4.42 3.58 8 8 8s8-3.58 8-8-3.58-8-8-8z"/>
          </svg>
        </button>

        {/* Play/Pause */}
        <button onClick={togglePlayPause} className="bg-orange-500 hover:bg-orange-400 text-black rounded-full w-16 h-16 flex items-center justify-center">
          {playing ? (
            <svg viewBox="0 0 24 24" fill="white" width="32" height="32"><path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/></svg>
          ) : (
            <svg viewBox="0 0 24 24" fill="white" width="32" height="32"><path d="M8 5v14l11-7z"/></svg>
          )}
        </button>

        {/* Перемотка +10с */}
        <button onClick={() => skip(10)} className="text-gray-400 hover:text-white" title="Вперёд на 10с">
          <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
            <path d="M12 5V1l5 5-5 5V7c-3.31 0-6 2.69-6 6s2.69 6 6 6 6-2.69 6-6h2c0 4.42-3.58 8-8 8s-8-3.58-8-8 3.58-8 8-8z"/>
          </svg>
        </button>

        {/* Кнопка повтора */}
        <button onClick={toggleLoop} className={`${loop ? 'text-orange-400' : 'text-gray-400'} hover:text-white`} title="Повтор">
          <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
            <path d="M7 7h10v3l4-4-4-4v3H5v6h2V7zm10 10H7v-3l-4 4 4 4v-3h12v-6h-2v4z"/>
          </svg>
        </button>

        {/* Кнопка скорости */}
        <button onClick={toggleSpeed} className={`text-xs font-bold px-3 py-1 rounded ${playbackRate === 2 ? 'text-orange-400 border border-orange-400' : 'text-gray-400 hover:text-white'}`} title="Скорость">
          x{playbackRate}
        </button>

        {/* Лайк */}
        <button onClick={handleLike} className="text-gray-400 hover:text-red-400 transition-colors" title="Мне нравится">
          <svg viewBox="0 0 24 24" fill="currentColor" width="28" height="28"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
        </button>

        {/* Добавить в плейлист */}
        <div className="relative" ref={addMenuRef}>
          <button onClick={() => setShowAddMenu(prev => !prev)} className="text-gray-400 hover:text-white" title="Добавить в плейлист">
            <svg viewBox="0 0 24 24" fill="currentColor" width="28" height="28"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"/></svg>
          </button>
          {showAddMenu && (
            <div className="absolute bottom-full mb-2 right-0 w-48 max-h-60 overflow-y-auto bg-dark-700 border border-dark-600 rounded shadow-lg z-50">
              {playlists.length === 0 ? <p className="p-2 text-gray-400 text-sm">Нет плейлистов</p> : playlists.map(p => (
                <button key={p.id} onClick={() => handleAddToPlaylist(p.id)} className="w-full text-left px-4 py-2 hover:bg-dark-600 text-sm text-white">{p.name}</button>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}