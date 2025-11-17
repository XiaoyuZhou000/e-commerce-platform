import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { cartApi, productApi, orderApi } from '../services/api';
import './Cart.css';

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [products, setProducts] = useState({});
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState({});
  const [placingOrder, setPlacingOrder] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const response = await cartApi.getCart();
      const items = response.data || [];
      setCartItems(items);

      // Fetch product details for cart items
      if (items.length > 0) {
        const productIds = items.map(item => item.productID);
        const productsResponse = await productApi.getProductsByIds(productIds);
        const productsData = {};
        (productsResponse.data || []).forEach(product => {
          productsData[product.productID] = product;
        });
        setProducts(productsData);
      }
    } catch (error) {
      console.error('Error fetching cart:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (productID, newQuantity) => {
    if (newQuantity < 1) {
      handleRemoveItem(productID);
      return;
    }

    try {
      setUpdating(prev => ({ ...prev, [productID]: true }));
      await cartApi.updateCartItem({ productID, quantity: newQuantity });
      await fetchCart();
    } catch (error) {
      console.error('Error updating cart:', error);
      alert('Failed to update item. Please try again.');
    } finally {
      setUpdating(prev => ({ ...prev, [productID]: false }));
    }
  };

  const handleRemoveItem = async (productID) => {
    try {
      setUpdating(prev => ({ ...prev, [productID]: true }));
      await cartApi.removeFromCart({ productID });
      await fetchCart();
    } catch (error) {
      console.error('Error removing item:', error);
      alert('Failed to remove item. Please try again.');
    } finally {
      setUpdating(prev => ({ ...prev, [productID]: false }));
    }
  };

  const handlePlaceOrder = async () => {
    if (cartItems.length === 0) {
      alert('Your cart is empty.');
      return;
    }

    try {
      setPlacingOrder(true);
      const response = await orderApi.placeOrder(cartItems);
      const orderResponse = Array.isArray(response.data) ? response.data[0] : response.data;
      
      if (orderResponse.code === 0) {
        alert(`Order placed successfully! Order ID: ${orderResponse.orderID}`);
        await cartApi.clearCart();
        navigate('/orders');
      } else {
        alert(`Failed to place order: ${orderResponse.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error placing order:', error);
      alert('Failed to place order. Please try again.');
    } finally {
      setPlacingOrder(false);
    }
  };

  const calculateSubtotal = () => {
    return cartItems.reduce((total, item) => {
      const product = products[item.productID];
      if (product) {
        return total + (product.price || 0) * (item.quantity || 0);
      }
      return total;
    }, 0);
  };

  const subtotal = calculateSubtotal();
  const tax = subtotal * 0.1; // 10% tax
  const total = subtotal + tax;

  if (loading) {
    return (
      <div className="cart-container">
        <div className="loading">Loading cart...</div>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h1>Shopping Cart</h1>
        {cartItems.length > 0 && (
          <p className="cart-item-count">
            {cartItems.length} {cartItems.length === 1 ? 'item' : 'items'}
          </p>
        )}
      </div>

      {cartItems.length === 0 ? (
        <div className="empty-cart">
          <div className="empty-cart-content">
            <h2>Your Amazon Cart is empty</h2>
            <p>Shop today's deals</p>
            <Link to="/products" className="shop-button">
              Shop Products
            </Link>
          </div>
        </div>
      ) : (
        <div className="cart-content">
          <div className="cart-items">
            {cartItems.map((item) => {
              const product = products[item.productID];
              if (!product) return null;

              return (
                <div key={item.productID} className="cart-item">
                  <div className="cart-item-image">
                    <div className="product-image-placeholder">
                      <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <rect x="3" y="3" width="18" height="18" rx="2"></rect>
                        <path d="M9 9h6v6H9z"></path>
                      </svg>
                    </div>
                  </div>
                  <div className="cart-item-details">
                    <h3 className="cart-item-title">{product.name}</h3>
                    <p className="cart-item-category">{product.category}</p>
                    <div className="cart-item-price">
                      <span className="price-symbol">$</span>
                      <span className="price-amount">{product.price?.toFixed(2)}</span>
                    </div>
                    <div className="cart-item-actions">
                      <div className="quantity-controls">
                        <label>Qty:</label>
                        <button
                          className="quantity-button"
                          onClick={() => handleUpdateQuantity(item.productID, (item.quantity || 1) - 1)}
                          disabled={updating[item.productID]}
                        >
                          −
                        </button>
                        <span className="quantity-value">{item.quantity || 1}</span>
                        <button
                          className="quantity-button"
                          onClick={() => handleUpdateQuantity(item.productID, (item.quantity || 1) + 1)}
                          disabled={updating[item.productID]}
                        >
                          +
                        </button>
                      </div>
                      <button
                        className="delete-button"
                        onClick={() => handleRemoveItem(item.productID)}
                        disabled={updating[item.productID]}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                  <div className="cart-item-total">
                    <span className="price-symbol">$</span>
                    <span className="price-amount">
                      {((product.price || 0) * (item.quantity || 1)).toFixed(2)}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="cart-summary">
            <div className="summary-card">
              <h2>Order Summary</h2>
              <div className="summary-row">
                <span>Subtotal ({cartItems.length} {cartItems.length === 1 ? 'item' : 'items'}):</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="summary-row">
                <span>Tax:</span>
                <span>${tax.toFixed(2)}</span>
              </div>
              <div className="summary-divider"></div>
              <div className="summary-row total">
                <span>Total:</span>
                <span>${total.toFixed(2)}</span>
              </div>
              <button
                className="place-order-button"
                onClick={handlePlaceOrder}
                disabled={placingOrder}
              >
                {placingOrder ? 'Placing Order...' : 'Proceed to Checkout'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Cart;

