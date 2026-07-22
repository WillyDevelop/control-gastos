/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        canvas: '#F8FAFC',
        surface: '#FFFFFF',
        sidebar: '#FFFFFF',
        brand: {
          primary: '#6366F1',
          'primary-light': '#EEF2FF',
          dark: '#0F172A'
        },
        extended: {
          'teal-positive': '#14B8A6',
          'coral-negative': '#F43F5E',
          'orange-alert': '#FFEDD5',
          'purple-accent': '#6D28D9',
          'gray-muted': '#64748B',
          'gray-border': '#E2E8F0',
          'gray-light-bg': '#F1F5F9'
        },
        slate: {
          50: '#fafafa',
          100: '#f4f4f5',
          200: '#e4e4e7',
          300: '#d1d5db',
          350: '#a1a1aa',
          400: '#8f9099',
          450: '#71717a',
          500: '#71717a',
          600: '#3f3f46',
          700: '#1f1f24',
          750: '#18181b',
          800: '#121214',
          850: '#0b0b0d',
          900: '#08080a',
          950: '#050507',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      borderRadius: {
        interactive: '8px',
        card: '12px',
        badge: '9999px'
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
