// src/components/ToastContainer.jsx
import { useToast } from '../contexts/ToastContext';

const typeStyles = {
  success: 'bg-green-600 border-green-400',
  error: 'bg-red-600 border-red-400',
  info: 'bg-blue-600 border-blue-400',
};

export default function ToastContainer() {
  const { toasts, removeToast } = useToast();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-[100] flex flex-col gap-2 max-w-xs w-full">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className={`${typeStyles[toast.type] || typeStyles.info} border text-white px-4 py-3 rounded-lg shadow-lg flex items-start justify-between animate-slide-in`}
        >
          <span className="text-sm">{toast.message}</span>
          <button
            onClick={() => removeToast(toast.id)}
            className="ml-3 text-white opacity-80 hover:opacity-100"
          >
            ✕
          </button>
        </div>
      ))}
    </div>
  );
}