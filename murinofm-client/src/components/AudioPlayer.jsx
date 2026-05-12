import { useRef, useState } from 'react';

export default function AudioPlayer({ src }) {
  const audioRef = useRef(null);
  const [playing, setPlaying] = useState(false);

  const togglePlay = () => {
    if (playing) {
      audioRef.current.pause();
    } else {
      audioRef.current.play();
    }
    setPlaying(!playing);
  };

  return (
    <div className="flex items-center gap-4 mt-4">
      <button
        onClick={togglePlay}
        className="bg-orange-500 px-4 py-2 rounded-full text-lg font-bold"
      >
        {playing ? '⏸ Пауза' : '▶ Играть'}
      </button>
      <audio ref={audioRef} src={src} onEnded={() => setPlaying(false)} />
    </div>
  );
}