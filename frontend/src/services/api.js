import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:18080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Product API
export const productApi = {
  getAllProducts: () => api.get('/api/products'),
  getProductsByIds: (ids = []) =>
    api.post('/api/product/id', ids.map((productID) => ({ productID }))),
};

// Cart API
export const cartApi = {
  getCart: () => api.get('/api/cart/get'),
  addToCart: ({ productID, quantity }) => api.post('/api/cart/add', { productID, quantity }),
  updateCartItem: ({ productID, quantity }) =>
    api.post('/api/cart/revise', { productID, quantity }),
  removeFromCart: ({ productID }) => api.post('/api/cart/remove', { productID }),
  clearCart: () => api.delete('/api/cart/clear'),
};

// Order API
export const orderApi = {
  placeOrder: (cartItems) => api.post('/api/orders/place', { cartItems }),
  getOrderById: (orderId) => api.get(`/api/orders/get/${orderId}`),
  getAllOrders: () => api.get('/api/orders/getAll'),
};

export default api;

