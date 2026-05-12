import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

// 🔹 Треки
export const getTracks = (params) => api.get('/tracks', { params });
export const getTrackById = (id) => api.get(`/tracks/${id}`);
export const createTrack = (data) => api.post('/tracks', data);
export const deleteTrack = (id) => api.delete(`/tracks/${id}`);
export const searchTracks = (name) => api.get('/tracks/search', { params: { name } });
export const getTracksByArtist = (artistName, page = 0, size = 10) =>
  api.get('/tracks/by-artist', { params: { artistName, page, size } });

// 🔹 Артисты
export const getArtists = () => api.get('/view/artists');
export const getArtistById = (id) => api.get(`/view/artists/${id}`);
export const createArtist = (name) => api.post('/artists', null, { params: { name } });
export const deleteArtist = (id) => api.delete(`/artists/${id}`);

// 🔹 Альбомы
export const getAlbums = () => api.get('/view/albums');
export const getAlbumById = (id) => api.get(`/view/albums/${id}`);
export const addAlbumToArtist = (artistId, title) =>
  api.post(`/artists/${artistId}/albums`, null, { params: { title } });
export const deleteAlbum = (id) => api.delete(`/albums/${id}`);

// 🔹 Плейлисты
export const getPlaylists = (userId) =>
  api.get('/playlists', { params: { userId } });
export const getPlaylistById = (id, userId) =>
  api.get(`/playlists/${id}`, { params: { userId } });
export const createPlaylist = (userId, name, trackIds, isPublic = true) =>
  api.post(`/playlists/user/${userId}`, trackIds ?? [], { params: { name, isPublic } });
export const addTrackToPlaylist = (playlistId, trackId, userId) =>
  api.post(`/playlists/${playlistId}/tracks/${trackId}`, null, { params: { userId } });
export const copyPlaylist = (playlistId, userId) =>
  api.post(`/playlists/${playlistId}/copy`, null, { params: { userId } });
// 🔹 Пользователи и авторизация
export const getUsers = () => api.get('/users');
export const getUserById = (id) => api.get(`/users/${id}`);
export const createUser = (username, password) =>
  api.post('/users', null, { params: { username, password } });
export const login = (username, password) =>
  api.post('/auth/login', { username, password });
export const searchUsers = (q) => api.get('/users/search', { params: { q } });

// 🔹 Загрузка файлов
export const uploadImage = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return api.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

// 🔹 Обновление аватара
export const updateUserAvatar = (userId, avatarUrl) =>
  api.put(`/users/${userId}/avatar`, null, { params: { avatarUrl } });

export default api;