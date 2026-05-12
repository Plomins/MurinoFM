import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createTrack } from '../services/api';

export default function TrackFormPage() {
  const [title, setTitle] = useState('');
  const [duration, setDuration] = useState('');
  const [audioUrl, setAudioUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim() || !duration) {
      setError('Название и длительность обязательны');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await createTrack({
        title: title.trim(),
        durationSeconds: parseInt(duration),
        audioUrl: audioUrl.trim() || null,
      });
      navigate('/tracks');
    } catch (err) {
      setError('Ошибка при сохранении трека. Проверьте данные и попробуйте снова.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-3xl font-bold mb-6">Новый трек</h2>
      <form onSubmit={handleSubmit} className="space-y-4 max-w-md">
        <div>
          <label className="block mb-1">Название</label>
          <input
            className="w-full bg-dark-700 p-2 rounded"
            placeholder="Введите название"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block mb-1">Длительность (секунд)</label>
          <input
            className="w-full bg-dark-700 p-2 rounded"
            type="number"
            placeholder="180"
            value={duration}
            onChange={e => setDuration(e.target.value)}
            required
          />
        </div>
        <div>
          <label className="block mb-1">Ссылка на аудио (необязательно)</label>
          <input
            className="w-full bg-dark-700 p-2 rounded"
            placeholder="/audio/sample1.mp3"
            value={audioUrl}
            onChange={e => setAudioUrl(e.target.value)}
          />
        </div>
        {error && <p className="text-red-500">{error}</p>}
        <button
          type="submit"
          disabled={loading}
          className="bg-orange-500 px-6 py-2 rounded font-bold hover:bg-orange-400 disabled:opacity-50"
        >
          {loading ? 'Сохранение...' : 'Сохранить'}
        </button>
      </form>
    </div>
  );
}