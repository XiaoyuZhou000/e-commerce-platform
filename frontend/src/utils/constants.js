// API Configuration
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:18080';

// Application Constants
export const APP_NAME = 'E-Commerce Platform';

// Route Constants
export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  CART: '/cart',
  ORDERS: '/orders',
};

// Status Messages
export const STATUS = {
  SUCCESS: 'success',
  ERROR: 'error',
  LOADING: 'loading',
};

