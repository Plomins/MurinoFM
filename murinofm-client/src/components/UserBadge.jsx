import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../contexts/UserContext';

export default function UserBadge() {
  const { activeUser, setActiveUser } = useUser();
  const navigate = useNavigate();
  const [showMenu, setShowMenu] = useState(false);
  const menuRef = useRef(null);

  // Закрывать меню при клике вне
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setShowMenu(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleNavigate = (path) => {
    setShowMenu(false);
    navigate(path);
  };

  if (!activeUser) {
    return (
      <button
        onClick={() => navigate('/')}
        className="bg-dark-700 hover:bg-dark-600 text-white px-4 py-2 rounded-full text-sm font-medium"
      >
        Войти
      </button>
    );
  }

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setShowMenu(prev => !prev)}
        className="flex items-center gap-3 bg-dark-800/90 backdrop-blur-sm px-4 py-2 rounded-full border border-dark-700 cursor-pointer"
      >
        <div className="w-8 h-8 rounded-full overflow-hidden bg-dark-700 flex items-center justify-center">
          {activeUser.avatarUrl ? (
            <img src={activeUser.avatarUrl} alt="avatar" className="object-cover w-full h-full" />
          ) : (
            <span className="text-xs text-gray-400">👤</span>
          )}
        </div>
        <span className="text-sm font-medium text-white">{activeUser.username}</span>
      </button>

      {showMenu && (
        <div className="absolute right-0 mt-2 w-48 bg-dark-800 border border-dark-700 rounded-lg shadow-xl z-50 py-1">
          <button
            onClick={() => handleNavigate(`/tracks?artist=${encodeURIComponent(activeUser.username)}`)}
            className="w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-dark-700 hover:text-white transition"
          >
            🎶 Мои треки
          </button>
          <button
            onClick={() => handleNavigate('/playlists')}
            className="w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-dark-700 hover:text-white transition"
          >
            📋 Мои плейлисты
          </button>
          <button
            onClick={() => handleNavigate('/liked')}
            className="w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-dark-700 hover:text-white transition"
          >
            ❤️ Мне нравится
          </button>
          <hr className="border-gray-700 my-1" />
          <button
            onClick={() => handleNavigate('/friends')}
            className="w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-dark-700 hover:text-white transition"
          >
            👥 Мои друзья
          </button>
          <hr className="border-gray-700 my-1" />
          <button
            onClick={() => { setActiveUser(null); setShowMenu(false); }}
            className="w-full text-left px-4 py-2 text-sm text-red-400 hover:bg-dark-700 hover:text-red-300 transition"
          >
            Выйти
          </button>
        </div>
      )}
    </div>
  );
}