console.log("Cart.js Loaded");

// URL for interacting with the server
const cartUrl = "http://localhost:40109/shopping";

// Fetch cart details on page load
document.addEventListener("DOMContentLoaded", () => {
  fetchCartData();
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
      const cartItemsDiv = document.getElementById("cartItems");
      const cartTotalSpan = document.getElementById("cartTotal");

      cartItemsDiv.innerHTML = ""; // Clear previous items
      let total = 0;

      if (data.items && data.items.length > 0) {
        data.items.forEach((item) => {
          const itemDiv = document.createElement("div");
          itemDiv.className = "cart-item";
          itemDiv.innerHTML = `
            <span>${item.itemName}</span>
            <span>$${item.price}</span>
          `;
          cartItemsDiv.appendChild(itemDiv);
          total += parseFloat(item.price);
        });

        cartTotalSpan.textContent = total.toFixed(2);
      } else {
        // Display a message when the cart is empty
        cartItemsDiv.innerHTML = `<p>Your cart is empty.</p>`;
        cartTotalSpan.textContent = "0.00";
      }
    })
    .catch((error) => {
      console.error("Error fetching cart data:", error);
      alert("An error occurred while fetching the cart data.");
    });
}
