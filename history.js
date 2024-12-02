console.log("History.js Loaded");

const historyUrl = "http://localhost:40109/shopping";

// Fetch order history on page load
document.addEventListener("DOMContentLoaded", () => {
  fetchOrderHistory();
});

function fetchOrderHistory() {
  const userId = sessionStorage.getItem("USERID");
  if (!userId) {
    alert("You are not logged in. Redirecting to login page.");
    window.location.href = "login.html";
    return;
  }

  const requestData = {
    userId: userId,
    action: "fetchOrderHistory",
  };

  fetch(historyUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => response.json())
    .then((data) => {
      const orderHistoryDiv = document.getElementById("orderHistory");

      if (data.length > 0) {
        data.forEach((order) => {
          const orderDiv = document.createElement("div");
          orderDiv.className = "order-item";
          orderDiv.innerHTML = `
            <p><strong>Item:</strong> ${order.itemName}</p>
            <p><strong>Price:</strong> $${order.price}</p>
            <p><strong>Status:</strong> ${order.status}</p>
            <p><strong>Order Date:</strong> ${order.timestamp}</p>
          `;
          orderHistoryDiv.appendChild(orderDiv);
        });
      } else {
        orderHistoryDiv.innerHTML = "<p>You have no order history.</p>";
      }
    })
    .catch((error) => console.error("Error fetching order history:", error));
}
