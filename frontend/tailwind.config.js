/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        canvas: '#FBF5EE',
        surface: '#FFFDFC',
        sidebar: '#F7EEE3',
        brand: {
          primary: '#0A0F23',
          'primary-light': '#F3E3D3',
          dark: '#0A0F23'
        },
        extended: {
          'teal-positive': '#0A0F23',
          'coral-negative': '#16214A',
          'orange-alert': '#FAE4CF',
          'purple-accent': '#1B2858',
          'gray-muted': '#786F66',
          'gray-border': '#E8D8C8',
          'gray-light-bg': '#F5EBDD'
        },
        slate: {
          50: '#fff8f2',
          100: '#fae4cf',
          200: '#f5e5d5',
          300: '#edd9c7',
          350: '#d9c6b4',
          400: '#c4b3a2',
          450: '#a89481',
          500: '#5067a3',
          600: '#243573',
          700: '#1b2858',
          750: '#16214a',
          800: '#121b3d',
          850: '#0e1530',
          900: '#0a0f23',
          950: '#060a18',
        },
        navy: {
          DEFAULT: '#0a0f23',
          950: '#060a18',
          900: '#0a0f23',
          850: '#0e1530',
          800: '#121b3d',
          750: '#16214a',
          700: '#1b2858',
          600: '#243573',
        },
        beige: {
          DEFAULT: '#fae4cf',
          50: '#fff8f2',
          100: '#fae4cf',
          200: '#f5e5d5',
          300: '#edd9c7',
          400: '#d9c6b4',
          500: '#c4b3a2',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      borderRadius: {
        sm: '4px',
        DEFAULT: '7px',
        md: '7px',
        lg: '7px',
        xl: '7px',
        '2xl': '7px',
        '3xl': '7px',
        interactive: '7px',
        card: '7px',
        badge: '7px',
        'brutal-btn': '7px',
        'brutal-card': '7px',
        'brutal-sharp': '7px'
      },
      borderWidth: {
        thin: '1px',
        focus: '2px'
      },
      boxShadow: {
        subtle: '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px -1px rgba(0, 0, 0, 0.05)'
      }
    },
  },
  plugins: [],
}
