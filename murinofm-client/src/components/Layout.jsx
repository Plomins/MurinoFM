import { Link, Outlet } from 'react-router-dom';
import { PlayerProvider } from '../contexts/PlayerContext';
import { ToastProvider } from '../contexts/ToastContext';
import ToastContainer from './ToastContainer';
import PlayerBar from './PlayerBar';
import FullScreenPlayer from './FullScreenPlayer';
import UserBadge from './UserBadge';

export default function Layout() {
  return (
    <PlayerProvider>
      <ToastProvider>
        <div className="relative h-screen">
          <div className="flex h-full">
            <nav className="w-64 bg-dark-800 p-6 flex flex-col gap-6">
              <h1 className="text-2xl font-bold text-orange-500">MurinoFM</h1>
              <div>
                <Link to="/" className="hover:text-orange-400 transition block py-1">Главная</Link>
              </div>
              <hr className="border-gray-600" />
              <div className="flex flex-col gap-2">
                <Link to="/tracks" className="hover:text-orange-400 transition block py-1">Треки</Link>
                <Link to="/artists" className="hover:text-orange-400 transition block py-1">Артисты</Link>
                <Link to="/albums" className="hover:text-orange-400 transition block py-1">Альбомы</Link>
              </div>
              <hr className="border-gray-600" />
              <div className="flex flex-col gap-2">
                <Link to="/playlists" className="hover:text-orange-400 transition block py-1">Плейлисты</Link>
              </div>
            </nav>
            <main className="flex-1 overflow-auto p-8 bg-dark-900 pb-24">
              <Outlet />
            </main>
          </div>
          <div className="absolute top-4 right-4 z-40">
            <UserBadge />
          </div>
          <PlayerBar />
          <FullScreenPlayer />
          <ToastContainer />
        </div>
      </ToastProvider>
    </PlayerProvider>
  );
}