import { useState, useEffect } from 'react';
import { productApi, cartApi } from '../services/api';
import './Products.css';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addingToCart, setAddingToCart] = useState({});

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await productApi.getAllProducts();
      setProducts(response.data || []);
      setError(null);
    } catch (err) {
      setError('Failed to load products. Please try again later.');
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productID, quantity = 1) => {
    try {
      setAddingToCart(prev => ({ ...prev, [productID]: true }));
      await cartApi.addToCart({ productID, quantity });
      // Show success message (you could add a toast notification here)
      alert('Item added to cart!');
    } catch (err) {
      console.error('Error adding to cart:', err);
      alert('Failed to add item to cart. Please try again.');
    } finally {
      setAddingToCart(prev => ({ ...prev, [productID]: false }));
    }
  };

  if (loading) {
    return (
      <div className="products-container">
        <div className="loading">Loading products...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="products-container">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className="products-container">
      <div className="products-header">
        <h1>Products</h1>
        <p className="products-count">{products.length} products available</p>
      </div>

      <div className="products-grid">
        {products.map((product) => (
          <div key={product.productID} className="product-card">
            <div className="product-image">
              <div className="product-image-placeholder">
                <svg width="120" height="120" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <rect x="3" y="3" width="18" height="18" rx="2"></rect>
                  <path d="M9 9h6v6H9z"></path>
                </svg>
              </div>
            </div>
            <div className="product-info">
              <h3 className="product-title">{product.name || 'Product Name'}</h3>
              <p className="product-category">{product.category || 'Uncategorized'}</p>
              <div className="product-rating">
                <span className="stars">★★★★★</span>
                <span className="rating-count">(0)</span>
              </div>
              <div className="product-price">
                <span className="price-symbol">$</span>
                <span className="price-amount">{product.price?.toFixed(2) || '0.00'}</span>
              </div>
              {product.quantityAvailable !== undefined && (
                <p className="product-stock">
                  {product.quantityAvailable > 0 
                    ? `In Stock (${product.quantityAvailable} available)`
                    : 'Out of Stock'}
                </p>
              )}
              {product.description && (
                <p className="product-description">{product.description}</p>
              )}
              <button
                className="add-to-cart-button"
                onClick={() => handleAddToCart(product.productID, 1)}
                disabled={addingToCart[product.productID] || (product.quantityAvailable !== undefined && product.quantityAvailable === 0)}
              >
                {addingToCart[product.productID] ? 'Adding...' : 'Add to Cart'}
              </button>
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="no-products">
          <p>No products available at the moment.</p>
        </div>
      )}
    </div>
  );
};

export default Products;

