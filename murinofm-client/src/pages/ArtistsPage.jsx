import { useEffect, useState } from 'react';
import { getArtists, createArtist, deleteArtist, addAlbumToArtist, getArtistById } from '../services/api';
import { usePlayer } from '../contexts/PlayerContext';
import { useUser } from '../contexts/UserContext';
import { useToast } from '../contexts/ToastContext';
import ArtistModal from '../components/ArtistModal';   // создайте этот компонент

export default function ArtistsPage() {
  const { activeUser } = useUser();
  const { playTrack } = usePlayer();
  const { addToast } = useToast();
  const [artists, setArtists] = useState([]);
  const [newArtistName, setNewArtistName] = useState('');
  const [selectedArtist, setSelectedArtist] = useState(null);  // для альбомов внизу (можно оставить, но основное – модалка)
  const [artistAlbums, setArtistAlbums] = useState([]);
  const [newAlbumTitle, setNewAlbumTitle] = useState('');
  const [showAlbumForm, setShowAlbumForm] = useState(false);
  const [modalArtist, setModalArtist] = useState(null);   // для модального окна

  const loadArtists = async () => {
    try { const res = await getArtists(); setArtists(res.data); } catch { addToast('Ошибка загрузки артистов', 'error'); }
  };
  useEffect(() => { loadArtists(); }, []);

  const handleCreateArtist = async () => {
    if (!newArtistName.trim()) return;
    try { await createArtist(newArtistName.trim()); setNewArtistName(''); loadArtists(); addToast('Артист создан', 'success'); } catch { addToast('Не удалось создать артиста', 'error'); }
  };

  const handleDeleteArtist = async (id) => {
    if (!window.confirm('Удалить артиста и все его альбомы?')) return;
    try { await deleteArtist(id); setSelectedArtist(null); setArtistAlbums([]); loadArtists(); addToast('Артист удалён', 'success'); } catch { addToast('Не удалось удалить артиста', 'error'); }
  };

  const openArtistModal = async (artist) => {
    setModalArtist(artist);
    // загрузить альбомы для отображения в модалке
    try {
      const res = await getArtistById(artist.id);
      setArtistAlbums(res.data.albums || []);
    } catch { setArtistAlbums([]); }
  };

  const canDeleteArtist = (artist) => {
    return activeUser && artist.name === activeUser.username;
  };

  return (
    <div>
      <h2 className="text-3xl font-bold mb-6 text-white">Артисты</h2>
      {/* Создание артиста */}
      <div className="bg-dark-800 p-6 rounded mb-8">
        <h3 className="text-xl mb-3 text-white">Добавить артиста</h3>
        <div className="flex gap-3">
          <input className="bg-dark-700 p-2 rounded flex-1 text-white" placeholder="Имя артиста" value={newArtistName} onChange={e => setNewArtistName(e.target.value)} />
          <button onClick={handleCreateArtist} className="bg-orange-500 px-4 py-2 rounded font-bold hover:bg-orange-400 text-white">Создать</button>
        </div>
      </div>

      {/* Список артистов */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {artists.map(artist => (
          <div key={artist.id} className="bg-dark-800 p-4 rounded cursor-pointer hover:bg-dark-700 transition" onClick={() => openArtistModal(artist)}>
            <div className="flex justify-between items-center">
              <h3 className="text-xl font-bold text-white">{artist.name}</h3>
              {canDeleteArtist(artist) ? (
                <button onClick={(e) => { e.stopPropagation(); handleDeleteArtist(artist.id); }} className="text-red-400 hover:text-red-300 text-sm">Удалить</button>
              ) : (
                <span className="text-gray-500 text-sm italic"></span>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Модальное окно артиста */}
      {modalArtist && (
        <ArtistModal
          artist={modalArtist}
          albums={artistAlbums}
          onClose={() => setModalArtist(null)}
          onPlayTrack={playTrack}
        />
      )}
    </div>
  );
}