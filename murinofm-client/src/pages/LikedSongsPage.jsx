import { useEffect, useState } from 'react';
import { useUser } from '../contexts/UserContext';
import { usePlayer } from '../contexts/PlayerContext';
import { useToast } from '../contexts/ToastContext';
import { getPlaylists, getPlaylistById, createPlaylist as apiCreatePlaylist } from '../services/api';

export default function LikedSongsPage() {
  const { activeUser } = useUser();
  const { playTrack } = usePlayer();
  const { addToast } = useToast();
  const [likedPlaylist, setLikedPlaylist] = useState(null);
  const [tracks, setTracks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!activeUser) return;
    getPlaylists(activeUser.id)
      .then(async (res) => {
        const liked = res.data.find(p => p.name === 'Мне нравится' && !p.public);
        if (liked) {
          setLikedPlaylist(liked);
          const detail = await getPlaylistById(liked.id, activeUser.id);
          setTracks(detail.data.tracks || []);
        } else {
          const newPl = await apiCreatePlaylist(activeUser.id, 'Мне нравится', [], false);
          setLikedPlaylist(newPl.data);
          setTracks([]);
        }
        setLoading(false);
      })
      .catch(() => { addToast('Не удалось загрузить понравившиеся треки', 'error'); setLoading(false); });
  }, [activeUser]);

  if (!activeUser) return <p className="text-white p-8">Необходимо войти</p>;
  if (loading) return <p className="text-gray-400 p-8">Загрузка...</p>;

  return (
    <div>
      <h2 className="text-3xl font-bold text-white mb-6">❤️ Мне нравится</h2>
      {tracks.length === 0 ? (
        <p className="text-gray-400">Нет любимых треков</p>
      ) : (
        <ul className="space-y-2">
          {tracks.map(track => (
            <li key={track.id} className="flex justify-between items-center bg-dark-800 p-3 rounded">
              <span className="text-white">{track.title} – {track.durationSeconds}с</span>
              <button onClick={() => playTrack(track)} className="text-orange-400 hover:text-orange-300">
                <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}