/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        military: {
          50:  '#f4f6f2',
          100: '#e3e8de',
          200: '#c8d3bf',
          300: '#a3b596',
          400: '#7d9470',
          500: '#4a5c3f',
          600: '#3d4e34',
          700: '#31402a',
          800: '#263220',
          900: '#1a2416',
        },
      },
      fontFamily: {
        sans: ['"Noto Sans KR"', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
