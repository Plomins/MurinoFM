import { useEffect, useState } from 'react';
import { getPlaylists, getPlaylistById, copyPlaylist } from '../services/api';
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';

export default function PlaylistsPage() {
  const { activeUser } = useUser();
  const [playlists, setPlaylists] = useState([]);
  const [selected, setSelected] = useState(null);
  const [showAllTracks, setShowAllTracks] = useState(false);
  const { playTrack } = usePlayer();
  const { addToast } = useToast();

  useEffect(() => {
    getPlaylists(activeUser?.id)
      .then(res => setPlaylists(res.data))
      .catch(() => addToast('Ошибка загрузки плейлистов', 'error'));
  }, [activeUser]);

  const handleView = async (id) => {
    try {
      const res = await getPlaylistById(id, activeUser?.id);
      setSelected(res.data);
      setShowAllTracks(false);
    } catch (e) {
      addToast('Нет доступа', 'error');
    }
  };

  const handleCopy = async (id) => {
    if (!activeUser) return;
    try {
      await copyPlaylist(id, activeUser.id);
      addToast('Плейлист скопирован в ваши', 'success');
      // обновить список плейлистов на странице
      const res = await getPlaylists(activeUser.id);
      setPlaylists(res.data);
    } catch {
      addToast('Ошибка копирования', 'error');
    }
  };

  const tracksToShow = selected?.tracks && !showAllTracks
    ? selected.tracks.slice(0, 5)
    : selected?.tracks || [];

  return (
    <div>
      <h2 className="text-3xl font-bold mb-6 text-white">Плейлисты</h2>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <h3 className="text-xl mb-3 text-white">Все плейлисты</h3>
          <ul className="space-y-2">
            {playlists.map(p => (
              <li key={p.id} className="bg-dark-800 p-3 rounded flex justify-between items-center">
                <span className="text-white">{p.name}{!p.public && <span className="ml-2 text-gray-400" title="Приватный">🔒</span>}</span>
                <div className="flex gap-2">
                  <button onClick={() => handleView(p.id)} className="text-orange-400 hover:text-orange-300 text-sm">Открыть</button>
                  <button onClick={() => handleCopy(p.id)} className="text-blue-400 hover:text-blue-300 text-sm">💾 Копировать</button>
                </div>
              </li>
            ))}
          </ul>
        </div>
        {selected && (
          <div className="bg-dark-800 p-4 rounded">
            <h3 className="text-xl font-bold text-orange-400 mb-2">{selected.name}</h3>
            <p className="text-sm text-gray-400 mb-4">Треков: {selected.tracks?.length || 0}</p>
            {selected.tracks && selected.tracks.length > 0 ? (
              <ul className="space-y-2">
                {tracksToShow.map(track => (
                  <li key={track.id} className="flex justify-between items-center bg-dark-700 p-2 rounded">
                    <div><span className="text-white font-medium">{track.title}</span><span className="text-gray-400 ml-2 text-sm">{track.durationSeconds}s</span></div>
                    <button onClick={() => playTrack(track)} className="text-orange-400 hover:text-orange-300">
                      <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
                    </button>
                  </li>
                ))}
              </ul>
            ) : <p className="text-gray-500">Плейлист пуст</p>}
            {selected.tracks?.length > 5 && (
              <button onClick={() => setShowAllTracks(!showAllTracks)} className="mt-3 text-orange-400 hover:underline text-sm">
                {showAllTracks ? 'Скрыть' : `Ещё (${selected.tracks.length - 5})`}
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}