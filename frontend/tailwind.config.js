/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        navy:  '#1A1F36',
        gold: {
          DEFAULT: '#F5A623',
          deep:    '#D48A1E',
          soft:    '#FEF3D7',
        },
        cream: {
          DEFAULT: '#FAF8F3',
          line:    '#E8E2D4',
        },
        ink: {
          dim:  '#6B7280',
          mute: '#9CA3AF',
        },
        ok: {
          danger:  '#FF4D4F',
          success: '#00C48C',
        },
      },
      fontFamily: {
        sans: ['Inter', 'Segoe UI', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Mono', 'ui-monospace', 'monospace'],
      },
    },
  },
  plugins: [],
};
