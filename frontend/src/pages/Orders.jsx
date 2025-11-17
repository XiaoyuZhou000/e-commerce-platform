import { useState, useEffect } from 'react';
import { orderApi, productApi } from '../services/api';
import './Orders.css';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedOrder, setExpandedOrder] = useState(null);
  const [products, setProducts] = useState({});

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const response = await orderApi.getAllOrders();
      const ordersData = response.data || [];
      setOrders(ordersData);

      // Fetch all unique product IDs from orders
      const productIds = new Set();
      ordersData.forEach(order => {
        if (order.items) {
          order.items.forEach(item => {
            productIds.add(item.productID);
          });
        }
      });

      if (productIds.size > 0) {
        const productsResponse = await productApi.getProductsByIds(Array.from(productIds));
        const productsData = {};
        (productsResponse.data || []).forEach(product => {
          productsData[product.productID] = product;
        });
        setProducts(productsData);
      }
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const toggleOrderDetails = (orderID) => {
    setExpandedOrder(expandedOrder === orderID ? null : orderID);
  };

  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'processing':
        return '##002b76';
      case 'out for delivery':
        return '#007600';
      case 'delivered':
        return '#007600';
      default:
        return '#565959';
    }
  };

  if (loading) {
    return (
      <div className="orders-container">
        <div className="loading">Loading orders...</div>
      </div>
    );
  }

  return (
    <div className="orders-container">
      <div className="orders-header">
        <h1>Your Orders</h1>
        <p className="orders-count">
          {orders.length} {orders.length === 1 ? 'order' : 'orders'} placed
        </p>
      </div>

      {orders.length === 0 ? (
        <div className="empty-orders">
          <div className="empty-orders-content">
            <h2>You haven't placed any orders yet</h2>
            <p>Start shopping to see your orders here</p>
          </div>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <div key={order.orderID} className="order-card">
              <div className="order-header">
                <div className="order-header-left">
                  <div className="order-info-row">
                    <span className="order-label">Order Placed</span>
                    <span className="order-value">
                      {order.orderDate 
                        ? new Date(order.orderDate).toLocaleDateString('en-US', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric'
                          })
                        : 'N/A'}
                    </span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Total</span>
                    <span className="order-value">
                      ${order.grandTotal?.toFixed(2) || '0.00'}
                    </span>
                  </div>
                  <div className="order-info-row">
                    <span className="order-label">Order #</span>
                    <span className="order-value">{order.orderID}</span>
                  </div>
                </div>
                <div className="order-header-right">
                  <div className="order-status" style={{ color: getStatusColor(order.status) }}>
                    {order.status || 'Pending'}
                  </div>
                </div>
              </div>

              <div className="order-items">
                {order.items && order.items.length > 0 ? (
                  <>
                    {order.items.slice(0, expandedOrder === order.orderID ? order.items.length : 3).map((item, index) => {
                      const product = products[item.productID];
                      return (
                        <div key={index} className="order-item">
                          <div className="order-item-image">
                            <div className="product-image-placeholder">
                              <svg width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <rect x="3" y="3" width="18" height="18" rx="2"></rect>
                                <path d="M9 9h6v6H9z"></path>
                              </svg>
                            </div>
                          </div>
                          <div className="order-item-details">
                            <h3 className="order-item-title">
                              {product?.name || `Product ID: ${item.productID}`}
                            </h3>
                            <p className="order-item-category">{product?.category || ''}</p>
                            <p className="order-item-quantity">Quantity: {item.quantity || 1}</p>
                            {product && (
                              <p className="order-item-price">
                                ${product.price?.toFixed(2)} each
                              </p>
                            )}
                          </div>
                        </div>
                      );
                    })}
                    {order.items.length > 3 && (
                      <button
                        className="view-details-button"
                        onClick={() => toggleOrderDetails(order.orderID)}
                      >
                        {expandedOrder === order.orderID
                          ? 'Show Less'
                          : `View ${order.items.length - 3} more item${order.items.length - 3 > 1 ? 's' : ''}`}
                      </button>
                    )}
                  </>
                ) : (
                  <p className="no-items">No items in this order</p>
                )}
              </div>

              <div className="order-actions">
                <button className="order-action-button">Track Package</button>
                <button className="order-action-button">View Order Details</button>
                {order.status?.toLowerCase() !== 'cancelled' && (
                  <button className="order-action-button">Cancel Order</button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;

