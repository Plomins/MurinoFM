import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getTrackById } from '../services/api';
import AudioPlayer from '../components/AudioPlayer';

export default function TrackDetailPage() {
  const { id } = useParams();
  const [track, setTrack] = useState(null);

  useEffect(() => { load(); }, [id]);

  const load = async () => {
    const res = await getTrackById(id);
    setTrack(res.data);
  };

  if (!track) return <div>Загрузка...</div>;

  return (
    <div>
      <h2 className="text-3xl font-bold mb-4">{track.title}</h2>
      <p>Длительность: {track.durationSeconds} сек.</p>
      {track.album && (
        <p>Альбом: {track.album.title} (ID: {track.album.id})</p>
      )}
      {track.audioUrl && <AudioPlayer src={track.audioUrl} />}
    </div>
  );
}