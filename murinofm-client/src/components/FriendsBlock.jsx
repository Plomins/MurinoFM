// src/components/FriendsBlock.jsx
import { useEffect, useState } from 'react';
import { searchUsers, getPlaylists, getPlaylistById } from '../services/api';
import { useUser } from '../contexts/UserContext';
import { usePlayer } from '../contexts/PlayerContext';

export default function FriendsBlock() {
  const { activeUser } = useUser();
  const { playTrack } = usePlayer();
  const [searchQuery, setSearchQuery] = useState('');
  const [foundUsers, setFoundUsers] = useState([]);
  const [friends, setFriends] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [friendPlaylists, setFriendPlaylists] = useState([]);
  const [expandedPlaylist, setExpandedPlaylist] = useState(null);
  const [tracks, setTracks] = useState([]);
  const [loading, setLoading] = useState(false);

  // Загрузка друзей из localStorage (храним массив {id, username})
  useEffect(() => {
    if (!activeUser) return;
    const stored = localStorage.getItem(`friends_${activeUser.id}`);
    if (stored) {
      try {
        setFriends(JSON.parse(stored));
      } catch {}
    }
  }, [activeUser]);

  const saveFriends = (newFriends) => {
    setFriends(newFriends);
    if (activeUser) {
      localStorage.setItem(`friends_${activeUser.id}`, JSON.stringify(newFriends));
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;
    try {
      const res = await searchUsers(searchQuery.trim());
      setFoundUsers(res.data.filter(u => u.id !== activeUser?.id));
    } catch (e) {
      console.error('Ошибка поиска', e);
    }
  };

  const addFriend = (user) => {
    const alreadyAdded = friends.find(f => f.id === user.id);
    if (alreadyAdded) return;
    const updated = [...friends, { id: user.id, username: user.username }];
    saveFriends(updated);
  };

  const removeFriend = (userId) => {
    const updated = friends.filter(f => f.id !== userId);
    saveFriends(updated);
  };

  const loadFriendPlaylists = async (friend) => {
    setSelectedFriend(friend);
    setLoading(true);
    try {
      const res = await getPlaylists(friend.id); // вернёт только публичные плейлисты друга
      setFriendPlaylists(res.data);
      setExpandedPlaylist(null);
      setTracks([]);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const loadPlaylistTracks = async (playlistId) => {
    if (expandedPlaylist === playlistId) {
      setExpandedPlaylist(null);
      setTracks([]);
      return;
    }
    try {
      const res = await getPlaylistById(playlistId, activeUser?.id);
      setExpandedPlaylist(playlistId);
      setTracks(res.data.tracks || []);
    } catch (e) {
      alert('Не удалось загрузить треки плейлиста');
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      {/* Поисковая строка */}
      <div className="flex gap-2 mb-6">
        <input
          className="flex-1 bg-dark-700 p-2 rounded text-white"
          placeholder="Поиск по нику..."
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSearch()}
        />
        <button onClick={handleSearch} className="bg-orange-500 px-4 py-2 rounded">Найти</button>
      </div>

      {/* Результаты поиска */}
      {foundUsers.length > 0 && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-white mb-2">Результаты</h3>
          {foundUsers.map(user => (
            <div key={user.id} className="flex items-center justify-between bg-dark-800 p-3 rounded mb-2">
              <span className="text-white">{user.username}</span>
              {!friends.find(f => f.id === user.id) ? (
                <button onClick={() => addFriend(user)} className="text-orange-400 text-sm">
                  + Добавить в друзья
                </button>
              ) : (
                <span className="text-green-400 text-sm">✓ В друзьях</span>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Список друзей */}
      <h3 className="text-lg font-semibold text-white mb-2">Мои друзья</h3>
      <div className="flex flex-wrap gap-2 mb-6">
        {friends.length === 0 ? (
          <p className="text-gray-500">Нет друзей</p>
        ) : (
          friends.map(friend => (
            <button
              key={friend.id}
              onClick={() => loadFriendPlaylists(friend)}
              className={`px-3 py-1 rounded-full text-sm ${
                selectedFriend?.id === friend.id
                  ? 'bg-orange-500 text-white'
                  : 'bg-dark-700 text-gray-300 hover:bg-dark-600'
              }`}
            >
              {friend.username}
              <span
                onClick={(e) => { e.stopPropagation(); removeFriend(friend.id); }}
                className="ml-2 text-red-400 hover:text-red-300"
              >
                ✕
              </span>
            </button>
          ))
        )}
      </div>

      {/* Плейлисты выбранного друга */}
      {selectedFriend && (
        <div className="bg-dark-800 p-4 rounded">
          <h4 className="text-xl font-bold text-orange-400 mb-4">
            Плейлисты {selectedFriend.username}
          </h4>
          {loading ? (
            <p className="text-gray-400">Загрузка...</p>
          ) : friendPlaylists.length === 0 ? (
            <p className="text-gray-500">Нет публичных плейлистов</p>
          ) : (
            <div className="space-y-2">
              {friendPlaylists.map(pl => (
                <div key={pl.id} className="bg-dark-700 rounded">
                  <div
                    className="flex justify-between items-center p-3 cursor-pointer"
                    onClick={() => loadPlaylistTracks(pl.id)}
                  >
                    <span className="text-white">{pl.name} ({pl.tracks?.length || 0})</span>
                    <span>{expandedPlaylist === pl.id ? '▲' : '▼'}</span>
                  </div>
                  {expandedPlaylist === pl.id && tracks.length > 0 && (
                    <div className="border-t border-dark-600 p-3 animate-fadeIn">
                      {tracks.map(track => (
                        <div key={track.id} className="flex justify-between items-center py-1">
                          <span className="text-sm text-white">{track.title}</span>
                          <button onClick={() => playTrack(track)} className="text-orange-400">▶</button>
                        </div>
                      ))}
                    </div>
                  )}
                  {expandedPlaylist === pl.id && tracks.length === 0 && (
                    <div className="border-t border-dark-600 p-3 text-gray-500 text-sm">
                      Нет треков
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}