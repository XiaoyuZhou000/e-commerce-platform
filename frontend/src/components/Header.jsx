import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { cartApi } from '../services/api';
import './Header.css';

const Header = () => {
  const [cartItemCount, setCartItemCount] = useState(0);

  useEffect(() => {
    const fetchCartCount = async () => {
      try {
        const response = await cartApi.getCart();
        const items = response.data || [];
        const totalItems = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
        setCartItemCount(totalItems);
      } catch (error) {
        console.error('Error fetching cart:', error);
      }
    };

    fetchCartCount();
    // Refresh cart count every 5 seconds
    const interval = setInterval(fetchCartCount, 5000);
    return () => clearInterval(interval);
  }, []);

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          <span className="logo-text">Shopping</span>
        </Link>

        <div className="header-search">
          {/* <input
            type="text"
            className="search-input"
            placeholder="Search products..."
          />
          <button className="search-button">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.35-4.35"></path>
            </svg>
          </button> */}
        </div>

        <nav className="header-nav">
          <Link to="/orders" className="nav-link">
            <span className="nav-link-line1">Returns</span>
            <span className="nav-link-line2">& Orders</span>
          </Link>
          <Link to="/cart" className="nav-link cart-link">
            <div className="cart-icon-container">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                <line x1="3" y1="6" x2="21" y2="6"></line>
                <path d="M16 10a4 4 0 0 1-8 0"></path>
              </svg>
              {cartItemCount > 0 && (
                <span className="cart-count">{cartItemCount}</span>
              )}
            </div>
            <span className="nav-link-line2">Cart</span>
          </Link>
        </nav>
      </div>
    </header>
  );
};

export default Header;

