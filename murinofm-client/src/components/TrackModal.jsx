// src/components/TrackModal.jsx
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';
import { getPlaylists, addTrackToPlaylist, createPlaylist as apiCreatePlaylist } from '../services/api';
import { useState, useEffect } from 'react';

export default function TrackModal({ track, onClose }) {
  const { playTrack } = usePlayer();
  const { activeUser } = useUser();
  const { addToast } = useToast();
  const [playlists, setPlaylists] = useState([]);
  const [showAddMenu, setShowAddMenu] = useState(false);
  const [likedPlaylistId, setLikedPlaylistId] = useState(null);

  useEffect(() => {
    if (!activeUser) return;
    getPlaylists(activeUser.id).then(res => {
      setPlaylists(res.data);
      const liked = res.data.find(p => p.name === 'Мне нравится' && !p.public);
      if (liked) setLikedPlaylistId(liked.id);
    });
  }, [activeUser]);

  const handleLike = async () => {
    if (!activeUser) return;
    let targetId = likedPlaylistId;
    if (!targetId) {
      try {
        const newPl = await apiCreatePlaylist(activeUser.id, 'Мне нравится', [], false);
        targetId = newPl.data.id;
        setLikedPlaylistId(targetId);
        setPlaylists(prev => [...prev, newPl.data]);
      } catch { addToast('Ошибка создания избранного', 'error'); return; }
    }
    try {
      await addTrackToPlaylist(targetId, track.id, activeUser.id);
      addToast('Добавлено в «Мне нравится»', 'success');
    } catch { addToast('Ошибка добавления', 'error'); }
  };

  const handleAddToPlaylist = async (playlistId) => {
    if (!activeUser) return;
    try {
      await addTrackToPlaylist(playlistId, track.id, activeUser.id);
      addToast('Трек добавлен', 'success');
      setShowAddMenu(false);
    } catch { addToast('Ошибка', 'error'); }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80" onClick={onClose}>
      <div className="bg-dark-800 p-8 rounded-2xl max-w-lg w-full mx-4 animate-fadeIn" onClick={e => e.stopPropagation()}>
        <button onClick={onClose} className="absolute top-4 right-4 text-gray-400 hover:text-white text-2xl">✕</button>

        <div className="flex flex-col items-center text-center">
          {track.coverImageUrl ? (
            <img src={track.coverImageUrl} alt={track.title} className="w-48 h-48 object-cover rounded-xl mb-4" />
          ) : (
            <div className="w-48 h-48 bg-dark-700 rounded-xl flex items-center justify-center text-6xl mb-4">🎵</div>
          )}
          <h2 className="text-2xl font-bold text-white">{track.title}</h2>
          {track.artistName && <p className="text-orange-400 mt-1">{track.artistName}</p>}
          <p className="text-gray-400 mt-2">{track.durationSeconds} секунд</p>

          <div className="flex gap-4 mt-6">
            <button
              onClick={() => { playTrack(track); onClose(); }}
              className="bg-orange-500 hover:bg-orange-400 text-black font-bold px-6 py-2 rounded-full"
            >
              ▶ Играть
            </button>
            <button onClick={handleLike} className="text-gray-400 hover:text-red-400 p-2">
              <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
            </button>
            <div className="relative">
              <button onClick={() => setShowAddMenu(prev => !prev)} className="text-gray-400 hover:text-white p-2">
                <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"/></svg>
              </button>
              {showAddMenu && (
                <div className="absolute right-0 mt-2 w-48 bg-dark-700 border border-dark-600 rounded shadow-lg z-50">
                  {playlists.map(p => (
                    <button key={p.id} onClick={() => handleAddToPlaylist(p.id)} className="w-full text-left px-4 py-2 hover:bg-dark-600 text-sm text-white">{p.name}</button>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}