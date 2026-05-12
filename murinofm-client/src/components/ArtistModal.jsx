import { useState, useEffect } from 'react';
import { getUsers } from '../services/api';

export default function ArtistModal({ artist, albums, onClose, onPlayTrack }) {
  const [avatarUrl, setAvatarUrl] = useState(null);

  useEffect(() => {
    getUsers().then(res => {
      const user = res.data.find(u => u.username === artist.name);
      if (user?.avatarUrl) setAvatarUrl(user.avatarUrl);
    });
  }, [artist.name]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80" onClick={onClose}>
      <div className="bg-dark-800 p-6 rounded-2xl max-w-2xl w-full mx-4 animate-fadeIn max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
        <button onClick={onClose} className="absolute top-4 right-4 text-gray-400 hover:text-white text-2xl">✕</button>
        <div className="flex flex-col items-center mb-6">
          <div className="w-24 h-24 rounded-full overflow-hidden bg-dark-700 mb-4 flex items-center justify-center">
            {avatarUrl ? (
              <img src={avatarUrl} alt="avatar" className="w-full h-full object-cover" />
            ) : (
              <span className="text-4xl">🎤</span>
            )}
          </div>
          <h2 className="text-2xl font-bold text-white">{artist.name}</h2>
          <p className="text-gray-400">Артист MurinoFM</p>
        </div>

        <div>
          <h3 className="text-xl font-semibold text-white mb-4">Альбомы</h3>
          {albums && albums.length > 0 ? (
            <div className="space-y-4">
              {albums.map(album => (
                <div key={album.id} className="bg-dark-700 p-4 rounded">
                  <h4 className="text-lg font-medium text-orange-400 mb-2">{album.title}</h4>
                  {album.tracks && album.tracks.length > 0 ? (
                    <ul className="space-y-2">
                      {album.tracks.map(track => (
                        <li key={track.id} className="flex justify-between items-center bg-dark-600 p-2 rounded">
                          <div>
                            <span className="text-white">{track.title}</span>
                            <span className="text-gray-400 ml-2 text-sm">{track.durationSeconds}с</span>
                          </div>
                          <button onClick={() => onPlayTrack(track)} className="text-orange-400 hover:text-orange-300">
                            <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
                          </button>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-gray-500 text-sm">Нет треков</p>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500">Нет альбомов</p>
          )}
        </div>
      </div>
    </div>
  );
}