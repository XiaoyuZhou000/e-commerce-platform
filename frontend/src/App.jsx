import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Products from './pages/Products';
import Cart from './pages/Cart';
import Orders from './pages/Orders';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <main>
          <Routes>
            <Route path="/" element={<Products />} />
            <Route path="/products" element={<Products />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/orders" element={<Orders />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App

