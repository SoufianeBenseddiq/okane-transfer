/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [ "./src/**/*.{html,ts}",],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'sans-serif'],
        mono: ['IBM Plex Mono', 'monospace'],
      },
      colors: {
        brand: {
          50:  '#fff8ed', 100:'#ffefd4', 200:'#ffdba8', 300:'#ffc170',
          400: '#ff9d36', 500:'#f97d10', 600:'#ea6006', 700:'#c24807',
          800: '#9a380e', 900:'#7c300f',
        },
        ink: {
          900:'#0d1117', 800:'#151b23', 700:'#1c2430', 600:'#242d3a',
          500:'#2d3848', 400:'#3d4d61', 300:'#5a6e85', 200:'#8096ae',
          100:'#b0c0d0', 50:'#dde6ef',
        },
        teal: { DEFAULT:'#0dd4b8', dark:'#0aaa93' },
        danger: '#ff4d6d',
      },
      boxShadow: {
        glow: '0 0 20px rgba(249,125,16,0.25)',
        card: '0 4px 24px rgba(0,0,0,0.3)',
      },
    },
  },
  plugins: [],
}

