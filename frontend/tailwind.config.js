/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'system-ui', 'sans-serif'],
      },
      colors: {
        navy:       { DEFAULT: '#1A1F36', 2: '#252B45', 3: '#2F3553' },
        gold:       { DEFAULT: '#F5A623', deep: '#C97F12', soft: '#FEF1DC' },
        cream:      { DEFAULT: '#FAF7F1', line: '#E8E2D4' },
        ink:        { DEFAULT: '#1A1F36', dim: '#5C6178', mute: '#8A8FA3' },
        ok: {
          success:  '#00C48C',
          danger:   '#FF4D4F',
          info:     '#1890FF',
          warning:  '#FAAD14',
        },
      },
    },
  },
  plugins: [],
};
