function updateStatus() {
    const orderId = document.getElementById("orderId").value;
    const status = document.getElementById("status").value;
  
    fetch("http://localhost:40109/admin", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ orderId, status, action: "updateOrderStatus" }),
    })
      .then((response) => response.json())
      .then((data) => {
        alert(data.message || "Order status updated successfully!");
      })
      .catch((error) => console.error("Error:", error));
  }
  function logout() {
    sessionStorage.clear();
    window.location.href = "adminlogin.html";
  }
  