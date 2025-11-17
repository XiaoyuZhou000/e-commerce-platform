# E-Commerce-Platform

## Problem

The primary objective of this system is to simulate an e-commerce shopping platform similar to Amazon. Due to time constraints, the system will focus primarily on core functionalities related to ordering, including order placement, inventory management, and order tracking. Secondary features, such as payment processing, will be implemented using simplified approaches.

## Project Structure

```
e-commerce-platform/
├── backend/
│   └── e-commerce-platform/  # Java backend (AWS Lambda)
│       ├── src/
│       │   └── main/
│       │       └── java/     # Java source code
│       ├── pom.xml           # Maven configuration
│       └── template.yaml     # AWS SAM template
├── frontend/                 # React frontend
│   ├── src/                  # React source code
│   ├── package.json          # npm dependencies
│   └── vite.config.js        # Vite configuration
├── README.md                 # This file
└── LICENSE
```

## Getting Started

### Backend

The backend is a Java application built with Maven and deployed as AWS Lambda functions.

1. Navigate to the backend directory:
```bash
cd backend/e-commerce-platform
```

2. Build the project:
```bash
mvn clean package
```

3. See `backend/e-commerce-platform/deploy.md` for deployment instructions.

### Frontend

The frontend is a React application built with Vite.

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

See `frontend/README.md` for more details.

## Technologies

### Backend
- Java
- AWS Lambda
- AWS SAM
- Maven
- MySQL (Database)
- Redis (Caching)

### Frontend
- React 18
- Vite
- React Router
- Axios
- CSS3

## License

See the LICENSE file for details.
