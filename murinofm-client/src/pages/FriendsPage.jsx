import { useEffect, useState } from 'react';
import { searchUsers, getPlaylists, getPlaylistById } from '../services/api';
import { useUser } from '../contexts/UserContext';
import { usePlayer } from '../contexts/PlayerContext';
import { useToast } from '../contexts/ToastContext';

export default function FriendsPage() {
  const { activeUser } = useUser();
  const { playTrack } = usePlayer();
  const { addToast } = useToast();
  const [friends, setFriends] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [foundUsers, setFoundUsers] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [friendPlaylists, setFriendPlaylists] = useState([]);
  const [expandedPlaylist, setExpandedPlaylist] = useState(null);
  const [playlistTracks, setPlaylistTracks] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!activeUser) return;
    const stored = localStorage.getItem(`friends_${activeUser.id}`);
    if (stored) try { setFriends(JSON.parse(stored)); } catch {}
  }, [activeUser]);

  const saveFriends = (newFriends) => { setFriends(newFriends); if (activeUser) localStorage.setItem(`friends_${activeUser.id}`, JSON.stringify(newFriends)); };

  const handleSearch = (value) => {
    setSearchQuery(value);
    if (value.trim().length >= 2) {
      clearTimeout(window.searchTimeout);
      window.searchTimeout = setTimeout(() => {
        searchUsers(value.trim())
          .then(res => setFoundUsers(res.data.filter(u => u.id !== activeUser?.id)))
          .catch(() => addToast('Ошибка поиска', 'error'));
      }, 300);
    } else setFoundUsers([]);
  };

  const addFriend = (user) => {
    if (friends.find(f => f.id === user.id)) return;
    saveFriends([...friends, { id: user.id, username: user.username }]);
    addToast(`${user.username} добавлен в друзья!`, 'success');
  };

  const removeFriend = (userId) => { saveFriends(friends.filter(f => f.id !== userId)); addToast('Удалён из друзей', 'info'); };

  const loadFriendPlaylists = async (friend) => {
    setSelectedFriend(friend); setLoading(true);
    try { const res = await getPlaylists(friend.id); setFriendPlaylists(res.data); setExpandedPlaylist(null); setPlaylistTracks([]); } catch (e) { addToast('Не удалось загрузить плейлисты', 'error'); }
    finally { setLoading(false); }
  };

  const loadPlaylistTracks = async (playlistId) => {
    if (expandedPlaylist === playlistId) { setExpandedPlaylist(null); setPlaylistTracks([]); return; }
    try { const res = await getPlaylistById(playlistId, activeUser?.id); setExpandedPlaylist(playlistId); setPlaylistTracks(res.data.tracks || []); } catch (e) { addToast('Не удалось загрузить треки', 'error'); }
  };

  return (
    <div>
      <h2 className="text-3xl font-bold text-white mb-6">👥 Мои друзья</h2>
      <input className="w-full bg-dark-700 p-3 rounded text-white mb-4" placeholder="Поиск по нику..." value={searchQuery} onChange={e => handleSearch(e.target.value)} />
      {foundUsers.length > 0 && (
        <div className="mb-6">
          {foundUsers.map(user => (
            <div key={user.id} className="flex items-center justify-between bg-dark-800 p-3 rounded mb-2">
              <span className="text-white">{user.username}</span>
              {!friends.find(f => f.id === user.id) ? (
                <button onClick={() => addFriend(user)} className="bg-orange-500 hover:bg-orange-400 text-white px-4 py-1 rounded-full text-sm">+ Добавить</button>
              ) : <span className="text-green-400 text-sm">✓ В друзьях</span>}
            </div>
          ))}
        </div>
      )}
      <div className="flex flex-wrap gap-2 mb-6">
        {friends.length === 0 ? <p className="text-gray-500">У вас пока нет друзей</p> : friends.map(friend => (
          <button key={friend.id} onClick={() => loadFriendPlaylists(friend)} className={`px-3 py-1 rounded-full text-sm ${selectedFriend?.id === friend.id ? 'bg-orange-500 text-white' : 'bg-dark-700 text-gray-300 hover:bg-dark-600'}`}>
            {friend.username}
            <span onClick={(e) => { e.stopPropagation(); removeFriend(friend.id); }} className="ml-2 text-red-400 hover:text-red-300">✕</span>
          </button>
        ))}
      </div>
      {selectedFriend && (
        <div className="bg-dark-800 p-4 rounded">
          <h3 className="text-xl font-bold text-orange-400 mb-4">Плейлисты {selectedFriend.username}</h3>
          {loading ? <p className="text-gray-400">Загрузка...</p> : friendPlaylists.length === 0 ? <p className="text-gray-500">Нет публичных плейлистов</p> : (
            <div className="space-y-2">
              {friendPlaylists.map(pl => (
                <div key={pl.id} className="bg-dark-700 rounded">
                  <div className="flex justify-between items-center p-3 cursor-pointer" onClick={() => loadPlaylistTracks(pl.id)}>
                    <span className="text-white">{pl.name} ({pl.tracks?.length || 0})</span>
                    <span>{expandedPlaylist === pl.id ? '▲' : '▼'}</span>
                  </div>
                  {expandedPlaylist === pl.id && playlistTracks.length > 0 && (
                    <div className="border-t border-dark-600 p-3 animate-fadeIn">
                      {playlistTracks.map(track => (
                        <div key={track.id} className="flex justify-between items-center py-1">
                          <span className="text-sm text-white">{track.title}</span>
                          <button onClick={() => playTrack(track)} className="text-orange-400"><svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg></button>
                        </div>
                      ))}
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