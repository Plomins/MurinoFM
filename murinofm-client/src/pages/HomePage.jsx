import { useEffect, useState, useRef } from 'react';
import { useUser } from '../contexts/UserContext';
import { usePlayer } from '../contexts/PlayerContext';
import { useToast } from '../contexts/ToastContext';
import {
  getUsers, createUser, login, getArtists, createArtist,
  addAlbumToArtist, createTrack, getArtistById,
  uploadImage, updateUserAvatar, createPlaylist as apiCreatePlaylist,
  getPlaylists, getPlaylistById, searchUsers
} from '../services/api';
import axios from 'axios';

export default function HomePage() {
  const { activeUser, setActiveUser } = useUser();
  const { playTrack } = usePlayer();
  const { addToast } = useToast();

  // --- Пользователи (для регистрации) ---
  const [users, setUsers] = useState([]);

  // --- Мои плейлисты ---
  const [myPlaylists, setMyPlaylists] = useState([]);

  // --- Друзья ---
  const [friends, setFriends] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [foundUsers, setFoundUsers] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [friendPlaylists, setFriendPlaylists] = useState([]);
  const [expandedPlaylist, setExpandedPlaylist] = useState(null);
  const [playlistTracks, setPlaylistTracks] = useState([]);
  const [loadingFriendPlaylists, setLoadingFriendPlaylists] = useState(false);

  // --- Авторизация ---
  const [authMode, setAuthMode] = useState('login');
  const [loginUsername, setLoginUsername] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [regUsername, setRegUsername] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [authError, setAuthError] = useState('');

  // --- Создание трека ---
  const [trackTitle, setTrackTitle] = useState('');
  const [trackDuration, setTrackDuration] = useState('');
  const [trackAudioUrl, setTrackAudioUrl] = useState('');
  const [trackAlbumName, setTrackAlbumName] = useState('');
  const [creatingTrack, setCreatingTrack] = useState(false);
  const [uploading, setUploading] = useState(false);

  // Обложка трека
  const [coverDragOver, setCoverDragOver] = useState(false);
  const [uploadingCover, setUploadingCover] = useState(false);
  const [trackCoverUrl, setTrackCoverUrl] = useState('');

  // Drag & drop аудио
  const dropRef = useRef(null);
  const [dragOver, setDragOver] = useState(false);

  // --- Плейлист ---
  const [newPlaylistName, setNewPlaylistName] = useState('');
  const [showPlaylistForm, setShowPlaylistForm] = useState(false);
  const [isPublic, setIsPublic] = useState(true);

  // Загрузка всех пользователей (для отображения зарегистрированных)
  const loadUsers = async () => {
    try { const res = await getUsers(); setUsers(res.data); } catch {}
  };

  // Загрузка моих плейлистов
  const loadMyPlaylists = async (userId) => {
    if (!userId) return;
    try {
      const res = await getPlaylists(userId);
      setMyPlaylists(res.data);
    } catch { addToast('Не удалось загрузить ваши плейлисты', 'error'); }
  };

  // Загрузка друзей из localStorage
  useEffect(() => {
    if (!activeUser) {
      setFriends([]);
      setMyPlaylists([]);
      return;
    }
    const stored = localStorage.getItem(`friends_${activeUser.id}`);
    if (stored) try { setFriends(JSON.parse(stored)); } catch {}
    loadMyPlaylists(activeUser.id);
    loadUsers();
  }, [activeUser]);

  // Сохранение друзей
  const saveFriends = (newFriends) => {
    setFriends(newFriends);
    if (activeUser) localStorage.setItem(`friends_${activeUser.id}`, JSON.stringify(newFriends));
  };

  // Поиск пользователей (автоматический с debounce)
  const handleSearchInput = (value) => {
    setSearchQuery(value);
    if (value.trim().length >= 2) {
      clearTimeout(window.searchTimeout);
      window.searchTimeout = setTimeout(() => {
        searchUsers(value.trim())
          .then(res => setFoundUsers(res.data.filter(u => u.id !== activeUser?.id)))
          .catch(() => addToast('Ошибка поиска', 'error'));
      }, 300);
    } else {
      setFoundUsers([]);
    }
  };

  const addFriend = (user) => {
    if (friends.find(f => f.id === user.id)) return;
    const updated = [...friends, { id: user.id, username: user.username }];
    saveFriends(updated);
    addToast(`${user.username} добавлен в друзья!`, 'success');
  };

  const removeFriend = (userId) => {
    const updated = friends.filter(f => f.id !== userId);
    saveFriends(updated);
    addToast('Удалён из друзей', 'info');
  };

  const loadFriendPlaylists = async (friend) => {
    setSelectedFriend(friend);
    setLoadingFriendPlaylists(true);
    try {
      const res = await getPlaylists(friend.id);
      setFriendPlaylists(res.data);
      setExpandedPlaylist(null);
      setPlaylistTracks([]);
    } catch (e) {
      addToast('Не удалось загрузить плейлисты друга', 'error');
    } finally {
      setLoadingFriendPlaylists(false);
    }
  };

  const loadPlaylistTracks = async (playlistId) => {
    if (expandedPlaylist === playlistId) {
      setExpandedPlaylist(null);
      setPlaylistTracks([]);
      return;
    }
    try {
      const res = await getPlaylistById(playlistId, activeUser?.id);
      setExpandedPlaylist(playlistId);
      setPlaylistTracks(res.data.tracks || []);
    } catch (e) {
      addToast('Не удалось загрузить треки плейлиста', 'error');
    }
  };

  // --- Авторизация ---
  const handleLogin = async (e) => {
    e.preventDefault();
    setAuthError('');
    if (!loginUsername.trim() || !loginPassword.trim()) {
      setAuthError('Заполните все поля');
      return;
    }
    try {
      const user = await login(loginUsername.trim(), loginPassword.trim());
      setActiveUser(user.data);
      setLoginUsername('');
      setLoginPassword('');
      addToast('Вход выполнен', 'success');
    } catch (err) {
      setAuthError('Неверное имя пользователя или пароль');
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setAuthError('');
    if (!regUsername.trim() || !regPassword.trim()) {
      setAuthError('Заполните все поля');
      return;
    }
    try {
      await createUser(regUsername.trim(), regPassword.trim());
      setRegUsername('');
      setRegPassword('');
      addToast('Аккаунт создан! Теперь войдите.', 'success');
      setAuthMode('login');
    } catch (err) {
      setAuthError('Ошибка при регистрации. Возможно, имя занято.');
    }
  };

  // --- Создание плейлиста ---
  const handleCreatePlaylist = async () => {
    if (!activeUser || !newPlaylistName.trim()) return;
    try {
      await apiCreatePlaylist(activeUser.id, newPlaylistName.trim(), [], isPublic);
      setNewPlaylistName('');
      setShowPlaylistForm(false);
      addToast('Плейлист создан!', 'success');
      loadMyPlaylists(activeUser.id);
    } catch (e) {
      addToast('Не удалось создать плейлист', 'error');
    }
  };

  // --- Получение ID артиста ---
  const getArtistIdByName = async (artistName) => {
    const { data: artists } = await getArtists();
    let artist = artists.find((a) => a.name === artistName);
    if (artist) return artist.id;
    try {
      const resp = await createArtist(artistName);
      const match = resp.data.match(/ID: (\d+)/);
      if (match && match[1]) return parseInt(match[1], 10);
      throw new Error('Не удалось извлечь ID');
    } catch (createErr) {
      const { data: artists2 } = await getArtists();
      const artist2 = artists2.find((a) => a.name === artistName);
      if (artist2) return artist2.id;
      throw new Error('Артист не найден');
    }
  };

  // --- Создание трека ---
  const handleCreateTrack = async () => {
    if (!trackTitle.trim() || !trackDuration) {
      addToast('Название и длительность обязательны', 'error');
      return;
    }
    setCreatingTrack(true);
    try {
      const artistId = await getArtistIdByName(activeUser.username);
      let albumId = null;
      if (trackAlbumName.trim()) {
        const artistDetail = await getArtistById(artistId);
        const albums = artistDetail.data.albums || [];
        let album = albums.find((a) => a.title === trackAlbumName.trim());
        if (!album) {
          await addAlbumToArtist(artistId, trackAlbumName.trim());
          const updatedArtist = await getArtistById(artistId);
          album = (updatedArtist.data.albums || []).find(
            (a) => a.title === trackAlbumName.trim()
          );
          if (!album) throw new Error('Не удалось создать альбом');
        }
        albumId = album.id;
      }
      await createTrack({
        title: trackTitle.trim(),
        durationSeconds: parseInt(trackDuration),
        audioUrl: trackAudioUrl.trim() || null,
        albumId,
        coverImageUrl: trackCoverUrl || null,
      });
      addToast(`Трек "${trackTitle.trim()}" опубликован!`, 'success');
      setTrackTitle('');
      setTrackDuration('');
      setTrackAudioUrl('');
      setTrackAlbumName('');
      setTrackCoverUrl('');
    } catch (err) {
      addToast('Ошибка: ' + (err.response?.data?.message || err.message), 'error');
    } finally {
      setCreatingTrack(false);
    }
  };

  // --- Drag & Drop аудио ---
  const handleDragOver = (e) => { e.preventDefault(); setDragOver(true); };
  const handleDragLeave = (e) => { e.preventDefault(); setDragOver(false); };
  const handleDrop = async (e) => {
    e.preventDefault(); setDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (!file || !file.type.startsWith('audio/')) {
      addToast('Перетащите аудиофайл (mp3, wav)', 'error');
      return;
    }
    setUploading(true);
    try {
      const formData = new FormData(); formData.append('file', file);
      const uploadRes = await axios.post('/api/upload/audio', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const audioUrl = uploadRes.data.audioUrl;
      const duration = await getAudioDuration(file);
      setTrackAudioUrl(audioUrl);
      if (duration) setTrackDuration(Math.round(duration).toString());
      const nameWithoutExt = file.name.replace(/\.[^/.]+$/, '');
      setTrackTitle(nameWithoutExt);
      addToast(`Файл ${file.name} загружен!`, 'success');
    } catch (err) {
      addToast('Ошибка загрузки файла: ' + (err.response?.data?.error || err.message), 'error');
    } finally { setUploading(false); }
  };

  const getAudioDuration = (file) => new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (event) => {
      const audioContext = new (window.AudioContext || window.webkitAudioContext)();
      audioContext.decodeAudioData(event.target.result, (buffer) => {
        resolve(buffer.duration); audioContext.close();
      }, () => { resolve(null); audioContext.close(); });
    };
    reader.onerror = () => resolve(null);
    reader.readAsArrayBuffer(file);
  });

  // --- Drag & Drop обложки ---
  const handleCoverDragOver = (e) => { e.preventDefault(); setCoverDragOver(true); };
  const handleCoverDragLeave = (e) => { e.preventDefault(); setCoverDragOver(false); };
  const handleCoverDrop = async (e) => {
    e.preventDefault(); setCoverDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (!file || !file.type.startsWith('image/')) {
      addToast('Перетащите изображение', 'error');
      return;
    }
    setUploadingCover(true);
    try {
      const res = await uploadImage(file);
      setTrackCoverUrl(res.data.imageUrl);
      addToast('Обложка загружена', 'success');
    } catch (err) {
      addToast('Ошибка загрузки обложки', 'error');
    } finally { setUploadingCover(false); }
  };

  // ======= РЕНДЕР =======
  return (
    <div className="relative">
      <h1 className="text-4xl font-bold mb-8 text-center">Добро пожаловать в MurinoFM!</h1>

      {/* Модальное окно авторизации */}
      {!activeUser && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
          <div className="bg-dark-800 p-8 rounded-2xl w-full max-w-md shadow-2xl animate-fadeIn">
            {authMode === 'login' ? (
              <>
                <h2 className="text-2xl font-bold text-center mb-6">Войти в MurinoFM</h2>
                <form onSubmit={handleLogin} className="space-y-4">
                  <input className="w-full bg-dark-700 p-3 rounded text-white focus:outline-none focus:ring-2 focus:ring-orange-500" placeholder="Имя пользователя" value={loginUsername} onChange={(e) => setLoginUsername(e.target.value)} />
                  <input type="password" className="w-full bg-dark-700 p-3 rounded text-white focus:outline-none focus:ring-2 focus:ring-orange-500" placeholder="Пароль" value={loginPassword} onChange={(e) => setLoginPassword(e.target.value)} />
                  {authError && <p className="text-red-500 text-sm">{authError}</p>}
                  <button type="submit" className="w-full bg-orange-500 hover:bg-orange-400 py-3 rounded-full font-bold transition-colors">Войти</button>
                </form>
                <p className="mt-6 text-center text-sm text-gray-400">
                  Впервые на MurinoFM?{' '}
                  <button onClick={() => { setAuthMode('register'); setAuthError(''); }} className="text-orange-400 hover:underline font-semibold">Зарегистрироваться</button>
                </p>
              </>
            ) : (
              <>
                <h2 className="text-2xl font-bold text-center mb-6">Создать аккаунт</h2>
                <form onSubmit={handleRegister} className="space-y-4">
                  <input className="w-full bg-dark-700 p-3 rounded text-white focus:outline-none focus:ring-2 focus:ring-orange-500" placeholder="Придумайте имя" value={regUsername} onChange={(e) => setRegUsername(e.target.value)} />
                  <input type="password" className="w-full bg-dark-700 p-3 rounded text-white focus:outline-none focus:ring-2 focus:ring-orange-500" placeholder="Придумайте пароль" value={regPassword} onChange={(e) => setRegPassword(e.target.value)} />
                  {authError && <p className="text-red-500 text-sm">{authError}</p>}
                  <button type="submit" className="w-full bg-orange-500 hover:bg-orange-400 py-3 rounded-full font-bold transition-colors">Зарегистрироваться</button>
                </form>
                <p className="mt-6 text-center text-sm text-gray-400">
                  Уже есть аккаунт?{' '}
                  <button onClick={() => { setAuthMode('login'); setAuthError(''); }} className="text-orange-400 hover:underline font-semibold">Войти</button>
                </p>
              </>
            )}
          </div>
        </div>
      )}

      {activeUser && (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 max-w-6xl mx-auto">
            {/* Левая колонка – Добавление трека */}
            <div className="bg-dark-800 p-6 rounded-xl">
              <h2 className="text-2xl font-semibold mb-4">Создать трек под ником {activeUser.username}</h2>

              {/* Зона аудио */}
              <div ref={dropRef} onDragOver={handleDragOver} onDragLeave={handleDragLeave} onDrop={handleDrop}
                className={`border-2 border-dashed rounded-lg p-6 text-center cursor-pointer mb-4 transition-colors ${dragOver ? 'border-orange-400 bg-dark-700' : 'border-gray-600 hover:border-gray-400'}`}>
                {uploading ? <p className="text-orange-400">Загрузка...</p> : <p className="text-gray-400">{trackAudioUrl ? 'Файл загружен!' : 'Перетащите аудиофайл сюда (.mp3, .wav)'}</p>}
              </div>

              {/* Зона обложки */}
              <div onDragOver={handleCoverDragOver} onDragLeave={handleCoverDragLeave} onDrop={handleCoverDrop}
                className={`border-2 border-dashed rounded-lg p-4 text-center cursor-pointer mb-4 transition-colors ${coverDragOver ? 'border-orange-400 bg-dark-700' : 'border-gray-600 hover:border-gray-400'}`}>
                {uploadingCover ? <p className="text-orange-400">Загрузка обложки...</p> : trackCoverUrl ? (
                  <div className="flex items-center justify-center gap-2"><img src={trackCoverUrl} alt="Обложка" className="w-12 h-12 object-cover rounded" /><span className="text-gray-400 text-sm">Обложка загружена</span></div>
                ) : <p className="text-gray-400">Перетащите обложку сюда (JPEG/PNG)</p>}
              </div>

              <div className="space-y-3">
                <input className="w-full bg-dark-700 p-2 rounded text-white" placeholder="Название трека" value={trackTitle} onChange={(e) => setTrackTitle(e.target.value)} />
                <input className="w-full bg-dark-700 p-2 rounded text-white" type="number" placeholder="Длительность (сек)" value={trackDuration} onChange={(e) => setTrackDuration(e.target.value)} />
                <input className="w-full bg-dark-700 p-2 rounded text-white" placeholder="Ссылка на аудио (авто)" value={trackAudioUrl} readOnly />
                <input className="w-full bg-dark-700 p-2 rounded text-white" placeholder="Название альбома (будет создан)" value={trackAlbumName} onChange={(e) => setTrackAlbumName(e.target.value)} />
                <button onClick={handleCreateTrack} disabled={creatingTrack || !trackAudioUrl}
                  className="w-full bg-orange-500 hover:bg-orange-400 py-3 rounded-full font-bold disabled:opacity-50 transition-colors">
                  {creatingTrack ? 'Публикуется...' : 'Опубликовать трек'}
                </button>
              </div>
            </div>

            {/* Правая колонка – Мои плейлисты */}
            <div className="bg-dark-800 p-6 rounded-xl flex flex-col">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-2xl font-semibold">Мои плейлисты</h2>
                <button
                  onClick={() => setShowPlaylistForm(!showPlaylistForm)}
                  className="bg-dark-700 hover:bg-dark-600 text-white px-3 py-1 rounded-full text-sm"
                >
                  {showPlaylistForm ? '−' : '+ Создать'}
                </button>
              </div>

              {/* Форма создания плейлиста (анимированная) */}
              <div className={`overflow-hidden transition-all duration-300 ${showPlaylistForm ? 'max-h-40 opacity-100 mb-4' : 'max-h-0 opacity-0'}`}>
                <div className="space-y-2">
                  <input className="w-full bg-dark-700 p-2 rounded text-white" placeholder="Название плейлиста" value={newPlaylistName} onChange={(e) => setNewPlaylistName(e.target.value)} />
                  <div className="flex items-center gap-2">
                    <input type="checkbox" id="publicCheckbox" checked={isPublic} onChange={(e) => setIsPublic(e.target.checked)} className="w-4 h-4" />
                    <label htmlFor="publicCheckbox" className="text-sm text-gray-300">Публичный</label>
                  </div>
                  <div className="flex gap-2">
                    <button onClick={handleCreatePlaylist} className="bg-orange-500 px-4 py-1 rounded text-sm font-bold">Создать</button>
                    <button onClick={() => setShowPlaylistForm(false)} className="bg-dark-700 px-4 py-1 rounded text-sm">Отмена</button>
                  </div>
                </div>
              </div>

              {/* Список моих плейлистов */}
              <div className="flex-1 overflow-y-auto space-y-2 pr-1 custom-scrollbar">
                {myPlaylists.length === 0 ? (
                  <p className="text-gray-500 text-sm">У вас пока нет плейлистов</p>
                ) : (
                  myPlaylists.map(pl => (
                    <div key={pl.id} className="bg-dark-700 rounded p-2 flex justify-between items-center">
                      <span className="text-white text-sm truncate">{pl.name}</span>
                      <span className="text-gray-500 text-xs">{pl.tracks?.length || 0} треков</span>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

          {/* Блок "Ваши друзья" */}
          <div className="mt-12 max-w-4xl mx-auto">
            <h2 className="text-2xl font-semibold mb-4">Ваши друзья</h2>
            <div className="mb-4">
              <input className="w-full bg-dark-700 p-3 rounded text-white" placeholder="Поиск по нику..." value={searchQuery} onChange={e => handleSearchInput(e.target.value)} />
            </div>
            {foundUsers.length > 0 && (
              <div className="mb-6">
                {foundUsers.map(user => (
                  <div key={user.id} className="flex items-center justify-between bg-dark-800 p-3 rounded mb-2">
                    <span className="text-white">{user.username}</span>
                    {!friends.find(f => f.id === user.id) ? (
                      <button onClick={() => addFriend(user)} className="bg-orange-500 hover:bg-orange-400 text-white px-4 py-1 rounded-full text-sm font-semibold">+ Добавить</button>
                    ) : (
                      <span className="text-green-400 text-sm font-semibold">✓ В друзьях</span>
                    )}
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
                {loadingFriendPlaylists ? <p className="text-gray-400">Загрузка...</p> : friendPlaylists.length === 0 ? <p className="text-gray-500">Нет публичных плейлистов</p> : (
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
                                <button onClick={() => playTrack(track)} className="text-orange-400">
                                  <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
                                </button>
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
        </>
      )}
    </div>
  );
}