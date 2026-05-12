export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        orange: { 400: '#FF9F0A', 500: '#FF7B00' },
        dark: { 900: '#121212', 800: '#181818', 700: '#282828' }
      }
      animation: {
              'spin-slow': 'spin 3s linear infinite',
              'pulse-fast': 'pulse 0.5s cubic-bezier(0.4, 0, 0.6, 1) infinite',
              'equalizer': 'equalizer 1s ease-in-out infinite',
            },
             keyframes: {
                    equalizer: {
                      '0%, 100%': { height: '5px' },
                      '50%': { height: '15px' },
                    }
    }
  },
  plugins: [],
};