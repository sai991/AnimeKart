// Fetch orders from the backend
fetch("http://localhost:40109/admin", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ action: "fetchOrders" }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json(); // Parse JSON from the response
    })
    .then((data) => {
      console.log("Order Data:", data);
      if (data.status === "success") {
        displayOrders(data.orders); // Call the display function with the orders
      } else {
        alert("Failed to fetch orders: " + (data.message || "Unknown error"));
      }
    })
    .catch((error) => console.error("Error fetching orders:", error));
  
  // Function to display orders on the page
  function displayOrders(orders) {
    const ordersContainer = document.getElementById("ordersContainer");
    ordersContainer.innerHTML = ""; // Clear previous content
  
    if (orders.length === 0) {
      ordersContainer.innerHTML = "<p>No orders found.</p>";
      return;
    }
  
    orders.forEach((order) => {
      // Create a container for each order
      const orderDiv = document.createElement("div");
      orderDiv.className = "order-item";
  
      // Populate order details
      orderDiv.innerHTML = `
        <p><strong>Order ID:</strong> ${order.orderId}</p>
        <p><strong>User ID:</strong> ${order.userId}</p>
        <p><strong>Item Name:</strong> ${order.itemName}</p>
        <p><strong>Price:</strong> $${order.price}</p>
        <p><strong>Status:</strong> ${order.status}</p>
        <p><strong>Timestamp:</strong> ${order.timestamp}</p>
        <label for="statusSelect_${order.orderId}">Change Status:</label>
        <select id="statusSelect_${order.orderId}" class="status-select">
          <option value="Pending" ${order.status === "Pending" ? "selected" : ""}>Pending</option>
          <option value="Shipped" ${order.status === "Shipped" ? "selected" : ""}>Shipped</option>
          <option value="Delivered" ${order.status === "Delivered" ? "selected" : ""}>Delivered</option>
        </select>
        <button onclick="updateOrderStatus(${order.orderId})">Update</button>
      `;
  
      ordersContainer.appendChild(orderDiv); // Add to the page
    });
  }
  
  // Function to update the status of an order
  function updateOrderStatus(orderId) {
    const statusSelect = document.getElementById(`statusSelect_${orderId}`);
    const newStatus = statusSelect.value;
  
    fetch("http://localhost:40109/admin", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ action: "updateOrderStatus", orderId, status: newStatus }),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        alert(data.message || "Order status updated successfully!");
        if (data.status === "success") {
          statusSelect.value = newStatus; // Update UI if successful
        }
      })
      .catch((error) => console.error("Error updating order status:", error));

      function logout() {
        console.log("Logout clicked. Clearing session and redirecting to login.");
        sessionStorage.clear();
        window.location.href = "adminlogin.html";
      }
  }
  