import { useEffect, useState, useRef } from 'react';
import {
  getTracks, searchTracks, getTracksByArtist, deleteTrack,
  getPlaylists, addTrackToPlaylist,
} from '../services/api';
import { Link, useSearchParams } from 'react-router-dom';
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';
import TrackModal from '../components/TrackModal';

const PAGE_SIZE = 20;

export default function TracksPage() {
  const { activeUser } = useUser();
  const { playTrack } = usePlayer();
  const { addToast } = useToast();
  const [searchParams] = useSearchParams();
  const artistParam = searchParams.get('artist');

  const [tracks, setTracks] = useState([]);
  const [search, setSearch] = useState('');
  const [artistFilter, setArtistFilter] = useState(artistParam || '');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [playlists, setPlaylists] = useState([]);

  const [openMenuId, setOpenMenuId] = useState(null);
  const [playlistSubmenuFor, setPlaylistSubmenuFor] = useState(null);
  const menuRef = useRef(null);
  const [modalTrack, setModalTrack] = useState(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenuId(null);
        setPlaylistSubmenuFor(null);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const safeSetTracks = (data) => {
    const filtered = (Array.isArray(data) ? data : []).filter(t => t && t.id != null);
    setTracks(filtered);
  };

  const loadTracks = async (pageNum = 0) => {
    setLoading(true);
    try {
      const res = await getTracks({ page: pageNum, size: PAGE_SIZE });
      safeSetTracks(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
      setPage(pageNum);
    } catch (e) {
      addToast('Ошибка загрузки треков', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadArtistTracks = async (artistName) => {
    setLoading(true);
    try {
      const res = await getTracksByArtist(artistName);
      const content = res.data.content || res.data;
      safeSetTracks(content);
      setTotalPages(res.data.totalPages || 1);
      setPage(0);
    } catch (e) {
      addToast('Ошибка фильтрации по артисту', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (artistParam) {
      setArtistFilter(artistParam);
      loadArtistTracks(artistParam);
    } else {
      loadTracks(0);
    }
    getPlaylists(activeUser?.id).then(res => setPlaylists(res.data)).catch(() => {});
  }, [artistParam, activeUser]);

  const handleSearch = async () => {
    if (!search.trim()) {
      if (artistParam) return loadArtistTracks(artistParam);
      return loadTracks(0);
    }
    try {
      const res = await searchTracks(search);
      safeSetTracks(res.data);
      setTotalPages(1);
      setPage(0);
    } catch (e) {
      addToast('Ошибка поиска', 'error');
    }
  };

  const handleArtistFilter = async () => {
    if (!artistFilter.trim()) return loadTracks(0);
    try {
      const res = await getTracksByArtist(artistFilter);
      const content = res.data.content || res.data;
      safeSetTracks(content);
      setTotalPages(res.data.totalPages || 1);
      setPage(0);
    } catch (e) {
      addToast('Ошибка фильтрации', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (!id) return;
    if (!window.confirm('Удалить этот трек?')) return;
    try {
      await deleteTrack(id);
      setOpenMenuId(null);
      addToast('Трек удалён', 'success');
      if (artistParam) loadArtistTracks(artistParam);
      else loadTracks(page);
    } catch (e) {
      addToast('Ошибка при удалении трека', 'error');
    }
  };

  const handleAddToPlaylist = async (trackId, playlistId) => {
    if (!playlistId || !trackId || !activeUser?.id) {
      addToast('Необходимо авторизоваться', 'info');
      return;
    }
    try {
      await addTrackToPlaylist(playlistId, trackId, activeUser.id);
      addToast('Трек добавлен в плейлист!', 'success');
      setOpenMenuId(null);
      setPlaylistSubmenuFor(null);
    } catch (e) {
      addToast('Не удалось добавить трек в плейлист', 'error');
    }
  };

  const canDelete = (track) => {
    if (!activeUser) return false;
    return track.artistName === activeUser.username;
  };

  return (
    <div>
      <h2 className="text-3xl font-bold mb-6 text-white">Треки</h2>

      <div className="flex gap-4 mb-6 flex-wrap">
        <input className="bg-dark-700 p-2 rounded text-white" placeholder="Поиск по названию..." value={search} onChange={e => setSearch(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleSearch()} />
        <button onClick={handleSearch} disabled={loading} className="bg-orange-500 px-4 py-2 rounded disabled:opacity-50 text-white">Найти</button>
        <input className="bg-dark-700 p-2 rounded text-white" placeholder="Фильтр по артисту..." value={artistFilter} onChange={e => setArtistFilter(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleArtistFilter()} />
        <button onClick={handleArtistFilter} disabled={loading} className="bg-orange-500 px-4 py-2 rounded disabled:opacity-50 text-white">Фильтр</button>
      </div>

      <Link to="/tracks/new" className="bg-orange-500 px-4 py-2 rounded inline-block mb-4 text-white">+ Новый трек</Link>

      {loading && <p className="text-orange-400 mb-2">Загрузка...</p>}

      <ul className="space-y-2 mb-6">
        {tracks.map(track => (
          <li key={track.id} className="flex flex-col sm:flex-row sm:justify-between sm:items-center bg-dark-800 p-3 rounded gap-2 cursor-pointer" onClick={() => setModalTrack(track)}>
            <div className="text-white">
              <span className="font-medium">{track.title}</span>
              <span className="text-gray-400 ml-2">{track.durationSeconds}s</span>
              {track.artistName && <span className="text-gray-500 ml-2 text-sm">— {track.artistName}</span>}
            </div>
            <div className="flex items-center gap-2" onClick={e => e.stopPropagation()}>
              <button onClick={() => playTrack(track)} className="bg-orange-500 hover:bg-orange-400 text-white px-2 py-1 rounded">
                <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M8 5v14l11-7z"/></svg>
              </button>
              <div className="relative">
                <button onClick={(e) => { e.stopPropagation(); setOpenMenuId(openMenuId === track.id ? null : track.id); setPlaylistSubmenuFor(null); }} className="text-gray-400 hover:text-white p-1 rounded-full">⋮</button>
                {openMenuId === track.id && (
                  <div ref={menuRef} className="absolute right-0 mt-1 w-48 bg-dark-700 border border-dark-600 rounded shadow-lg z-50">
                    <button onClick={(e) => { e.stopPropagation(); setPlaylistSubmenuFor(playlistSubmenuFor === track.id ? null : track.id); }} className="w-full text-left px-4 py-2 hover:bg-dark-600 rounded-t text-white">➕ Добавить в плейлист</button>
                    {playlistSubmenuFor === track.id && (
                      <div className="border-t border-dark-600 max-h-40 overflow-y-auto">
                        {playlists.length === 0 ? <p className="px-4 py-2 text-gray-400 text-sm italic">Нет плейлистов</p> : playlists.map(p => (
                          <button key={p.id} onClick={(e) => { e.stopPropagation(); handleAddToPlaylist(track.id, p.id); }} className="w-full text-left px-6 py-1 hover:bg-dark-600 text-sm text-gray-200">🎧 {p.name}</button>
                        ))}
                      </div>
                    )}
                    {canDelete(track) ? (
                      <button onClick={(e) => { e.stopPropagation(); handleDelete(track.id); }} className="w-full text-left px-4 py-2 hover:bg-dark-600 text-red-400 rounded-b">🗑 Удалить</button>
                    ) : (
                      <div className="px-4 py-2 text-gray-500 text-sm italic">Нет прав на удаление</div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </li>
        ))}
      </ul>

      {totalPages > 1 && (
        <div className="flex justify-center gap-4 items-center">
          <button onClick={() => loadTracks(page - 1)} disabled={page === 0 || loading} className="bg-dark-700 px-3 py-1 rounded disabled:opacity-50 text-white">Назад</button>
          <span className="text-sm text-white">{page + 1} / {totalPages}</span>
          <button onClick={() => loadTracks(page + 1)} disabled={page >= totalPages - 1 || loading} className="bg-dark-700 px-3 py-1 rounded disabled:opacity-50 text-white">Вперед</button>
        </div>
      )}

      {modalTrack && <TrackModal track={modalTrack} onClose={() => setModalTrack(null)} />}
    </div>
  );
}