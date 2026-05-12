import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { UserProvider } from './contexts/UserContext';
import Layout from './components/Layout';
import HomePage from './pages/HomePage';
import TracksPage from './pages/TracksPage';
import TrackDetailPage from './pages/TrackDetailPage';
import TrackFormPage from './pages/TrackFormPage';
import ArtistsPage from './pages/ArtistsPage';
import AlbumsPage from './pages/AlbumsPage';
import PlaylistsPage from './pages/PlaylistsPage';
import LikedSongsPage from './pages/LikedSongsPage';   // <-- импорт
import FriendsPage from './pages/FriendsPage';         // <-- импорт


export default function App() {
  return (
    <UserProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<HomePage />} />
            <Route path="tracks" element={<TracksPage />} />
            <Route path="tracks/new" element={<TrackFormPage />} />
            <Route path="tracks/:id" element={<TrackDetailPage />} />
            <Route path="artists" element={<ArtistsPage />} />
            <Route path="albums" element={<AlbumsPage />} />
            <Route path="playlists" element={<PlaylistsPage />} />
            <Route path="liked" element={<LikedSongsPage />} />
            <Route path="friends" element={<FriendsPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </UserProvider>
  );
}