## E-commerce Backend API Endpoints

- Base URL: `{BASE_URL}` (API Gateway/Lambda deployment URL)
- Content-Type: `application/json` for all requests and responses
- CORS: Responses include `Access-Control-Allow-Origin: *` and standard headers

### Error format

```json
{ "error": "Message describing the error" }
```

---

## Products

### GET /api/products
- Description: Retrieve all products.
- Request: No body
- Response: 200 OK

```json
[
  {
    "productID": 1,
    "name": "string",
    "description": "string",
    "price": 0,
    "category": "string",
    "quantityAvailable": 0
  }
]
```

- Errors:
  - 500 Error retrieving products

### POST /api/product/id
- Description: Retrieve multiple products by IDs.
- Request: array of ProductRequest

```json
[ { "productID": 1 }, { "productID": 2 } ]
```

- Response: 200 OK

```json
[
  {
    "productID": 1,
    "name": "string",
    "description": "string",
    "price": 0,
    "category": "string",
    "quantityAvailable": 0
  }
]
```

- Errors:
  - 400 Request body is required
  - 400 At least one Product ID is required
  - 400 Product ID is required (for invalid items)
  - 404 Product not found
  - 500 Error retrieving product

---

## Cart

Cart item schema:
```json
{ "productID": 1, "quantity": 1 }
```

### GET /api/cart/get
- Description: Retrieve current cart items.
- Request: No body
- Response: 200 OK

```json
[ { "productID": 1, "quantity": 2 } ]
```

- Errors:
  - 500 Error retrieving cart

### POST /api/cart/add
- Description: Add an item to the cart.
- Request: CartRequest

```json
{ "productID": 1, "quantity": 2 }
```

- Response:
  - 200 OK: "Item added to cart successfully"
  - 400 Failed to add item to cart

- Errors:
  - 400 Request body is required
  - 400 Product ID and quantity are required
  - 500 Error adding to cart

### POST /api/cart/revise
- Description: Update quantity for an item in the cart.
- Request: CartRequest

```json
{ "productID": 1, "quantity": 3 }
```

- Response:
  - 200 OK: "Item added to cart successfully" (message mirrors implementation)
  - 400 Failed to update cart item

- Errors:
  - 400 Request body is required
  - 400 Product ID and quantity are required
  - 500 Error updating cart item

### POST /api/cart/remove
- Description: Remove an item from the cart.
- Request:

```json
{ "productID": 1 }
```

- Response:
  - 200 OK: "Item removed from cart successfully"
  - 400 Failed to remove item from cart

- Errors:
  - 400 Request body is required
  - 400 Product ID is required
  - 500 Error removing from cart

### DELETE /api/cart/clear
- Description: Clear the cart.
- Request: No body
- Response:
  - 200 OK: "Cart cleared successfully"
  - 400 Failed to clear cart

- Errors:
  - 500 Error clearing cart

---

## Orders

### POST /api/orders/place
- Description: Place an order from cart items.
- Request: OrderRequest

```json
{
  "cartItems": [ { "productID": 1, "quantity": 2 } ]
}
```

- Response:
  - 200 OK (on success): array with single OrderResponse

```json
[
  {
    "code": 0,
    "message": "string",
    "orderID": 123,
    "status": "string",
    "grandTotal": 0
  }
]
```

  - 400 Bad Request with body as JSON stringified array of OrderResponse (when `code` != 0)

- Errors:
  - 400 Request body is required
  - 400 Cart items are required
  - 500 Error placing order

### GET /api/orders/get/{orderID}
- Description: Retrieve order by ID.
- Request: No body
- Response: 200 OK

```json
{
  "orderID": 123,
  "orderDate": "YYYY-MM-DDThh:mm:ssZ",
  "status": "string",
  "grandTotal": 0,
  "items": [ { "productID": 1, "quantity": 2 } ]
}
```

- Errors:
  - 404 Order not found
  - 500 Error retrieving order

### GET /api/orders/getAll
- Description: Retrieve all orders.
- Request: No body
- Response: 200 OK

```json
[
  {
    "orderID": 123,
    "orderDate": "YYYY-MM-DDThh:mm:ssZ",
    "status": "string",
    "grandTotal": 0,
    "items": [ { "productID": 1, "quantity": 2 } ]
  }
]
```

- Errors:
  - 500 Error retrieving orders

---

## Notes
- All endpoints return JSON and include permissive CORS headers.
- Replace `{BASE_URL}` with your deployed API Gateway invoke URL.
- Some response messages are simple strings (from cart operations) per implementation.
*** End Patch

