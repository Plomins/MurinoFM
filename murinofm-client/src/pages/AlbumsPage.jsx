import { useEffect, useState } from 'react';
import { getAlbums, deleteAlbum } from '../services/api';
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';

export default function AlbumsPage() {
  const { activeUser } = useUser();
  const [albums, setAlbums] = useState([]);
  const { playTrack } = usePlayer();
  const { addToast } = useToast();

  const loadAlbums = async () => {
    try { const res = await getAlbums(); setAlbums(res.data); } catch { addToast('Ошибка загрузки альбомов', 'error'); }
  };
  useEffect(() => { loadAlbums(); }, []);

  const handleDeleteAlbum = async (id) => {
    if (!window.confirm('Удалить этот альбом?')) return;
    try { await deleteAlbum(id); loadAlbums(); addToast('Альбом удалён', 'success'); } catch { addToast('Не удалось удалить альбом', 'error'); }
  };

  const canDeleteAlbum = (album) => {
    return activeUser && album.artist && album.artist.name === activeUser.username;
  };

  return (
    <div>
      <h2 className="text-3xl font-bold mb-6 text-white">Альбомы</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {albums.map(album => (
          <div key={album.id} className="bg-dark-800 p-4 rounded">
            <div className="flex justify-between items-start mb-3">
              <div>
                <h3 className="text-xl font-bold text-white">{album.title}</h3>
                {album.artist && <p className="text-orange-400 text-sm">{album.artist.name}</p>}
              </div>
              {canDeleteAlbum(album) && (
                <button onClick={() => handleDeleteAlbum(album.id)} className="text-red-400 hover:text-red-300 text-sm">Удалить</button>
              )}
            </div>
            {album.tracks && album.tracks.length > 0 ? (
              <ul className="space-y-1">
                {album.tracks.map(track => (
                  <li key={track.id} className="flex justify-between items-center bg-dark-700 p-2 rounded">
                    <span className="text-sm text-white">{track.title} ({track.durationSeconds}с)</span>
                    <button onClick={() => playTrack(track)} className="text-orange-400 hover:text-orange-300">
                      <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
                    </button>
                  </li>
                ))}
              </ul>
            ) : <p className="text-gray-500 text-sm">Нет треков</p>}
          </div>
        ))}
      </div>
    </div>
  );
}