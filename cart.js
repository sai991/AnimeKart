console.log("Cart.js Loaded");

const cartUrl = "http://localhost:40109/shopping";

document.addEventListener("DOMContentLoaded", () => {
  fetchCartData();

  // Place order event
  document.getElementById("placeOrder").addEventListener("click", () => {
    placeOrder();
  });
});

// Fetch cart details
function fetchCartData() {
  const userId = sessionStorage.getItem("USERID");
  if (!userId) {
    alert("You are not logged in. Redirecting to login page.");
    window.location.href = "login.html";
    return;
  }

  const requestData = {
    userId: userId,
    action: "fetchCart",
  };

  fetch(cartUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => response.json())
    .then((data) => {
        console.log("Cart Data:", data);
      const cartItemsDiv = document.getElementById("cartItems");
      const cartTotalSpan = document.getElementById("cartTotal");

      cartItemsDiv.innerHTML = ""; // Clear previous items
      let total = 0;

      data.items.forEach((item) => {
        const itemDiv = document.createElement("div");
        itemDiv.className = "cart-item";
        itemDiv.innerHTML = `
          <div class="item-info">
            <span>${item.itemName}</span>
            <span>$${item.price}</span>
          </div>
          <button class="delete-btn" onclick="deleteCartItem(${item.cartId})">❌</button>
        `;
        cartItemsDiv.appendChild(itemDiv);
        total += parseFloat(item.price);
      });

      cartTotalSpan.textContent = total.toFixed(2);
    })
    .catch((error) => console.error("Error fetching cart data:", error));
}

// Delete an item from the cart
function deleteCartItem(cartId) {
  const userId = sessionStorage.getItem("USERID");

  const requestData = {
    userId: userId,
    cartId: cartId,
    action: "deleteCartItem",
  };
  console.log("Deleting cart item with ID:", cartId);


  fetch(cartUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.status === "success") {
        alert("Item deleted from cart.");
        fetchCartData(); // Refresh cart
      } else {
        console.error("Error deleting cart item:", data.message);
      }
    })
    .catch((error) => console.error("Error deleting cart item:", error));
}

// Place an order
function placeOrder() {
  const userId = sessionStorage.getItem("USERID");

  const requestData = {
    userId: userId,
    action: "placeOrder",
  };

  fetch(cartUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(requestData),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.status === "success") {
        alert("Order placed successfully!");
        fetchCartData(); // Clear the cart
      } else {
        console.error("Error placing order:", data.message);
      }
    })
    .catch((error) => console.error("Error placing order:", error));
}
