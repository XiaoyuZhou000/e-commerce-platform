# E-Commerce Platform - Frontend

Frontend application for the E-Commerce Platform built with React and Vite.

## Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and development server
- **React Router** - Client-side routing
- **Axios** - HTTP client for API requests

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

The production build will be in the `dist` directory.

### Preview Production Build

```bash
npm run preview
```

## Project Structure

```
frontend/
├── public/          # Static assets
├── src/
│   ├── components/  # React components
│   ├── pages/       # Page components
│   ├── services/    # API services
│   ├── utils/       # Utility functions
│   ├── App.jsx      # Main App component
│   ├── main.jsx     # Entry point
│   └── index.css    # Global styles
├── index.html       # HTML template
├── vite.config.js   # Vite configuration
└── package.json     # Dependencies and scripts
```

## API Configuration

The frontend is configured to proxy API requests to the backend. Update the proxy target in `vite.config.js` if your backend runs on a different port.

## Development

- The development server supports hot module replacement (HMR)
- API calls are proxied to `http://localhost:8080` by default
- Update the proxy configuration in `vite.config.js` as needed

## License

See the LICENSE file in the repository root.

